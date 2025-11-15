//> using scala 3.7.3
//> using dep io.github.lego-eden::bearlyb::0.1.2

import bearlyb as bl

@main
def squareRandomizer(): Unit =
  bl.init(bl.Init.Video)

  val (width, height, moveDelta, randomDelta) = (800, 600, 10, 2)
  val (pink, purple) = (242, 128, 161, 0) -> (153, 102, 204, 0)

  val (window, renderer) = bl
    .createWindowAndRenderer("hello bearlyb!", width, height)

  var running = true
  var x = width / 2
  var y = height / 2
  var count = 1
  val measureTimeEvery = 2000
  println("HELLO bearlyb! Press Q to quit. Press Arrow Keys to move rectangle.")
  var t0 = System.nanoTime()

  while running do
    for e <- bl.Event.pollEvents() do
      e match
        case bl.Event.Quit(_) | bl.Event.Key.Down(key = bl.Keycode.Q) =>
          println("quitting")
          running = false
        case bl.Event.Key.Down(key = bl.Keycode.Right) => x += moveDelta
        case bl.Event.Key.Down(key = bl.Keycode.Left)  => x -= moveDelta
        case bl.Event.Key.Down(key = bl.Keycode.Up)    => y -= moveDelta
        case bl.Event.Key.Down(key = bl.Keycode.Down)  => y += moveDelta
        case other                                     => println(other)
    end for
    x += util.Random.nextInt(2 * randomDelta + 1) - randomDelta
    y += util.Random.nextInt(2 * randomDelta + 1) - randomDelta
    x = math.floorMod(x, width)
    y = math.floorMod(y, height)
    renderer.drawColor = pink
    renderer.clear()
    renderer.drawColor = purple
    renderer.fillRect(bl.Rect(x, y, width / 8, width / 8))
    renderer.present()
    if (count % measureTimeEvery) == 0 then
      val time = System.nanoTime() - t0
      val fps = 1e9 / time
      println(s"Time between frames: ${time / 1e9} ns")
      println(s"Frames per second  : ${(fps + 10).round / 10.0} fps")
    end if
    t0 = System.nanoTime()
    count += 1
  end while

  bl.quit()
end squareRandomizer
