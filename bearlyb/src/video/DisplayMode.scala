package bearlyb.video

import bearlyb.pixels.PixelFormat
import org.lwjgl.sdl.*

final class DisplayMode private (
    private[bearlyb] val internal: SDL_DisplayMode
):
  lazy val displayID: DisplayID = internal.displayID

  lazy val format: PixelFormat = PixelFormat.fromInternal(internal.format)

  lazy val width: Int = internal.w
  lazy val height: Int = internal.h
  lazy val pixelDensity: Float = internal.pixel_density
  lazy val refreshRate: Float = internal.refresh_rate

  lazy val refreshRateFraction: (numerator: Int, denominator: Int) =
    (internal.refresh_rate_numerator, internal.refresh_rate_denominator)

  override def toString: String =
    val (n, d) = refreshRateFraction
    s"""DisplayMode(
       |displayID: $displayID,
       |format: $format,
       |width: $width,
       |height: $height,
       |pixelDensity: $pixelDensity,
       |refreshRate: $refreshRate,
       |refreshRateFraction: $n/$d
       |)""".stripMargin

  end toString

end DisplayMode

object DisplayMode:

  private[bearlyb] def fromInternal(internal: SDL_DisplayMode): DisplayMode =
    new DisplayMode(internal)
