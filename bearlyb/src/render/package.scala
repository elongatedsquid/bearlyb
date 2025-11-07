package bearlyb.render

import bearlyb.video.Window
import org.lwjgl.sdl.SDLRender.*
import bearlyb.video.Window.Flag.combine
import bearlyb.util.*

def createWindowAndRenderer(
    title: String,
    width: Int,
    height: Int,
    windowFlags: IterableOnce[Window.Flag] = Nil
  ): (window: Window, renderer: Renderer) = withStack:
  val window   = stack.mallocPointer(1)
  val renderer = stack.mallocPointer(1)
  val flags    = windowFlags.combine
  SDL_CreateWindowAndRenderer(title, width, height, flags, window, renderer)
    .sdlErrorCheck()
  (new Window(window.get(0)), new Renderer(renderer.get(0)))
