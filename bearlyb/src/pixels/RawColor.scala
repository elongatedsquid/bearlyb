package bearlyb.pixels

opaque type RawColor = Int

object RawColor:
  inline def fromInt(inline inner: Int): RawColor = inner
  private[bearlyb] inline def apply(inline inner: Int): RawColor = inner

  extension (inline color: RawColor)
    inline def toInt: Int = color
    private[bearlyb] inline def internal: Int = color
