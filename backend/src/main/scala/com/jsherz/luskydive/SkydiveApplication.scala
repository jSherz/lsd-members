/**
  * MIT License
  *
  * Copyright (c) 2016 James Sherwood-Jones <james.sherwoodjones@gmail.com>
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

package com.jsherz.luskydive

import com.datasift.dropwizard.scala.ScalaApplication
import com.datasift.dropwizard.scala.jdbi.JDBI
import com.jsherz.luskydive.dao.MemberDAOImpl
import com.jsherz.luskydive.db.Members
import com.jsherz.luskydive.resources.SignupResource
import io.dropwizard.setup.{Bootstrap, Environment}

/**
  * The main setup point for the whole application.
  */
object SkydiveApplication extends ScalaApplication[SkydiveConfiguration] {

  /**
    * Called to bootstrap components & CLI commands.
    *
    * @param bootstrap
    */
  override def init(bootstrap: Bootstrap[SkydiveConfiguration]): Unit = super.init(bootstrap)

  /**
    * Sets up the database and adds all resources.
    *
    * @param configuration
    * @param environment
    */
  override def run(configuration: SkydiveConfiguration, environment: Environment): Unit = {
    // DB

    val db = JDBI(environment, configuration.getDataSourceFactory(), "postgres")

    val members: Members = db.onDemand[Members](classOf[Members])

    // DAOs

    val memberDAO = MemberDAOImpl(members)

    // Resources

    val signupResource = new SignupResource(memberDAO)

    environment.jersey().register(signupResource)
  }

}
