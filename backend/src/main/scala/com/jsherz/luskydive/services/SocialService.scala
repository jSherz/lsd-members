/**
  * MIT License
  *
  * Copyright (c) 2016-2018 James Sherwood-Jones <james.sherwoodjones@gmail.com>
  *
  * Permission is hereby granted, free of charge, to any person obtaining a copy
  * of this software and associated documentation files (the "Software"), to deal
  * in the Software without restriction, including without limitation the rights
  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  * copies of the Software, and to permit persons to whom the Software is
  * furnished to do so, subject to the following conditions:
  *
  * The above copyright notice and this permission notice shall be included in all
  * copies or substantial portions of the Software.
  *
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  * SOFTWARE.
  */

package com.jsherz.luskydive.services

import java.io.IOException

import com.jsherz.luskydive.core.FBSignedRequest
import com.jsherz.luskydive.util.FbClientFactory
import com.restfb.FacebookClient.AccessToken
import com.restfb.exception.FacebookException
import com.restfb.json.{JsonException, JsonObject}
import com.restfb.types.User
import com.restfb.{FacebookClient, Parameter}
import org.slf4j.{Logger, LoggerFactory}
import scalaz.{-\/, \/, \/-}

trait SocialService {

  val fbClientNotReady = "fb client not ready"

  def parseSignedRequest(signedRequest: String): Option[FBSignedRequest]

  def getUserAccessToken(verificationCode: String): \/[String, AccessToken]

  def getUserForAccessToken(accessToken: String): \/[String, User]

  def getNameAndEmail(userId: String): \/[String, (String, String, String)]

  def getProfilePhoto(userId: String): \/[String, String]

  def ready: Boolean

}

/**
  * No, this service does not help with societal issues.
  * <p>
  * It's for social website authentication.
  */
class SocialServiceImpl(fbClientFactory: FbClientFactory, appId: String, appSecret: String, loginReturnUrl: String)
  extends SocialService {

  private var fbClient: Option[FacebookClient] = None

  private val log: Logger = LoggerFactory.getLogger(getClass)

  synchronized {
    val fbTokenThread = new Thread(() => {
      try {
        log.info("Obtaining FB access token...")

        fbClient = Some(fbClientFactory.forAppIdAndSecret(appId, appSecret))

        log.info(s"FB init complete")
      } catch {
        case ex: Exception => log.error("failed to init FB client", ex)
      }
    })
    fbTokenThread.setName("GetFbToken")
    fbTokenThread.start()
  }

  override def parseSignedRequest(signedRequest: String): Option[FBSignedRequest] = try {
    synchronized {
      fbClient match {
        case Some(client) => Some(client.parseSignedRequest(signedRequest, appSecret, classOf[FBSignedRequest]))
        case None =>
          log.error(fbClientNotReady)
          None
      }
    }
  } catch {
    case ex: FacebookException =>
      log.error("Failed to parse FB signed request.", ex)

      None
  }

  /**
    * Use a verification code to request an access token for a user.
    *
    * @param verificationCode Sent as a query param to our login return URL
    * @return
    */
  override def getUserAccessToken(verificationCode: String): \/[String, AccessToken] = try {
    synchronized {
      fbClient match {
        case Some(client) => \/-(client.obtainUserAccessToken(appId, appSecret, loginReturnUrl, verificationCode))
        case None =>
          log.error(fbClientNotReady)
          -\/(fbClientNotReady)
      }
    }
  } catch {
    case ex: FacebookException =>
      log.error(s"Failed to get a user access token for verification code '${verificationCode}'.", ex)
      -\/(ex.getMessage)
  }

  /**
    * Get a user's information using only an access token.
    *
    * @param accessToken Access token granted for a user
    * @return
    */
  override def getUserForAccessToken(accessToken: String): \/[String, User] = try {
    val userAuthdFbClient = fbClientFactory.forAccessToken(accessToken)

    \/-(userAuthdFbClient.fetchObject("/me", classOf[User], Parameter.`with`("fields", "id,first_name,last_name,name,email")))
  } catch {
    case ex: FacebookException =>
      log.error("Failed to get user for access token.", ex)

      -\/(ex.getMessage)
  }

  /**
    * Lookup the name & e-mail of an FB user.
    *
    * @param userId User
    * @return (first, last, email)
    */
  override def getNameAndEmail(userId: String): \/[String, (String, String, String)] = {
    try {
      synchronized {
        fbClient match {
          case Some(client) =>
            val user: User = client.fetchObject(userId, classOf[User],
              Parameter.`with`("fields", "id,first_name,last_name,name,email"))

            log.info(s"Welcome '${user.getName}' to LSD!")

            \/-((user.getFirstName, user.getLastName, user.getEmail))
          case None =>
            log.error(fbClientNotReady)
            -\/(fbClientNotReady)
        }
      }
    } catch {
      case ex: JsonException =>
        val msg = "Failed to parse FB response for user's name & e-mail."
        log.error(msg, ex)
        -\/(msg)

      case ex: IOException =>
        val msg = "Failed to request user's name & e-mail - network error."
        log.error(msg, ex)
        -\/(msg)
    }
  }

  /**
    * Download a user's profile picture.
    *
    * @param userId FB user ID
    * @return URL to profile picture
    */
  override def getProfilePhoto(userId: String): \/[String, String] = {
    try {
      synchronized {
        fbClient match {
          case Some(client) =>
            val picture: JsonObject = client.fetchObject(
              s"$userId/picture",
              classOf[JsonObject],
              Parameter.`with`("redirect", "false"),
              Parameter.`with`("height", "160"),
              Parameter.`with`("width", "160")
            )

            \/-(picture.getString("data.url"))
          case None =>
            log.error(fbClientNotReady)
            -\/(fbClientNotReady)
        }
      }
    } catch {
      case ex: JsonException =>
        val msg = "Failed to parse FB response for user profile picture."
        log.error(msg, ex)
        -\/(msg)

      case ex: IOException =>
        val msg = "Failed to request user profile picture - network error."
        log.error(msg, ex)
        -\/(msg)
    }
  }

  override def ready: Boolean = synchronized {
    fbClient.isDefined
  }

}
