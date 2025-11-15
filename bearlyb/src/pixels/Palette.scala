package bearlyb.pixels

import bearlyb.pixels.Color.*
import org.lwjgl.sdl.SDLPixels.*
import org.lwjgl.sdl.{SDL_Color, SDL_Palette}
import org.lwjgl.system.MemoryStack.stackPush

import scala.util.Using

case class Palette(colors: Color*):

  private[bearlyb] def internal: SDL_Palette = Using(stackPush()): stack =>
    val palette = SDL_CreatePalette(colors.size)
    val internals = colors.map(_.internal(stack))
    val buf = SDL_Color.calloc(colors.size, stack)
    internals.foreach(c => buf.put(c))
    SDL_SetPaletteColors(palette, buf, 0)
    palette
  .get

end Palette
