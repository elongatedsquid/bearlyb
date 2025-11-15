package bearlyb.pixels

import bearlyb.vectors.Vec.given
import org.lwjgl.sdl.SDL_Color
import org.lwjgl.system.MemoryStack

type Color = (r: Int, g: Int, b: Int, a: Int)
type FColor = (r: Float, g: Float, b: Float, a: Float)

object Color:

  extension (c: Color)

    private[bearlyb] def internal(stack: MemoryStack): SDL_Color =
      val (r, g, b, a) = c.toTuple.vmap(_.toByte)
      SDL_Color.malloc(stack).set(r, g, b, a)
