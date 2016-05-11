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

package controllers

import javax.inject._

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

/**
 * Handles the administration interface.
 */
@Singleton
class AdminController @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {
  /**
    * Shows the main admin screen.
    */
  def index: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.admin.index())
  }

  /**
    * Shows the settings edit form.
    *
    * @return
    */
  def settings: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.admin.settings())
  }
}
