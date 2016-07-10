package dragisak.workday

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.{FormData, HttpMethods, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpcirce.CirceSupport
import io.circe.syntax._
import io.circe.generic.auto._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object Main extends App with CirceSupport {

  val config = ConfigFactory.load()

  val twitterKey = config.getString("twitter.key")
  val twitterSecret = config.getString("twitter.secret")

  implicit val system = ActorSystem("reactive-tweets", config)
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  val log = Logging(system, "Main")

  try {
    val f = for {
      allProjects <- getGithubProjects
      projects = allProjects.take(10)
      bearer <- getBearer(
        key = twitterKey,
        secret = twitterSecret
      )
      tweets <- Future.sequence(
        projects.map(p =>
          searchTweets(bearer, p.full_name)
            .map(tw => ProjectTweets(p, tw))
        )
      )
      json = tweets.asJson
    } yield json

    f.foreach(j => println(j.spaces4))

    f.recover {
      case t => t.printStackTrace()
    }

    Await.ready(f, 10.seconds)
  } finally {
    Http().shutdownAllConnectionPools().onComplete { _ =>
      Await.ready(system.terminate(), 10.seconds)
    }
  }

  private def getGithubProjects = {
    log.debug("Calling GitHub")
    for {
      response <- Http().singleRequest(HttpRequest(uri = "https://api.github.com/search/repositories?q=reactive"))
      searchResults <- Unmarshal(response.entity).to[GitHubResponse]
    } yield searchResults.items
  }

  private def getBearer(key: String, secret: String) = {
    log.debug("Calling Twitter oauth with {}, {}", key, secret)
    for {
      response <- Http()
        .singleRequest(
          HttpRequest(uri = "https://api.twitter.com/oauth2/token")
            .withMethod(HttpMethods.POST)
            .withHeaders(Authorization(BasicHttpCredentials(key, secret)))
            .withEntity(FormData("grant_type" -> "client_credentials").toEntity)
        )

      bearer <- Unmarshal(response.entity).to[Bearer]
    } yield OAuth2BearerToken(bearer.access_token)
  }

  private def searchTweets(bearer: OAuth2BearerToken, search: String) = {

    log.debug("Searching twitter for {}", search)
    for {
      response <- Http().singleRequest(
        HttpRequest(uri = s"https://api.twitter.com/1.1/search/tweets.json?q=$search")
          .withHeaders(Authorization(bearer))
      )
      searchResults <- Unmarshal(response.entity).to[TwitterSearchResult]
    } yield searchResults.statuses
  }
}
