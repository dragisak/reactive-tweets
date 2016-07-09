package dragisak.workday

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpcirce.CirceSupport
import io.circe.generic.auto._

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App with CirceSupport {

  val config = ConfigFactory.load()

  implicit val system = ActorSystem("reactive-tweets", config)
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  try {
    val f = for {
      response <- Http().singleRequest(HttpRequest(uri = "https://api.github.com/search/repositories?q=reactive"))
      response <- Unmarshal(response.entity).to[GitHubResponse]
     } yield println(response)

    f.recover {
      case t => t.printStackTrace()
    }

    Await.ready(f, 10.seconds)
  } finally {
    Http().shutdownAllConnectionPools().onComplete { _ =>
      Await.ready(system.terminate(), 10.seconds)
    }
  }

}
