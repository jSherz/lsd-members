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

import dao.SettingsDAO
import models.{Member, Setting}
import org.scalatest.Matchers._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Tests the SettingsDAO.
  */
class SettingsDAOSpec extends BaseSpec {
  /**
    * An instance of the DAO, used for testing.
    */
  private val settingsDao = app.injector.instanceOf[SettingsDAO]

  /**
    * An example setting, used for testing.
    */
  private val testSetting = Setting("foo-bar", "some value :)")

  "SettingsDAO" should {
    "returns false if a setting does not exist" in {
      settingsDao.exists("foo-bar").futureValue shouldBe false
      settingsDao.exists("test-123").futureValue shouldBe false
    }

    "return true if a setting does exist" in {
      settingsDao.exists(testSetting.key).futureValue shouldBe false
      settingsDao.put(testSetting).futureValue
      settingsDao.exists(testSetting.key).futureValue shouldBe true
    }

    "return the correct value for a setting" in {
      settingsDao.put(testSetting).futureValue
      settingsDao.exists(testSetting.key).futureValue shouldBe true

      settingsDao.get(testSetting.key).futureValue shouldEqual Some(testSetting)
    }

    "return None if the setting does not exist" in {
      settingsDao.exists(testSetting.key).futureValue shouldBe false
      settingsDao.get(testSetting.key).futureValue shouldBe empty
    }

    "return the current value for an existing setting, even if a default value is given" in {
      settingsDao.put(testSetting).futureValue
      settingsDao.exists(testSetting.key).futureValue shouldBe true

      settingsDao.getOrElse(testSetting.key, "not-the-correct-value").futureValue shouldEqual testSetting.value
    }

    "return the default value for a setting if one was specified and the setting does not exist" in {
      settingsDao.exists(testSetting.key).futureValue shouldBe false
      val defaultValue = "a-default-value"

      settingsDao.getOrElse(testSetting.key, defaultValue).futureValue shouldEqual defaultValue
    }

    "insert a new settings with the correct value" in {
      settingsDao.exists(testSetting.key).futureValue shouldBe false
      settingsDao.put(testSetting).futureValue

      settingsDao.get(testSetting.key).futureValue shouldBe Some(testSetting)
    }

    "update a setting with a new value, even if it exists" in {
      val oldTestSetting = Setting(testSetting.key, "an-old-value")
      settingsDao.put(oldTestSetting).futureValue
      settingsDao.put(testSetting).futureValue

      settingsDao.get(testSetting.key).futureValue shouldBe Some(testSetting)
    }

    "erase all settings correctly" in {
      val testSettings = Seq(
        Setting("test-key-1", "test-value-1"),
        Setting("test-key-2", "test-value-2"),
        Setting("test-key-3", "test-value-3"),
        Setting("test-key-4", "test-value-4"),
        Setting("test-key-5", "test-value-5")
      )

      testSettings.map(settingsDao.put(_).futureValue)
      testSettings.map(setting => settingsDao.exists(setting.key).futureValue shouldBe true)

      settingsDao.empty().futureValue

      testSettings.map(setting => settingsDao.exists(setting.key).futureValue shouldBe false)
    }
  }
}
