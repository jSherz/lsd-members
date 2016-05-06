package services

import java.util
import javax.inject.Inject

import com.twilio.sdk.TwilioRestClient
import com.twilio.sdk.resource.factory.MessageFactory
import com.twilio.sdk.resource.instance.Message
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import models.Member
import net.greghaines.jesque.{Config, ConfigBuilder}
import net.greghaines.jesque.worker.{MapBasedJobFactory, Worker, WorkerImpl}
import net.greghaines.jesque.utils.JesqueUtils._

import scala.collection.JavaConverters._

/**
  * Handles texting new members if they signed up with a phone number.
  */
object MemberSignupWorker {
  private val ACCOUNT_SID: String = ""
  private val AUTH_TOKEN: String = ""
  private val FROM: String = ""

  private val twilioRestClient = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN)

  private val config: Config = new ConfigBuilder().build()

  private val classMap: Map[String, Class[services.MemberSignupAction]] = Map(("MemberSignup", classOf[MemberSignupAction]))

  private val jobFactory: MapBasedJobFactory = new MapBasedJobFactory(classMap.asJava)

  private val worker: Worker = new WorkerImpl(config, util.Arrays.asList("membersignup"), jobFactory)

  private val workerThread: Thread = new Thread(worker)

  workerThread.start()

  private def sendSMS(name: String, phoneNumber: String) {
    val params = new util.ArrayList[NameValuePair]()
    params.add(new BasicNameValuePair("Body", s"Hey, ${name}! Thanks for coming to see Leeds Skydivers!"))
    params.add(new BasicNameValuePair("To", phoneNumber))
    params.add(new BasicNameValuePair("From", FROM))

    val messageFactory: MessageFactory = twilioRestClient.getAccount().getMessageFactory
    val message: Message = messageFactory.create(params)

    Console.println(s"Message sid: ${message.getSid}")
  }

  /**
    * Called by the queue worker listener.
    *
    * @param name
    * @param phoneNumber
    */
  def signupMember(name: String, phoneNumber: String) {
    if (phoneNumber != null && phoneNumber != "") {
      sendSMS(name, phoneNumber)
    } else {
      Console.println("Member doesn't have phone number - not sending message.")
    }
  }

  def main(args: Array[String]): Unit = {

  }
}

class MemberSignupAction extends Runnable {
  private var name: String = null

  private var phoneNumber: String = null

  private var email: String = null

  def setName(name: String): Unit = {
    this.name = name
  }

  def setPhoneNumber(phoneNumber: String): Unit = {
    this.phoneNumber = phoneNumber
  }

  def setEmail(email: String): Unit = {
    this.email = email
  }

  override def run(): Unit = {
    MemberSignupWorker.signupMember(name, phoneNumber)
  }
}
