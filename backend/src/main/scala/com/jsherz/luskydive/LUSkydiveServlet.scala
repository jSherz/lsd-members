package com.jsherz.luskydive

import org.scalatra._

class LUSkydiveServlet extends LeedsSkydiversStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }

}
