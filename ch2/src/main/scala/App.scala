import zhttp.http._
import zhttp.service.Server
import zio._
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, EncoderOps, JsonDecoder, JsonEncoder}

// web server를 만들어 보세요
// https://github.com/dream11/zio-http/blob/main/example/src/main/scala/example/HelloWorld.scala

object App extends ZIOAppDefault {
  case class Todo(id: Long, title: String)
  object Todo {
    implicit val decoder: JsonDecoder[Todo] = DeriveJsonDecoder.gen[Todo]
    implicit val encoder: JsonEncoder[Todo] = DeriveJsonEncoder.gen[Todo]
  }

  val todos: Map[Long, Todo] =
    Map(1L -> Todo(1, "task1"), 2L -> Todo(2, "task2"), 3L -> Todo(3, "task3"))

  def app: Http[Any, Nothing, Request, Response] = Http.collect[Request] {
    case Method.GET -> !! / "todo" / "list" => Response.json(todos.toJson)
    case Method.GET -> !! / "todo" / long(item) =>
      Response.json(todos(item).toJson)
  }

  def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Nothing] =
    Server.start(8080, app)
}
