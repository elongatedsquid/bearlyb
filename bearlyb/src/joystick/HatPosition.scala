package bearlyb.joystick

import org.lwjgl.sdl.SDLJoystick.*

enum HatPosition(private[bearlyb] val internal: Byte):
  case Centered extends HatPosition(SDL_HAT_CENTERED)
  case Up extends HatPosition(SDL_HAT_UP)
  case Right extends HatPosition(SDL_HAT_RIGHT)
  case Down extends HatPosition(SDL_HAT_DOWN)
  case Left extends HatPosition(SDL_HAT_LEFT)
  case RightUp extends HatPosition(SDL_HAT_RIGHTUP)
  case RightDown extends HatPosition(SDL_HAT_RIGHTDOWN)
  case LeftUp extends HatPosition(SDL_HAT_LEFTUP)
  case LeftDown extends HatPosition(SDL_HAT_LEFTDOWN)

end HatPosition

object HatPosition:

  private[bearlyb] def fromInternal(pos: Byte): HatPosition =
    import HatPosition as H
    pos match
      case SDL_HAT_CENTERED  => H.Centered
      case SDL_HAT_UP        => H.Up
      case SDL_HAT_RIGHT     => H.Right
      case SDL_HAT_DOWN      => H.Down
      case SDL_HAT_LEFT      => H.Left
      case SDL_HAT_RIGHTUP   => H.RightUp
      case SDL_HAT_RIGHTDOWN => H.RightDown
      case SDL_HAT_LEFTUP    => H.LeftUp
      case SDL_HAT_LEFTDOWN  => H.LeftDown
      case _                 => sys.error("Invalid hat position")

    end match

  end fromInternal

end HatPosition
