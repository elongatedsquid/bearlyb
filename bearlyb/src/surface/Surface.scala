package bearlyb.surface

import bearlyb.*
import bearlyb.pixels.RawColor
import bearlyb.surface.FlipMode
import bearlyb.util.*
import bearlyb.vectors.Vec.*
import bearlyb.video.imghelper
import org.lwjgl.sdl
import org.lwjgl.system.MemoryStack.*

import scala.math.Numeric.Implicits.infixNumericOps
import scala.util.Using

import sdl.SDLSurface.*
import sdl.SDL_Surface
import sdl.SDLPixels.*
import pixels.{PixelFormat, Color}
import rect.*

class Surface private (private[bearlyb] val internal: SDL_Surface):
  import Surface.Pos

  private def pixFormat = SDL_GetPixelFormatDetails(internal.format)
    .sdlCreationCheck()

  def clear(r: Float, g: Float, b: Float, a: Float = 1): Unit =
    SDL_ClearSurface(internal, r, g, b, a).sdlErrorCheck()

  /** 'Blit' a surface onto this one. Blitting is like taking a picture and
    * putting it on top of another one.
    */
  def blit(src: Surface, at: Pos, mask: Rect[Int] | Null = null): Unit =
    Using(stackPush()): stack =>
      val srcrect = mask.internal(stack)
      val dstrect = Rect(at.x, at.y, 0, 0).internal(stack)
      SDL_BlitSurface(src.internal, srcrect, this.internal, dstrect)
        .sdlErrorCheck()
    .get

  def blitScaled(
      src: Surface,
      dstmask: Rect[Int] | Null = null,
      srcmask: Rect[Int] | Null = null
  )(using scaleMode: ScaleMode): Unit = Using(stackPush()): stack =>
    val srcrect = srcmask.internal(stack)
    val dstrect = dstmask.internal(stack)
    SDL_BlitSurfaceScaled(
      src.internal,
      srcrect,
      this.internal,
      dstrect,
      scaleMode.ordinal
    ).sdlErrorCheck()
  .get

  def blitTiled(
      src: Surface,
      dstmask: Rect[Int] | Null = null,
      srcmask: Rect[Int] | Null = null
  ): Unit = Using(stackPush()): stack =>
    val srcrect = srcmask.internal(stack)
    val dstrect = dstmask.internal(stack)
    SDL_BlitSurfaceTiled(src.internal, srcrect, this.internal, dstrect)
      .sdlErrorCheck()
  .get

  def blitTiledWithScale[T: Numeric](
      src: Surface,
      scale: T,
      dstmask: Rect[Int] | Null = null,
      srcmask: Rect[Int] | Null = null
  )(using scaleMode: ScaleMode): Unit = Using(stackPush()): stack =>
    val srcrect = srcmask.internal(stack)
    val dstrect = dstmask.internal(stack)
    SDL_BlitSurfaceTiledWithScale(
      src.internal,
      srcrect,
      scale.toFloat,
      scaleMode.ordinal,
      this.internal,
      dstrect
    ).sdlErrorCheck()
  .get

  def scaled[T: Numeric as num](by: T)(using scaleMode: ScaleMode): Surface =
    new Surface(
      SDL_ScaleSurface(
        internal,
        (num.fromInt(width) * by).toInt,
        (num.fromInt(height) * by).toInt,
        scaleMode.ordinal
      ).sdlCreationCheck()
    )

  def scaled(width: Int, height: Int)(using scaleMode: ScaleMode): Surface =
    new Surface(
      SDL_ScaleSurface(internal, width, height, scaleMode.ordinal)
        .sdlCreationCheck()
    )

  def destroy(): Unit = SDL_DestroySurface(internal)

  def duplicate: Surface =
    new Surface(SDL_DuplicateSurface(internal).sdlCreationCheck())

  def flip(mode: FlipMode): Unit = SDL_FlipSurface(internal, mode.ordinal)
    .sdlErrorCheck()

  def saveBMP(file: String): Unit = SDL_SaveBMP(internal, file).sdlErrorCheck()

  def width: Int = internal.w
  def height: Int = internal.h

  def format: PixelFormat = PixelFormat.fromInternal(internal.format)

  def apply(pos: Pos): RawColor =
    assert(pos.x < internal.w && pos.y < internal.h)
    val Bpp = pixFormat.bytes_per_pixel

    val idx = pos.y * internal.pitch + pos.x * Bpp
    val raw = Using(stackPush()): stack =>
      val raw = stack.calloc(4)
      for i <- 0 until Bpp do
        val b = internal.pixels.get(idx + i)
        raw.put(i, b)
      raw.asIntBuffer.get(0)
    .get

    RawColor(raw)
  end apply

  def update(pos: Pos, color: RawColor): Unit =
    assert(pos.x < internal.w && pos.y < internal.h)
    val bpp = pixFormat.bits_per_pixel
    val Bpp = pixFormat.bytes_per_pixel
    require(bpp == 8 || bpp == 16 || bpp == 32, s"Unknown bpp: $bpp")

    val idx = pos.y * internal.pitch + pos.x * Bpp
    Using(stackPush()): stack =>
      val raw = stack.malloc(4).putInt(color.internal)
      for i <- 0 until Bpp do internal.pixels.put(idx + i, raw.get(i))
    .get
  end update

  def update(pos: Pos, color: Color): Unit =
    val (x, y) = pos
    val (r, g, b, a) = color
    SDL_WriteSurfacePixel(
      internal,
      x,
      y,
      r.toByte,
      g.toByte,
      b.toByte,
      a.toByte
    ).sdlErrorCheck()

  def update(x: Int, y: Int, color: Color): Unit = update((x, y), color)

  def mapRGBA(color: Color): RawColor =

    val (r, g, b, a) = color
    val result =
      SDL_MapSurfaceRGBA(internal, r.toByte, g.toByte, b.toByte, a.toByte)
    RawColor(result)

  /** Converts a color to the raw value, depending on the surface's format.
    *
    * @param color
    *   the color to be converted
    * @tparam T
    *   the type used to represent each channel of the color, if it is an
    *   integer type, then the value must be between 0 and 255, and if it is a
    *   floating-point type (or is fractional), then the value must be in the
    *   range 0..=1
    * @return
    *   the raw color value
    */
  def mapRGB(color: NamedTuple.Init[Color]): RawColor =
    val (r, g, b) = color
    val result = SDL_MapSurfaceRGB(internal, r.toByte, g.toByte, b.toByte)
    RawColor(result)

  def unmapRGBA(raw: RawColor): Color = Using(stackPush()): stack =>
    val (r, g, b, a) = mallocMany(4, stack)
    SDL_GetRGBA(
      raw.internal,
      SDL_GetPixelFormatDetails(internal.format),
      SDL_GetSurfacePalette(internal),
      r,
      g,
      b,
      a
    )
    (r.get(0).toInt, g.get(0).toInt, b.get(0).toInt, a.get(0).toInt)
  .get

  def unmapRGB(raw: RawColor): NamedTuple.Init[Color] =
    Using(stackPush()): stack =>
      val (r, g, b) = mallocMany(3, stack)
      SDL_GetRGB(
        raw.internal,
        SDL_GetPixelFormatDetails(internal.format),
        SDL_GetSurfacePalette(internal),
        r,
        g,
        b
      )
      (r.get(0).toInt, g.get(0).toInt, b.get(0).toInt)
    .get

  def fillRect(rect: Rect[Int], color: RawColor): Unit =
    Using(stackPush()): stack =>
      val sdlrect = rect.internal(stack)
      SDL_FillSurfaceRect(internal, sdlrect, color.internal).sdlErrorCheck()
    .get

  def fillRect(x: Int, y: Int, w: Int, h: Int, color: RawColor): Unit =
    fillRect(Rect(x, y, w, h), color)

  def fillRect(rect: Rect[Int], color: Color): Unit =
    fillRect(rect, mapRGBA(color))

  def fillRect(x: Int, y: Int, w: Int, h: Int, color: Color): Unit =
    fillRect(x, y, w, h, mapRGBA(color))

  /** Adds an alternative version of this surface, typically used for high dpi
    * representations of cursors or icons. This call adds a reference to the
    * image so you should call image.destroy() after this method returns.
    *
    * @param image
    *   alternative version of this surface, does not need to be the same
    *   format, size, or have the same content as this surface.
    */
  def addAlternateImage(image: Surface): Unit =
    SDL_AddSurfaceAlternateImage(internal, image.internal).sdlErrorCheck()

  def hasAlternateImages: Boolean = SDL_SurfaceHasAlternateImages(internal)

  def removeAlternateImages(): Unit = SDL_RemoveSurfaceAlternateImages(internal)

  def getImages(): Iterator[Surface] =
    new Iterator[Surface]:
      val buffer = SDL_GetSurfaceImages(internal)

      override def next(): Surface =
        if !hasNext then
          throw new NoSuchElementException("no more alternate images")
        val img = buffer.get()
        new Surface(SDL_Surface.create(img))

      override def hasNext: Boolean = buffer.hasRemaining
    end new
  end getImages

  def lock(): Unit = SDL_LockSurface(internal).sdlErrorCheck()

  def unlock(): Unit = SDL_UnlockSurface(internal)

  def mustLock: Boolean = SDL_MUSTLOCK(internal)

  override def toString: String = s"Surface($width, $height, $format)"

end Surface

object Surface:
  type Pos = Point[Int]

  def apply(
      width: Int,
      height: Int,
      format: PixelFormat = PixelFormat.RGBA8888
  ): Surface = new Surface(
    SDL_CreateSurface(width, height, format.internal).sdlCreationCheck()
  )

  def unapply(
      s: Surface
  ): Some[(width: Int, height: Int, format: PixelFormat)] =
    Some(s.width, s.height, s.format)

  private[bearlyb] def fromInternal(internal: SDL_Surface): Surface =
    new Surface(internal)

  def loadImage(file: String): Option[Surface] = imghelper
    .loadSurface(file)
    .map(fromInternal)
  end loadImage

  def loadGIF(file: String): Option[
    (w: Int, h: Int, n: Int, frames: IndexedSeq[(surface: Surface, delay: Int)])
  ] =
    for
      (w, h, n, frames) <- imghelper.loadGIF(file)
      newFrames = frames.map(t => (fromInternal(t.surface), t.delay))
    yield (w, h, n, newFrames)

  def loadBMP(file: String): Option[Surface] = Option(SDL_LoadBMP(file))
    .map(fromInternal)

end Surface
