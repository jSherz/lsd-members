package services

/**
  * Created by james on 07/05/16.
  */
object Queues extends Enumeration {
  /**
    * This is an Enumeration of Strings.
    */
  type Queues = String

  /**
    * The queue for members entering their contact information.
    */
  val SIGNUP = "membersignup";

  /**
    * The action that the worker will process.
    */
  val SIGNUP_ACTION = "MemberSignup";
}
