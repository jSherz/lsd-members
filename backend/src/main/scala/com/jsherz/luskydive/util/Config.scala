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

package com.jsherz.luskydive.util

import com.typesafe.config.ConfigFactory

/**
  * The application configuration, as read from resources/application.conf.
  */
trait Config {

  private val config = ConfigFactory.load()
  private val configHttp = config.getConfig("http")
  private val configDb = config.getConfig("database")
  private val configTwilio = config.getConfig("twilio")
  private val fb = config.getConfig("fb")

  val interface: String = configHttp.getString("interface")
  val port: Int = configHttp.getInt("port")

  val dbUrl: String = configDb.getString("url")
  val dbUsername: String = configDb.getString("username")
  val dbPassword: String = configDb.getString("password")

  val textMessageReceiveApiKey: String = config.getString("text_message_receive_api_key")

  val twilioAccountSid: String = configTwilio.getString("account_sid")
  val twilioAuthToken: String = configTwilio.getString("auth_token")
  val twilioMessagingServiceSid: String = configTwilio.getString("messaging_service_sid")

  val fbAppId: String = fb.getString("app_id")
  val fbSecret: String = fb.getString("secret")
  val loginReturnUrl: String = fb.getString("login_return_url")

  val jwtSecret: String = config.getConfig("jwt").getString("secret")

}
