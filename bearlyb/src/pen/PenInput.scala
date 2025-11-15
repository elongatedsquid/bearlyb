package bearlyb.pen

import org.lwjgl.sdl.SDLPen.*

import scala.collection.immutable.BitSet

opaque type PenInput = Int

extension (internal: Int)

  private[bearlyb] def toPenInputSet: Set[PenInput] = BitSet
    .fromBitMaskNoCopy(Array(internal))

object PenInput:
  /** pen is pressed down */
  val Down: PenInput = SDL_PEN_INPUT_DOWN

  /** button 1 is pressed */
  val Button1: PenInput = SDL_PEN_INPUT_BUTTON_1

  /** button 2 is pressed */
  val Button2: PenInput = SDL_PEN_INPUT_BUTTON_2

  /** button 3 is pressed */
  val Button3: PenInput = SDL_PEN_INPUT_BUTTON_3

  /** button 4 is pressed */
  val Button4: PenInput = SDL_PEN_INPUT_BUTTON_4

  /** button 5 is pressed */
  val Button5: PenInput = SDL_PEN_INPUT_BUTTON_5

  /** eraser tip is used */
  val EraserTip: PenInput = SDL_PEN_INPUT_ERASER_TIP

  extension (p: PenInput) private[bearlyb] def internal: Int = p

  extension (flags: IterableOnce[PenInput])

    private[bearlyb] def combine: Int = flags.iterator.foldLeft(0)(_ | _)

  private[bearlyb] def fromInternal(internal: Int): PenInput = internal

end PenInput
