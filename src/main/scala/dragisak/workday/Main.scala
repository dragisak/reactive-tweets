package dragisak.workday

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpcirce.CirceSupport
import io.circe.Json

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App with CirceSupport {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  try {
    val f = for {
      response <- Http().singleRequest(HttpRequest(uri = "https://api.github.com/search/repositories?q=reactive"))
      json <- Unmarshal(response.entity).to[Json]
    } yield println(json.spaces4)

    Await.ready(f, 10.seconds)
  } finally {
    Http().shutdownAllConnectionPools().onComplete { _ =>
      Await.ready(system.terminate(), 10.seconds)
    }
  }

}
