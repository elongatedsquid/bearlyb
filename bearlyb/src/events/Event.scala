package bearlyb.events

import bearlyb.camera.CameraID
import bearlyb.joystick.{HatPosition, JoystickID}
import bearlyb.keyboard.KeyboardID
import bearlyb.keycode.*
import bearlyb.mouse.toMouseButtonSet
import bearlyb.pen.{PenAxis, PenID, PenInput, toPenInputSet}
import bearlyb.power.PowerState
import bearlyb.scancode.Scancode
import bearlyb.sensor.SensorData
import bearlyb.video.{DisplayID, WindowID}
import bearlyb.{gamepad, mouse, touch}
import org.lwjgl.PointerBuffer
import org.lwjgl.sdl.*
import org.lwjgl.sdl.SDLEvents.*
import org.lwjgl.system.MemoryStack.*

import java.util as ju
import scala.concurrent.duration.*
import scala.util.Using

import mouse.MouseID

extension (internal: SDL_Event)

  private[bearlyb] inline def duration: Duration =
    internal.common().timestamp().nanos

trait Event:
  def timestamp: Duration

object Event:

  def pollEvents(): Iterator[Event] = new Iterator[Event]:
    private var eventBuffer: Option[Event] = pollEvent

    private def pollEvent: Option[Event] = Using(stackPush()): stack =>
      val event = SDL_Event.malloc(stack)
      if SDL_PollEvent(event) then Some(Event.fromInternal(event)) else None
    .get

    override def next(): Event = (eventBuffer, pollEvent) match
      case (Some(oldEvent), Some(newEvent)) =>
        eventBuffer = Some(newEvent)
        oldEvent
      case (Some(oldEvent), None) =>
        eventBuffer = None
        oldEvent
      case (None, _) =>
        throw ju.NoSuchElementException("No more events to handle")

    override def hasNext: Boolean = eventBuffer.nonEmpty
  end pollEvents

  private[bearlyb] def fromInternal(internal: SDL_Event): Event =
    internal.`type`() match
      case SDL_EVENT_QUIT                  => Quit(internal.duration)
      case SDL_EVENT_TERMINATING           => Terminating(internal.duration)
      case SDL_EVENT_LOW_MEMORY            => LowMemory(internal.duration)
      case SDL_EVENT_WILL_ENTER_BACKGROUND =>
        WillEnterBackground(internal.duration)
      case SDL_EVENT_DID_ENTER_BACKGROUND =>
        DidEnterBackground(internal.duration)
      case SDL_EVENT_WILL_ENTER_FOREGROUND =>
        WillEnterForeground(internal.duration)
      case SDL_EVENT_LOCALE_CHANGED       => LocaleChanged(internal.duration)
      case SDL_EVENT_SYSTEM_THEME_CHANGED =>
        SystemThemeChanged(internal.duration)

      // Display events
      case SDL_EVENT_DISPLAY_ORIENTATION =>
        Display
          .Orientation(internal.duration, internal.display.displayID)
      case SDL_EVENT_DISPLAY_ADDED =>
        Display
          .Added(internal.duration, internal.display.displayID)
      case SDL_EVENT_DISPLAY_REMOVED =>
        Display
          .Removed(internal.duration, internal.display.displayID)
      case SDL_EVENT_DISPLAY_MOVED =>
        Display
          .Moved(internal.duration, internal.display.displayID)
      case SDL_EVENT_DISPLAY_DESKTOP_MODE_CHANGED =>
        Display
          .DesktopModeChanged(internal.duration, internal.display.displayID)
      case SDL_EVENT_DISPLAY_CURRENT_MODE_CHANGED =>
        Display
          .CurrentModeChanged(internal.duration, internal.display.displayID)
      case SDL_EVENT_DISPLAY_CONTENT_SCALE_CHANGED =>
        Display
          .ContentScaleChanged(internal.duration, internal.display.displayID)

      // Window events
      case SDL_EVENT_WINDOW_SHOWN =>
        Window
          .Shown(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_HIDDEN =>
        Window
          .Hidden(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_EXPOSED =>
        Window
          .Exposed(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_MOVED =>
        Window.Moved(
          internal.duration,
          internal.window.windowID,
          internal.window.data1,
          internal.window.data2
        )
      case SDL_EVENT_WINDOW_RESIZED =>
        Window.Resized(
          internal.duration,
          internal.window.windowID,
          internal.window.data1,
          internal.window.data2
        )
      case SDL_EVENT_WINDOW_PIXEL_SIZE_CHANGED =>
        Window.Resized(
          internal.duration,
          internal.window.windowID,
          internal.window.data1,
          internal.window.data2
        )
      case SDL_EVENT_WINDOW_METAL_VIEW_RESIZED =>
        Window
          .MetalViewResized(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_MINIMIZED =>
        Window
          .Minimized(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_MAXIMIZED =>
        Window
          .Maximized(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_RESTORED =>
        Window
          .Restored(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_MOUSE_ENTER =>
        Window
          .MouseEnter(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_MOUSE_LEAVE =>
        Window
          .MouseLeave(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_FOCUS_GAINED =>
        Window
          .FocusGained(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_FOCUS_LOST =>
        Window
          .FocusLost(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_CLOSE_REQUESTED =>
        Window
          .CloseRequested(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_HIT_TEST =>
        Window
          .HitTest(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_ICCPROF_CHANGED =>
        Window
          .ICCProfChanged(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_DISPLAY_CHANGED =>
        Window.DisplayChanged(
          internal.duration,
          internal.window.windowID,
          internal.window.data1
        )
      case SDL_EVENT_WINDOW_SAFE_AREA_CHANGED =>
        Window
          .SafeAreaChanged(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_OCCLUDED =>
        Window
          .Occluded(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_ENTER_FULLSCREEN =>
        Window
          .EnterFullscreen(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_LEAVE_FULLSCREEN =>
        Window
          .LeaveFullscreen(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_DESTROYED =>
        Window
          .Destroyed(internal.duration, internal.window.windowID)
      case SDL_EVENT_WINDOW_HDR_STATE_CHANGED =>
        Window
          .HDRStateChanged(internal.duration, internal.window.windowID)

      // Key events
      case SDL_EVENT_KEY_DOWN =>
        Key.Down(
          internal.duration,
          internal.key.windowID,
          internal.key.which,
          Scancode.fromInternal(internal.key().scancode()),
          Keycode.fromInternal(internal.key().key()),
          internal.key.mod.toKeymodSet,
          internal.key.down,
          internal.key.repeat
        )
      case SDL_EVENT_KEY_UP =>
        Key.Up(
          internal.duration,
          internal.key.windowID,
          internal.key.which,
          Scancode.fromInternal(internal.key().scancode()),
          Keycode.fromInternal(internal.key().key()),
          internal.key.mod.toKeymodSet,
          internal.key.down,
          internal.key.repeat
        )

      case SDL_EVENT_TEXT_EDITING =>
        TextEditing(
          internal.duration,
          internal.edit.windowID,
          internal.edit.textString,
          internal.edit.start,
          internal.edit.length
        )

      case SDL_EVENT_TEXT_INPUT =>
        TextInput(
          internal.duration,
          internal.text.windowID,
          internal.text.textString
        )

      case SDL_EVENT_KEYMAP_CHANGED => KeymapChanged(internal.duration)
      case SDL_EVENT_KEYBOARD_ADDED =>
        KeyboardAdded(internal.duration, internal.kdevice.which)
      case SDL_EVENT_KEYBOARD_REMOVED =>
        KeyboardAdded(internal.duration, internal.kdevice.which)

      case SDL_EVENT_TEXT_EDITING_CANDIDATES =>
        val candidateBuffer: Option[PointerBuffer] =
          Option(internal.edit_candidates.candidates)
        val candidateSeq: Seq[String] = candidateBuffer match
          case None         => Seq.empty
          case Some(buffer) =>
            Seq.tabulate(
              internal.edit_candidates.num_candidates
            )(buffer.getStringUTF8)
        val selectedCandidate =
          internal.edit_candidates.selected_candidate match
            case -1 => None
            case c  => Some(c)
        TextEditingCandidates(
          internal.duration,
          internal.edit_candidates.windowID,
          candidateSeq,
          selectedCandidate,
          internal.edit_candidates.horizontal
        )

      case SDL_EVENT_MOUSE_BUTTON_DOWN =>
        Mouse.ButtonDown(
          internal.duration,
          internal.button.windowID,
          internal.button.which,
          mouse.Button.fromInternal(internal.button.button),
          internal.button.down,
          internal.button.clicks,
          internal.button.x,
          internal.button.y
        )
      case SDL_EVENT_MOUSE_BUTTON_UP =>
        Mouse.ButtonUp(
          internal.duration,
          internal.button.windowID,
          internal.button.which,
          mouse.Button.fromInternal(internal.button.button),
          internal.button.down,
          internal.button.clicks,
          internal.button.x,
          internal.button.y
        )

      case SDL_EVENT_MOUSE_MOTION =>
        Mouse.Motion(
          internal.duration,
          internal.motion.which,
          internal.motion.state.toMouseButtonSet,
          internal.motion.x,
          internal.motion.y,
          internal.motion.xrel,
          internal.motion.yrel
        )

      case SDL_EVENT_MOUSE_WHEEL =>
        Mouse.Wheel(
          internal.duration,
          internal.wheel.windowID,
          internal.wheel.which,
          internal.wheel.x,
          internal.wheel.y,
          mouse.WheelDirection.fromOrdinal(internal.wheel.direction),
          internal.wheel.mouse_x,
          internal.wheel.mouse_y,
          internal.wheel.integer_x,
          internal.wheel.integer_y
        )

      case SDL_EVENT_MOUSE_ADDED =>
        Mouse
          .Added(internal.duration, internal.mdevice.which)

      case SDL_EVENT_MOUSE_REMOVED =>
        Mouse
          .Removed(internal.duration, internal.mdevice.which)

      case SDL_EVENT_JOYSTICK_BUTTON_DOWN =>
        Joystick.ButtonDown(
          internal.duration,
          internal.jbutton.which,
          internal.jbutton.button,
          internal.jbutton.down
        )

      case SDL_EVENT_JOYSTICK_BUTTON_UP =>
        Joystick.ButtonUp(
          internal.duration,
          internal.jbutton.which,
          internal.jbutton.button,
          internal.jbutton.down
        )

      case SDL_EVENT_JOYSTICK_AXIS_MOTION =>
        Joystick.AxisMotion(
          internal.duration,
          internal.jaxis.which,
          internal.jaxis.axis,
          internal.jaxis.value
        )

      case SDL_EVENT_JOYSTICK_BALL_MOTION =>
        Joystick.BallMotion(
          internal.duration,
          internal.jball.which,
          internal.jball.ball,
          internal.jball.xrel,
          internal.jball.yrel
        )

      case SDL_EVENT_JOYSTICK_HAT_MOTION =>
        Joystick.HatMotion(
          internal.duration,
          internal.jhat.which,
          internal.jhat.hat,
          HatPosition.fromInternal(internal.jhat.value)
        )

      case SDL_EVENT_JOYSTICK_ADDED =>
        Joystick
          .Added(internal.duration, internal.jdevice.which)

      case SDL_EVENT_JOYSTICK_REMOVED =>
        Joystick
          .Removed(internal.duration, internal.jdevice.which)

      case SDL_EVENT_JOYSTICK_BATTERY_UPDATED =>
        Joystick.BatteryUpdated(
          internal.duration,
          internal.jbattery.which,
          PowerState.fromInternal(internal.jbattery.state),
          internal.jbattery.percent
        )

      case SDL_EVENT_JOYSTICK_UPDATE_COMPLETE =>
        Joystick
          .UpdateComplete(internal.duration, internal.jdevice.which)

      case SDL_EVENT_GAMEPAD_BUTTON_DOWN =>
        Gamepad.ButtonDown(
          internal.duration,
          internal.gbutton.which,
          gamepad.Gamepad.Button.fromInternal(internal.gbutton.button),
          internal.gbutton.down
        )

      case SDL_EVENT_GAMEPAD_BUTTON_UP =>
        Gamepad.ButtonUp(
          internal.duration,
          internal.gbutton.which,
          gamepad.Gamepad.Button.fromInternal(internal.gbutton.button),
          internal.gbutton.down
        )

      case SDL_EVENT_GAMEPAD_AXIS_MOTION =>
        Gamepad.AxisMotion(
          internal.duration,
          internal.gaxis.which,
          gamepad.Gamepad.Axis.fromInternal(internal.gaxis.axis),
          internal.gaxis.value
        )

      case SDL_EVENT_GAMEPAD_ADDED =>
        Gamepad
          .Added(internal.duration, internal.gdevice.which)
      case SDL_EVENT_GAMEPAD_REMOVED =>
        Gamepad
          .Removed(internal.duration, internal.gdevice.which)
      case SDL_EVENT_GAMEPAD_REMAPPED =>
        Gamepad
          .Remapped(internal.duration, internal.gdevice.which)

      case SDL_EVENT_GAMEPAD_TOUCHPAD_DOWN =>
        Gamepad.TouchpadDown(
          internal.duration,
          internal.gtouchpad.which,
          internal.gtouchpad.touchpad,
          internal.gtouchpad.finger,
          internal.gtouchpad.x,
          internal.gtouchpad.y,
          internal.gtouchpad.pressure
        )

      case SDL_EVENT_GAMEPAD_TOUCHPAD_MOTION =>
        Gamepad.TouchpadMotion(
          internal.duration,
          internal.gtouchpad.which,
          internal.gtouchpad.touchpad,
          internal.gtouchpad.finger,
          internal.gtouchpad.x,
          internal.gtouchpad.y,
          internal.gtouchpad.pressure
        )

      case SDL_EVENT_GAMEPAD_TOUCHPAD_UP =>
        Gamepad.TouchpadUp(
          internal.duration,
          internal.gtouchpad.which,
          internal.gtouchpad.touchpad,
          internal.gtouchpad.finger,
          internal.gtouchpad.x,
          internal.gtouchpad.y,
          internal.gtouchpad.pressure
        )

      case SDL_EVENT_GAMEPAD_SENSOR_UPDATE =>
        Gamepad.SensorUpdate(
          internal.duration,
          internal.gsensor.which,
          SensorData.fromInternal(
            internal.gsensor.sensor,
            internal.gsensor.data
          ),
          internal.gsensor.sensor_timestamp
        )

      case SDL_EVENT_GAMEPAD_UPDATE_COMPLETE =>
        Gamepad
          .UpdateComplete(internal.duration, internal.gdevice.which)

      case SDL_EVENT_GAMEPAD_STEAM_HANDLE_UPDATED =>
        Gamepad
          .SteamHandleUpdated(internal.duration, internal.gdevice.which)

      case SDL_EVENT_FINGER_DOWN =>
        Finger.Down(
          internal.duration,
          internal.tfinger.touchID,
          internal.tfinger.fingerID,
          internal.tfinger.x,
          internal.tfinger.y,
          internal.tfinger.dx,
          internal.tfinger.dy,
          internal.tfinger.pressure,
          internal.tfinger.windowID
        )

      case SDL_EVENT_FINGER_UP =>
        Finger.Up(
          internal.duration,
          internal.tfinger.touchID,
          internal.tfinger.fingerID,
          internal.tfinger.x,
          internal.tfinger.y,
          internal.tfinger.dx,
          internal.tfinger.dy,
          internal.tfinger.pressure,
          internal.tfinger.windowID
        )

      case SDL_EVENT_FINGER_MOTION =>
        Finger.Motion(
          internal.duration,
          internal.tfinger.touchID,
          internal.tfinger.fingerID,
          internal.tfinger.x,
          internal.tfinger.y,
          internal.tfinger.dx,
          internal.tfinger.dy,
          internal.tfinger.pressure,
          internal.tfinger.windowID
        )

      case SDL_EVENT_FINGER_CANCELED =>
        Finger.Canceled(
          internal.duration,
          internal.tfinger.touchID,
          internal.tfinger.fingerID,
          internal.tfinger.x,
          internal.tfinger.y,
          internal.tfinger.dx,
          internal.tfinger.dy,
          internal.tfinger.pressure,
          internal.tfinger.windowID
        )

      case SDL_EVENT_CLIPBOARD_UPDATE =>
        val mimeTypesBuffer = internal.clipboard.mime_types
        val mimeTypesSeq: Seq[String] = Seq.tabulate(
          internal.clipboard.num_mime_types
        )(mimeTypesBuffer.getStringUTF8)
        ClipboardUpdate(
          internal.duration,
          internal.clipboard.owner,
          mimeTypesSeq
        )

      case SDL_EVENT_DROP_FILE =>
        Drop.File(
          internal.duration,
          internal.drop.windowID,
          internal.drop.x,
          internal.drop.y,
          Option(internal.drop.sourceString),
          internal.drop.dataString
        )

      case SDL_EVENT_DROP_TEXT =>
        Drop.Text(
          internal.duration,
          internal.drop.windowID,
          internal.drop.x,
          internal.drop.y,
          Option(internal.drop.sourceString),
          internal.drop.dataString
        )

      case SDL_EVENT_DROP_BEGIN =>
        Drop.Begin(
          internal.duration,
          internal.drop.windowID,
          Option(internal.drop.sourceString)
        )

      case SDL_EVENT_DROP_COMPLETE =>
        Drop.Complete(
          internal.duration,
          internal.drop.windowID,
          internal.drop.x,
          internal.drop.y,
          Option(internal.drop.sourceString)
        )

      case SDL_EVENT_DROP_POSITION =>
        Drop.Position(
          internal.duration,
          internal.drop.windowID,
          internal.drop.x,
          internal.drop.y,
          Option(internal.drop.sourceString)
        )

      case SDL_EVENT_PEN_PROXIMITY_IN =>
        Pen.ProximityIn(
          internal.duration,
          internal.pproximity.windowID,
          internal.pproximity.which
        )

      case SDL_EVENT_PEN_PROXIMITY_OUT =>
        Pen.ProximityOut(
          internal.duration,
          internal.pproximity.windowID,
          internal.pproximity.which
        )

      case SDL_EVENT_PEN_DOWN =>
        Pen.Down(
          internal.duration,
          internal.ptouch.windowID,
          internal.ptouch.which,
          internal.ptouch.pen_state.toPenInputSet,
          internal.ptouch.x,
          internal.ptouch.y,
          internal.ptouch.eraser,
          internal.ptouch.down
        )

      case SDL_EVENT_PEN_UP =>
        Pen.Up(
          internal.duration,
          internal.ptouch.windowID,
          internal.ptouch.which,
          internal.ptouch.pen_state.toPenInputSet,
          internal.ptouch.x,
          internal.ptouch.y,
          internal.ptouch.eraser,
          internal.ptouch.down
        )

      case SDL_EVENT_PEN_BUTTON_DOWN =>
        Pen.ButtonDown(
          internal.duration,
          internal.pbutton.windowID,
          internal.pbutton.which,
          internal.pbutton.pen_state.toPenInputSet
        )

      case SDL_EVENT_PEN_BUTTON_UP =>
        Pen.ButtonUp(
          internal.duration,
          internal.pbutton.windowID,
          internal.pbutton.which,
          internal.pbutton.pen_state.toPenInputSet
        )

      case SDL_EVENT_PEN_MOTION =>
        Pen.Motion(
          internal.duration,
          internal.pmotion.windowID,
          internal.pmotion.which,
          internal.pmotion.pen_state.toPenInputSet,
          internal.pmotion.x,
          internal.pmotion.y
        )

      case SDL_EVENT_PEN_AXIS =>
        Pen.Axis(
          internal.duration,
          internal.paxis.windowID,
          internal.paxis.which,
          internal.paxis.pen_state.toPenInputSet,
          internal.paxis.x,
          internal.paxis.y,
          PenAxis.fromInternal(internal.paxis.axis),
          internal.paxis.value
        )

      case SDL_EVENT_CAMERA_DEVICE_ADDED =>
        CameraDevice
          .Added(internal.duration, internal.cdevice.which)

      case SDL_EVENT_CAMERA_DEVICE_REMOVED =>
        CameraDevice
          .Removed(internal.duration, internal.cdevice.which)

      case SDL_EVENT_CAMERA_DEVICE_APPROVED =>
        CameraDevice
          .Approved(internal.duration, internal.cdevice.which)

      case SDL_EVENT_CAMERA_DEVICE_DENIED =>
        CameraDevice
          .Denied(internal.duration, internal.cdevice.which)

      case SDL_EVENT_RENDER_TARGETS_RESET =>
        Render
          .TargetsReset(internal.duration, internal.render.windowID)

      case SDL_EVENT_RENDER_DEVICE_RESET =>
        Render
          .DeviceReset(internal.duration, internal.render.windowID)

      case SDL_EVENT_RENDER_DEVICE_LOST =>
        Render
          .DeviceLost(internal.duration, internal.render.windowID)

      case SDL_EVENT_POLL_SENTINEL => PollSentinel(internal.duration)

      case other if (SDL_EVENT_USER to SDL_EVENT_LAST) contains other =>
        User(internal.duration, other, internal)
  end fromInternal

  // ALL EVENTS HERE

  /** User-requested quit */
  case class Quit(timestamp: Duration) extends Event
  case class Terminating(timestamp: Duration) extends Event
  case class LowMemory(timestamp: Duration) extends Event

  case class WillEnterBackground(timestamp: Duration) extends Event

  case class DidEnterBackground(timestamp: Duration) extends Event

  case class WillEnterForeground(timestamp: Duration) extends Event

  case class DidEnterForeground(timestamp: Duration) extends Event

  case class LocaleChanged(timestamp: Duration) extends Event

  case class SystemThemeChanged(timestamp: Duration) extends Event

  enum Display extends Event:
    def id: DisplayID

    case Orientation(timestamp: Duration, id: DisplayID)
    case Added(timestamp: Duration, id: DisplayID)
    case Removed(timestamp: Duration, id: DisplayID)
    case Moved(timestamp: Duration, id: DisplayID)

    case DesktopModeChanged(timestamp: Duration, id: DisplayID)

    case CurrentModeChanged(timestamp: Duration, id: DisplayID)

    case ContentScaleChanged(timestamp: Duration, id: DisplayID)

  end Display

  enum Window extends Event:
    def id: WindowID

    /** Window has been shown */
    case Shown(timestamp: Duration, id: WindowID)

    /** Window has been hidden */
    case Hidden(timestamp: Duration, id: WindowID)

    /** Window has been exposed and should be redrawn, and can be redrawn
      * directly from event watchers for this event
      */
    case Exposed(timestamp: Duration, id: WindowID)

    /** Window has been moved to x, y */
    case Moved(timestamp: Duration, id: WindowID, x: Int, y: Int)

    /** Window has been resized to w x h */
    case Resized(timestamp: Duration, id: WindowID, w: Int, h: Int)

    /** The pixel size of the window has changed to pw x ph */
    case PixelSizeChanged(timestamp: Duration, id: WindowID, pw: Int, ph: Int)

    /** The pixel size of a Metal view associated with the window has changed */
    case MetalViewResized(timestamp: Duration, id: WindowID)

    /** Window has been minimized */
    case Minimized(timestamp: Duration, id: WindowID)

    /** Window has been maximized */
    case Maximized(timestamp: Duration, id: WindowID)

    /** Window has been restored to normal size and position */
    case Restored(timestamp: Duration, id: WindowID)

    /** Window has gained mouse focus */
    case MouseEnter(timestamp: Duration, id: WindowID)

    /** Window has lost mouse focus */
    case MouseLeave(timestamp: Duration, id: WindowID)

    /** Window has gained keyboard focus */
    case FocusGained(timestamp: Duration, id: WindowID)

    /** Window has lost keyboard focus */
    case FocusLost(timestamp: Duration, id: WindowID)

    /** The window manager requests that the window be closed */
    case CloseRequested(timestamp: Duration, id: WindowID)

    /** Window had a hit test that wasn't SDL_HITTEST_NORMAL */
    case HitTest(timestamp: Duration, id: WindowID)

    /** The ICC profile of the window's display has changed */
    case ICCProfChanged(timestamp: Duration, id: WindowID)

    /** Window has been moved to display `display` */
    case DisplayChanged(timestamp: Duration, id: WindowID, display: DisplayID)

    /** Window display scale has been changed */
    case DisplayScaleChanged(timestamp: Duration, id: WindowID)

    /** The window safe area has been changed */
    case SafeAreaChanged(timestamp: Duration, id: WindowID)

    /** The window has been occluded */
    case Occluded(timestamp: Duration, id: WindowID)

    /** The window has entered fullscreen mode */
    case EnterFullscreen(timestamp: Duration, id: WindowID)

    /** The window has left fullscreen mode */
    case LeaveFullscreen(timestamp: Duration, id: WindowID)

    /** The window with the associated ID is being or has been destroyed. If
      * this message is being handled in an event watcher, the window handle is
      * still valid and can still be used to retrieve any properties associated
      * with the window. Otherwise, the handle has already been destroyed and
      * all resources associated with it are invalid
      */
    case Destroyed(timestamp: Duration, id: WindowID)

    /** Window HDR properties have changed */
    case HDRStateChanged(timestamp: Duration, id: WindowID)

  end Window

  enum Key extends Event:
    def windowID: WindowID
    def which: KeyboardID
    def scancode: Scancode
    def key: Keycode
    def mod: Set[Keymod]
    def down: Boolean
    def repeat: Boolean

    case Down(
        timestamp: Duration,
        windowID: WindowID,
        which: KeyboardID,
        scancode: Scancode,
        key: Keycode,
        mod: Set[Keymod],
        down: Boolean,
        repeat: Boolean
    )

    case Up(
        timestamp: Duration,
        windowID: WindowID,
        which: KeyboardID,
        scancode: Scancode,
        key: Keycode,
        mod: Set[Keymod],
        down: Boolean,
        repeat: Boolean
    )

  end Key

  case class TextEditing(
      timestamp: Duration,
      windowID: WindowID,
      text: String,
      start: Int,
      length: Int
  ) extends Event

  case class TextInput(timestamp: Duration, windowID: WindowID, text: String)
      extends Event

  case class KeymapChanged(timestamp: Duration) extends Event

  case class KeyboardAdded(timestamp: Duration, which: KeyboardID) extends Event

  case class KeyboardRemoved(timestamp: Duration, which: KeyboardID)
      extends Event

  case class TextEditingCandidates(
      timestamp: Duration,
      windowID: WindowID,
      candidates: Seq[String],
      selectedCandidate: Option[Int],
      horizontal: Boolean
  ) extends Event

  enum Mouse extends Event:
    def which: MouseID

    case ButtonDown(
        timestamp: Duration,
        windowID: WindowID,
        which: MouseID,
        button: mouse.Button,
        down: Boolean,
        clicks: Byte,
        x: Float,
        y: Float
    )

    case ButtonUp(
        timestamp: Duration,
        windowID: WindowID,
        which: MouseID,
        button: mouse.Button,
        down: Boolean,
        clicks: Byte,
        x: Float,
        y: Float
    )

    case Motion(
        timestamp: Duration,
        which: MouseID,
        state: Set[mouse.Button],
        x: Float,
        y: Float,
        xrel: Float,
        yrel: Float
    )

    case Wheel(
        timestamp: Duration,
        windowID: WindowID,
        which: MouseID,
        scrolledX: Float,
        scrolledY: Float,
        direction: mouse.WheelDirection,
        mouseX: Float,
        mouseY: Float,
        integerScrolledX: Int,
        integerScrolledY: Int
    )

    case Added(timestamp: Duration, which: MouseID)
    case Removed(timestamp: Duration, which: MouseID)

  end Mouse

  enum Joystick extends Event:
    def which: JoystickID

    case ButtonDown(
        timestamp: Duration,
        which: JoystickID,
        button: Byte,
        down: Boolean
    )

    case ButtonUp(
        timestamp: Duration,
        which: JoystickID,
        button: Byte,
        down: Boolean
    )

    case AxisMotion(
        timestamp: Duration,
        which: JoystickID,
        axis: Byte,
        value: Int
    )

    case BallMotion(
        timestamp: Duration,
        which: JoystickID,
        ball: Byte,
        xrel: Int,
        yrel: Int
    )

    case HatMotion(
        timestamp: Duration,
        which: JoystickID,
        hat: Byte,
        value: HatPosition
    )

    case Added(timestamp: Duration, which: JoystickID)
    case Removed(timestamp: Duration, which: JoystickID)

    case BatteryUpdated(
        timestamp: Duration,
        which: JoystickID,
        state: PowerState,
        percent: Int
    )

    case UpdateComplete(timestamp: Duration, which: JoystickID)

  end Joystick

  enum Gamepad extends Event:
    def which: JoystickID

    case ButtonDown(
        timestamp: Duration,
        which: JoystickID,
        button: gamepad.Gamepad.Button,
        down: Boolean
    )

    case ButtonUp(
        timestamp: Duration,
        which: JoystickID,
        button: gamepad.Gamepad.Button,
        down: Boolean
    )

    case AxisMotion(
        timestamp: Duration,
        which: JoystickID,
        axis: gamepad.Gamepad.Axis,
        value: Int
    )

    case Added(timestamp: Duration, which: JoystickID)
    case Removed(timestamp: Duration, which: JoystickID)
    case Remapped(timestamp: Duration, which: JoystickID)

    case TouchpadDown(
        timestamp: Duration,
        which: JoystickID,
        touchpad: Int,
        finger: Int,
        x: Float,
        y: Float,
        pressure: Float
    )

    case TouchpadMotion(
        timestamp: Duration,
        which: JoystickID,
        touchpad: Int,
        finger: Int,
        x: Float,
        y: Float,
        pressure: Float
    )

    case TouchpadUp(
        timestamp: Duration,
        which: JoystickID,
        touchpad: Int,
        finger: Int,
        x: Float,
        y: Float,
        pressure: Float
    )

    case SensorUpdate(
        timestamp: Duration,
        which: JoystickID,
        data: SensorData,
        sensorTimestamp: Long
    )

    case UpdateComplete(timestamp: Duration, which: JoystickID)

    case SteamHandleUpdated(timestamp: Duration, which: JoystickID)

  end Gamepad

  enum Finger extends Event:
    def touchID: touch.TouchID
    def fingerID: touch.FingerID
    def x: Float
    def y: Float
    def dx: Float
    def dy: Float
    def pressure: Float
    def windowID: WindowID

    case Down(
        timestamp: Duration,
        touchID: touch.TouchID,
        fingerID: touch.FingerID,
        x: Float,
        y: Float,
        dx: Float,
        dy: Float,
        pressure: Float,
        windowID: WindowID
    )

    case Up(
        timestamp: Duration,
        touchID: touch.TouchID,
        fingerID: touch.FingerID,
        x: Float,
        y: Float,
        dx: Float,
        dy: Float,
        pressure: Float,
        windowID: WindowID
    )

    case Motion(
        timestamp: Duration,
        touchID: touch.TouchID,
        fingerID: touch.FingerID,
        x: Float,
        y: Float,
        dx: Float,
        dy: Float,
        pressure: Float,
        windowID: WindowID
    )

    case Canceled(
        timestamp: Duration,
        touchID: touch.TouchID,
        fingerID: touch.FingerID,
        x: Float,
        y: Float,
        dx: Float,
        dy: Float,
        pressure: Float,
        windowID: WindowID
    )

  end Finger

  /** An event which lets you handle the clipboard
    * @param owner
    *   are we owning the clipboard? (internal update)
    * @param mimeTypes
    *   current mime types, a mime type is the kind of data contained in the
    *   clipboard. Examples of MIME-types are text/javascript or image/png.
    */
  case class ClipboardUpdate(
      timestamp: Duration,
      owner: Boolean,
      mimeTypes: Seq[String]
  ) extends Event

  enum Drop extends Event:
    def windowID: WindowID
    def source: Option[String]

    case File(
        timestamp: Duration,
        windowID: WindowID,
        x: Float,
        y: Float,
        source: Option[String],
        filename: String
    )

    case Text(
        timestamp: Duration,
        windowID: WindowID,
        x: Float,
        y: Float,
        source: Option[String],
        text: String
    )

    case Begin(timestamp: Duration, windowID: WindowID, source: Option[String])

    case Complete(
        timestamp: Duration,
        windowID: WindowID,
        x: Float,
        y: Float,
        source: Option[String]
    )

    case Position(
        timestamp: Duration,
        windowID: WindowID,
        x: Float,
        y: Float,
        source: Option[String]
    )

  end Drop

  enum Pen extends Event:
    def windowID: WindowID
    def which: PenID

    case ProximityIn(timestamp: Duration, windowID: WindowID, which: PenID)

    case ProximityOut(timestamp: Duration, windowID: WindowID, which: PenID)

    case Down(
        timestamp: Duration,
        windowID: WindowID,
        which: PenID,
        penState: Set[PenInput],
        x: Float,
        y: Float,
        eraser: Boolean,
        down: Boolean
    )

    case Up(
        timestamp: Duration,
        windowID: WindowID,
        which: PenID,
        penState: Set[PenInput],
        x: Float,
        y: Float,
        eraser: Boolean,
        down: Boolean
    )

    case ButtonDown(
        timestamp: Duration,
        windowID: WindowID,
        which: PenID,
        penState: Set[PenInput]
    )

    case ButtonUp(
        timestamp: Duration,
        windowID: WindowID,
        which: PenID,
        penState: Set[PenInput]
    )

    case Motion(
        timestamp: Duration,
        windowID: WindowID,
        which: PenID,
        penState: Set[PenInput],
        x: Float,
        y: Float
    )

    case Axis(
        timestamp: Duration,
        windowID: WindowID,
        which: PenID,
        penState: Set[PenInput],
        x: Float,
        y: Float,
        axis: PenAxis,
        value: Float
    )

  end Pen

  enum CameraDevice extends Event:
    def which: CameraID

    case Added(timestamp: Duration, which: CameraID)
    case Removed(timestamp: Duration, which: CameraID)
    case Approved(timestamp: Duration, which: CameraID)
    case Denied(timestamp: Duration, which: CameraID)

  enum Render extends Event:
    def windowID: WindowID

    case TargetsReset(timestamp: Duration, windowID: WindowID)

    case DeviceReset(timestamp: Duration, windowID: WindowID)

    case DeviceLost(timestamp: Duration, windowID: WindowID)

  end Render

  private case class PollSentinel(timestamp: Duration) extends Event

  private case class User(timestamp: Duration, `type`: Int, internal: SDL_Event)
      extends Event

end Event
