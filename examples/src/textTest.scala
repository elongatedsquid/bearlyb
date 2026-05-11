import bearlyb as bl, bl.{Event, Init, Keycode, Rect}
import bl.render.Font
import bl.video.BlendMode

@main
def textTest(): Unit =
  bl.init(Init.Video)

  val (window, renderer) =
    bearlyb.createWindowAndRenderer("Hello Text!", 400, 80)

  val font = Font.default
    .withTextSize(50)

  var running = true
  while running do
    Event.pollEvents().foreach {
      case Event.Quit(_) | Event.Key.Down(key = Keycode.Escape) =>
        println("Quitting")
        running = false
      case _ =>
    }

    renderer.drawColor = (255, 255, 255, 255)
    renderer.clear()

    val text = "Hello, bearlyb!"
    val (textWidth, textHeight) = font.measure(text)

    // center the text
    val (w, h) = window.size
    val (x, y) = (w/2 - (textWidth/2), h/2 - (textHeight/2))

    // draw the text to the screen
    renderer.drawColor = (0, 0, 0, 255)
    renderer.renderText(font, text, x, y) 

    renderer.present()
  end while

  font.destroy()

  bl.quit()
end textTest
