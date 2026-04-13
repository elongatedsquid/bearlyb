// ---------------- Text Rendering Example ----------------
import bearlyb as bl, bl.{Event, Init, Keycode, Rect}
import bl.render.Font

@main
def textTest(): Unit =
  bl.init(Init.Video)

  val (window, renderer) =
    bearlyb.createWindowAndRenderer("Hello Text!", 450, 250)

  val textSize = 48f
  val dpi = 96
  val font = Font.default
    .withTextSize(textSize)
    .withDPI(dpi)
  // val font = Font.defaultBoldItalic
  // val font = Font.fromFile(os.resource/"fonts"/"PlaywriteDKUloopetGuides-Regular.ttf")

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

    val text = "Hello, World!"
    val (textWidth, textHeight) = font.measure(text)

    // center the text
    val (w, h) = window.size
    val (x, y) = (w/2 - (textWidth/2), h/2 - (textHeight/2))

    renderer.drawColor = (0, 0, 255, 255)
    renderer.renderText(font, text, x, y)

    renderer.present()
  end while

  font.destroy()

  bl.quit()
end textTest
