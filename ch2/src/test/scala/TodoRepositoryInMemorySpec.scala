import zio.Scope
import zio.test.{Gen, Spec, TestEnvironment, ZIOSpecDefault}

object TodoRepositoryInMemorySpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("TodoRepositoryInMemorySpec"){
      test("Create todo") {

      },
      test("si thread safe") {
        Gen.listOfN(10)(Gen.alphaNumericStringBounded(2, 5))
      }
    }
}
