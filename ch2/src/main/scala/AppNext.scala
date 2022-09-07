import zio._
import zhttp.http._
import zio.json.{
  DeriveJsonCodec,
  DeriveJsonEncoder,
  EncoderOps,
  JsonCodec,
  JsonEncoder
}
import zhttp.service.Server

final case class Todo(id: Int, task: String)

object Todo {
  val todoList: List[Todo] =
    List(Todo(1, "hi"), Todo(2, "hello"), Todo(3, "bye"))
  implicit val encoder: JsonEncoder[Todo] = DeriveJsonEncoder.gen[Todo]
  implicit val listEncoder: JsonEncoder[List[Todo]] =
    DeriveJsonEncoder.gen[List[Todo]]
}

class TodoRepositoryInMemory(todo: Ref[Chunk[Todo]]) {
  def listAll: Task[Chunk[Todo]] = todo.get

  def create(title: String): Task[Todo] = todo.modify { list =>
    val newId = list.length + 1
    val newTodo = Todo(newId, title)
    (newTodo, list :+ newTodo)
  }
}
object TodoRepositoryInMemory {
  val make = for {
    ref <- Ref.make(Chunk.empty[Todo])
  } yield new TodoRepositoryInMemory(ref)

  val layer: ZLayer[Any, Nothing, TodoRepositoryInMemory] = ZLayer {
    make
  }
}

case class TodoForm(title: String)
object TodoForm {
  implicit val todoFormJsonCodec: JsonCodec[TodoForm] =
    DeriveJsonCodec.gen[TodoForm]
}

import zio.json._

class AppNext(repo: TodoRepositoryInMemory) {
  // Create HTTP route
  val app: HttpApp[Any, Throwable] = Http.collectZIO[Request] {
    case Method.GET -> !! / "todo" / "list" =>
      repo.listAll.map { list =>
        Response.json(list.toJson)
      }
    case req @ Method.POST -> !! / "todo" =>
      for {
        body <- req.bodyAsString
        form <- ZIO
          .from(body.fromJson[TodoForm])
          .mapError(msg => new Exception(s"Invalid form data: $msg"))
        newTodo <- repo.create(form.title)
      } yield Response.json(newTodo.toJson)
  }
}
object AppNext {
  val layer: ZLayer[TodoRepositoryInMemory, Nothing, AppNext] = ZLayer {
    for {
      repo <- TodoRepositoryInMemory.layer
    } yield new AppNext(repo)
  }
}

object Main extends ZIOAppDefault {

  override val run =
    (for {
      app <- ZIO.service[AppNext]
      _ <- Server.start(8080, app.app)
    } yield ()).provide(
      TodoRepositoryInMemory.layer,
      AppNext.layer
    )
}
