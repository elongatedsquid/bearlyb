package bearlyb.initialize

enum Flags(private[bearlyb] val internal: Int):
  /** < `SDL_INIT_AUDIO` implies `SDL_INIT_EVENTS` */
  case Audio extends Flags(0x00000010)

  /** < `SDL_INIT_VIDEO` implies `SDL_INIT_EVENTS`, should be initialized on the
    * main thread
    */
  case Video extends Flags(0x00000020)

  /** < `SDL_INIT_JOYSTICK` implies `SDL_INIT_EVENTS`, should be initialized on
    * the same thread as SDL_INIT_VIDEO on Windows if you don't set
    * SDL_HINT_JOYSTICK_THREAD
    */
  case Joystick extends Flags(0x00000200)
  case Haptic extends Flags(0x00001000)

  /** < `SDL_INIT_GAMEPAD` implies `SDL_INIT_JOYSTICK` */
  case Gamepad extends Flags(0x00002000)
  case Events extends Flags(0x00004000)

  /** < `SDL_INIT_SENSOR` implies `SDL_INIT_EVENTS` */
  case Sensor extends Flags(0x00008000)

  /** < `SDL_INIT_CAMERA` implies `SDL_INIT_EVENTS` */
  case Camera extends Flags(0x00010000)

end Flags

object Flags:

  extension (flags: IterableOnce[Flags])

    private[bearlyb] def combine: Int = flags.iterator
      .foldLeft(0)(_ | _.internal)
