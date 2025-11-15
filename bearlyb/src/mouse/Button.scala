package bearlyb.mouse

import scala.collection.immutable.BitSet

// enum Button:
//   case Left, Middle, Right, X1, X2

//   private[bearlyb] def internal: Int = this.ordinal + 1

extension (internal: Int)

  private[bearlyb] def toMouseButtonSet: Set[Button] = BitSet
    .fromBitMaskNoCopy(Array(internal))

opaque type Button = Int

object Button:
  val Left: Button = 0
  val Middle: Button = 1
  val Right: Button = 2
  val X1: Button = 3
  val X2: Button = 4

  extension (b: Button) private[bearlyb] def internal: Int = b + 1

  private[bearlyb] def fromInternal(internal: Int): Button = internal - 1

end Button
