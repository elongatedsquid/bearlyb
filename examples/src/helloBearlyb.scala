import bearlyb as bl, bl.{Event, Init, Keycode, Rect}

@main
def helloBearlyb(): Unit =
  bl.init(Init.Video)

  val (window, renderer) = bearlyb
    .createWindowAndRenderer("hello bearlyb!", 800, 600)

  var running = true
  while running do
    Event
      .pollEvents()
      .foreach:
        case Event.Quit(_) | Event.Key.Down(key = Keycode.Q) =>
          println("quitting")
          running = false
        case other => println(other)

    renderer.drawColor = (255, 255, 0, 0)
    renderer.clear()
    renderer.drawColor = (0, 0, 0, 0)
    renderer.fillRect(Rect(200, 200, 100, 100))
    renderer.present()
  end while

  bl.quit()
end helloBearlyb
