package bearlyb.keycode

import org.lwjgl.sdl.SDLKeycode.*

import scala.collection.immutable.BitSet

extension (internal: Short)

  private[bearlyb] def toKeymodSet: Set[Keymod] = BitSet
    .fromBitMaskNoCopy(Array(internal))

opaque type Keymod = Int

object Keymod:
  /** no modifier is applicable. */
  val None: Keymod = 0x0000

  /** the left Shift key is down. */
  val LShift: Keymod = 0x0001

  /** the right Shift key is down. */
  val RShift: Keymod = 0x0002

  /** the Level 5 Shift key is down. */
  val Level5: Keymod = 0x0004

  /** the left Ctrl (Control) key is down. */
  val LCtrl: Keymod = 0x0040

  /** the right Ctrl (Control) key is down. */
  val RCtrl: Keymod = 0x0080

  /** the left Alt key is down. */
  val LAlt: Keymod = 0x0100

  /** the right Alt key is down. */
  val RAlt: Keymod = 0x0200

  /** the left GUI key (often the Windows key) is down. */
  val LGui: Keymod = 0x0400

  /** the right GUI key (often the Windows key) is down. */
  val RGui: Keymod = 0x0800

  /** the Num Lock key (may be located on an extended keypad) is down. */
  val Num: Keymod = 0x1000

  /** the Caps Lock key is down. */
  val Caps: Keymod = 0x2000

  /** the !AltGr key is down. */
  val Mode: Keymod = 0x4000

  /** the Scroll Lock key is down. */
  val Scroll: Keymod = 0x8000

  /** Any Ctrl key is down. */
  val Ctrl: Keymod = SDL_KMOD_CTRL

  /** Any Shift key is down. */
  val Shift: Keymod = SDL_KMOD_SHIFT

  /** Any Alt key is down. */
  val Alt: Keymod = SDL_KMOD_ALT

  /** Any Gui (often windows key) is down. */
  val Gui: Keymod = SDL_KMOD_GUI

  extension (mod: Keymod) private[bearlyb] def internal: Int = mod

  private[bearlyb] def fromInternal(internal: Int): Keymod = internal

end Keymod
