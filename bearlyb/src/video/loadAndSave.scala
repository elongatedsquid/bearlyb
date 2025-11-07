package bearlyb.video

import org.lwjgl.sdl.SDL_Surface
import org.lwjgl.sdl.SDLSurface.*
import org.lwjgl.sdl.SDLPixels.*
import org.lwjgl.sdl.SDL_Texture
import org.lwjgl.sdl.SDLRender.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.sdl.SDLIOStream.*
import org.lwjgl.sdl.SDLStdinc.SDL_free
import org.lwjgl.system.MemoryUtil.{memCopy, memAddress}
import org.lwjgl.system.MemoryStack.stackPush
import scala.util.Using
import bearlyb.util.{mallocManyInt, sdlCreationCheck}
import bearlyb.vectors.Vec.given

private[bearlyb] object imghelper:

  def loadSurface(file: String): Option[SDL_Surface] =
    Using(stackPush()): stack =>
      val (w, h, channels) = mallocManyInt(3, stack)
      Option(stbi_load(file, w, h, channels, 4)) match
        case Some(pixels) =>
          val surf =
            SDL_CreateSurface(w.get(0), h.get(0), SDL_PIXELFORMAT_RGBA8888)
              .sdlCreationCheck()
          memCopy(pixels, surf.pixels)
          stbi_image_free(pixels)
          Some(surf)
        case None => None
      end match
    .get

  def loadTexture(file: String, renderer: Long): Option[SDL_Texture] =
    for
      surf <- loadSurface(file)
      tex  <- Option(SDL_CreateTextureFromSurface(renderer, surf))
    yield
      SDL_DestroySurface(surf)
      tex

  def loadGIF(file: String): Option[
    (
        w: Int,
        h: Int,
        n: Int,
        frames: IndexedSeq[(surface: SDL_Surface, delay: Int)]
      )
  ] = Using(stackPush()): stack =>
    val buf                    = SDL_LoadFile(file).sdlCreationCheck()
    val pDelays                = stack.mallocPointer(1)
    val (pw, ph, pn, channels) = mallocManyInt(4, stack)
    val result                 = Option(
      stbi_load_gif_from_memory(buf, pDelays, pw, ph, pn, channels, 4)
    ).map: pixels =>
      val delays    = pDelays.getIntBuffer(0)
      val (w, h, n) = (pw, ph, pn).vmap(_.get(0))

      inline val fmt = SDL_PIXELFORMAT_RGBA8888
      val frameSize  = w * h * SDL_GetPixelFormatDetails(fmt).bytes_per_pixel()

      val frames =
        for i <- 0 until n yield
          val delay = delays.get(i)
          val surf  = SDL_CreateSurface(w, h, fmt).sdlCreationCheck()
          memCopy(
            memAddress(pixels) + i * frameSize, memAddress(surf.pixels),
            frameSize
          )
          (surf, delay)

      stbi_image_free(pixels)

      (w, h, n, frames)
    SDL_free(buf)
    result
  .get

end imghelper
