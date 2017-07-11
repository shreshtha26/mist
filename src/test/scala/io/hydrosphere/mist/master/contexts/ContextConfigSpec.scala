package io.hydrosphere.mist.master.contexts

import com.typesafe.config.{ConfigFactory, ConfigRenderOptions}
import org.scalatest._

import scala.concurrent.duration._

class ContextConfigSpec extends FunSpec with Matchers {

  it("should parse from config") {

    val cfg = ConfigFactory.parseString(
      """
        |{
        |  spark-conf {
        |    x = "z"
        |    a = 1
        |  }
        |  downtime = Inf
        |  max-parallel-jobs = 100
        |  precreated = false
        |  run-options = "--key"
        |  streaming-duration = 30 seconds
        |
        |}
      """.stripMargin
    )

    val context = ContextConfig.fromConfig("test", cfg)
    context.sparkConf shouldBe Map("x" -> "z", "a" -> "1")
    context.downtime shouldBe Duration.Inf
    context.maxJobs shouldBe 100
    context.precreated shouldBe false
    context.runOptions shouldBe "--key"
    context.streamingDuration shouldBe 30.seconds
  }

  it("should render to raw") {
    val context = ContextConfig(
      name = "foo",
      sparkConf = Map("x" -> "y", "z" -> "4"),
      downtime = 10.minutes,
      maxJobs = 10,
      precreated = false,
      runOptions = "--key",
      streamingDuration = 1.minutes
    )
//    val options = ConfigRenderOptions.defaults()
//      .setComments(false)
//      .setOriginComments(false)
//      .setJson(false)
//      .setFormatted(true)
//    println(context.toRaw.root.render(options))

    val raw = context.toRaw
    raw.getInt("downtime") shouldBe 60 * 1000 * 10
    raw.getInt("max-parallel-jobs") shouldBe 10
    raw.getBoolean("precreated") shouldBe false
    raw.getString("spark-conf.x") shouldBe "y"
    raw.getString("spark-conf.z") shouldBe "4"
    raw.getInt("streaming-duration") shouldBe 60 * 1000
  }

}
