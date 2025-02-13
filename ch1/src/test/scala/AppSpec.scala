import zio._
import zio.test._

// console를 테스트해보세요
// https://zio.dev/reference/test/
// https://zio.dev/reference/test/services/console/

object AppSpec extends ZIOSpecDefault {

  override def spec = suite("App")(
    test("console test") {
      assertTrue(true)
    },
    test("your test") {
      for {
        _ <- TestConsole.feedLines("Kim")
        _ <- App.prog
        lines <- TestConsole.output
        _ <- Console.printLine(lines)
      } yield assertTrue(lines == Vector("Please enter your name: ", "Hello, Kim!\n"))
    }
  )

}
// https://zio.dev/reference/test/services/console/
