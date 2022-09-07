import sttp.client3.{HttpClientSyncBackend, UriContext, basicRequest}
import sttp.model.StatusCode
import zio.json.EncoderOps
import zio.test.{assertTrue, _}

// 서버를 테스트해보세요
// https://zio.dev/reference/test/
// https://sttp.softwaremill.com/en/latest/quickstart.html
// https://sttp.softwaremill.com/en/latest/backends/zio.html

object AppSpec extends ZIOSpecDefault {
  override def spec: Spec[Any, Nothing] = suite("App")(
    test("request test") {
      assertTrue(true)
    },
    test("test list todo") {
      val request = basicRequest.get(uri"http://localhost:8080/todo/list")
      val backend = HttpClientSyncBackend()
      val response = request.send(backend)
      assertTrue(response.code == StatusCode.Ok) &&
        assertTrue(response.body.getOrElse("") == App.todos.toJson)
    },
    test ("test get todo") {
      val request = basicRequest.get(uri"http://localhost:8080/todo/1")
      val backend = HttpClientSyncBackend()
      val response = request.send(backend)
      assertTrue(response.code == StatusCode.Ok) &&
        assertTrue(response.body.getOrElse("") == App.todos(1).toJson)
    }
  )
}
