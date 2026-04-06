// ---------------- Text Rendering Example ----------------
import bearlyb as bl, bl.{Event, Init, Keycode, Rect}
import bearlyb.vectors.given
import org.lwjgl.util.freetype.*
import org.lwjgl.util.harfbuzz.*
import org.lwjgl.BufferUtils
import bl.render.Renderer
import bl.pixels.Color

import java.nio.file.{Files, Paths}
import java.nio.ByteBuffer
import scala.collection.mutable
import java.awt.image.BufferedImage
import bl.vectors.Vec
import bl.pixels.PixelFormat
import bl.render.TextureAccess
import bl.pixels.RawColor
import scala.util.Using
import bl.render.Font

@main
def textTest(): Unit =
  bl.init(Init.Video)

  val (window, renderer) =
    bearlyb.createWindowAndRenderer("Hello Text!", 960, 540)

  val font = Font.defaultBoldItalic

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

    val text = "gello jorld!"
    val textSize = 48
    val dpi = 96
    val (textWidth, textHeight) = font.measure(text, textSize, dpi)

    val (w, h) = window.size
    val (x, y) = (w/2 - (textWidth/2), h/2 - (textHeight/2))

    renderer.drawColor = (255, 0, 255, 255)
    renderer.fillRect(Rect(x, y, textWidth, textHeight))

    renderer.drawColor = (0, 0, 0, 255)
    renderer.renderText(
      font, text, x, y,
      textSize,
      dpi = dpi
    )

    renderer.present()
  end while

  font.destroy()

  bl.quit()
end textTest
