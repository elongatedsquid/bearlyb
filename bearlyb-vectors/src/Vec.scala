package bearlyb.vectors

import scala.compiletime.ops.int.S

/** Represents a tuple containing only a single type For example `Vec[Int, 3]` =
  * `(Int, Int, Int)`
  */
type Vec[+T, Len <: Int] <: Tuple = Len match
  case 0    => EmptyTuple
  case S[n] => T *: Vec[T, n]

object Vec:

  export VecOps.given
  export swizzling.swizzle
  export swizzling.extensions as swizzleExtensions

  def fill[A](size: Int)(elem: => A)(using ops: VecOps[Vec[A, size.type], A]) =
    ops.fill(elem)

  def zeroed[A: Numeric as num](
      size: Int
  )(using ops: VecOps[Vec[A, size.type], A]) = ops.fill(num.zero)

  extension [T <: Tuple](tup: T)
    inline def x = tup(0)
    inline def y = tup(1)
    inline def z = tup(2)
    inline def w = tup(3)

    inline def r = tup.x
    inline def g = tup.y
    inline def b = tup.z
    inline def a = tup.w
  end extension

end Vec

/** Stuff you can only do with a [[Vec]] */
sealed trait VecOps[Tup <: Tuple, +A]:
  type Return[+T] <: Tuple

  def fill[A1 >: A](elem: => A1): Return[A1]

  extension (tup: Tup)

    def vmap[B](f: A => B): Return[B]
    def foreach[U](f: A => U): Unit
    def count(p: A => Boolean): Int
    def find(p: A => Boolean): Option[A]
    def foldLeft[B](z: B)(op: (B, A) => B): B
    def foldRight[B](z: B)(op: (A, B) => B): B

    def sum[A1 >: A: Numeric as num]: A1 = tup.foldLeft(num.zero)(num.plus)

    def product[A1 >: A: Numeric as num]: A1 = tup.foldLeft(num.one)(num.times)

    infix def dot[A1 >: A: Numeric as num](other: Tup): A1 = tup.iterator
      .zip(other.iterator)
      .map(num.times.tupled)
      .sum

    infix def mul[A1 >: A: Numeric as num](
        other: Tup
    )(using VecOps[Tuple.Zip[Tup, Tup], (A1, A1)]) =
      tup.zip(other).vmap(num.times.tupled)

    inline infix def div[A1 >: A: ([x] =>> Fractional[x] | Integral[x]) as num](
        other: Tup
    )(using VecOps[Tuple.Zip[Tup, Tup], (A1, A1)]) = inline num match
      case frac: Fractional[A1] => tup.zip(other).vmap(frac.div)
      case int: Integral[A1]    => tup.zip(other).vmap(int.quot)

    def +[A1 >: A: Numeric as num](
        other: Tup
    )(using VecOps[Tuple.Zip[Tup, Tup], (A1, A1)]) =
      tup.zip(other).vmap(num.plus)

    def -[A1 >: A: Numeric as num](
        other: Tup
    )(using VecOps[Tuple.Zip[Tup, Tup], (A1, A1)]) =
      tup.zip(other).vmap(num.minus)

    inline def /[A1 >: A: ([x] =>> Fractional[x] | Integral[x]) as num](
        scalar: A1
    ) = inline num match
      case frac: Fractional[A1] => tup.vmap(frac.div(_, scalar))
      case int: Integral[A1]    => tup.vmap(int.quot(_, scalar))

    def *[A1 >: A: Numeric as num](scalar: A1) = tup.vmap(num.times(_, scalar))

    def iterator: Iterator[A] = tup.productIterator.asInstanceOf[Iterator[A]]

  end extension

end VecOps

object VecOps:

  given VecOps[EmptyTuple, Nothing] with
    type Return[+T] = EmptyTuple

    def fill[A1 >: Nothing](elem: => A1): Return[A1] = EmptyTuple

    extension (tup: EmptyTuple)
      def vmap[B](f: Nothing => B): Return[B] = EmptyTuple
      def foreach[U](f: Nothing => U): Unit = ()
      def count(p: Nothing => Boolean): Int = 0

      def find(p: Nothing => Boolean): Option[Nothing] = None

      def foldLeft[B](z: B)(op: (B, Nothing) => B): B = z
      def foldRight[B](z: B)(op: (Nothing, B) => B): B = z

    end extension

  end given

  given [
      A,
      Tail <: Tuple,
      TailVecOps <: VecOps[Tail, A]
  ]
    => (
        tailVecOps: TailVecOps
  ) => VecOps[A *: Tail, A]:
    type Return[+T] = T *: tailVecOps.Return[T]

    def fill[A1 >: A](elem: => A1): Return[A1] = elem *: tailVecOps.fill(elem)

    extension (tup: A *: Tail)

      def vmap[B](f: A => B): Return[B] = f(tup.head) *: tup.tail.vmap(f)

      def foreach[U](f: A => U): Unit =
        f(tup.head)
        tup.tail.foreach(f)

      def count(p: A => Boolean): Int =
        val x = if p(tup.head) then 1 else 0
        x + tup.tail.count(p)

      def find(p: A => Boolean): Option[A] =
        val x *: xs = tup
        if p(x) then Some(x) else xs.find(p)

      def foldLeft[B](z: B)(op: (B, A) => B): B =
        val acc = op(z, tup.head)
        tup.tail.foldLeft(acc)(op)

      def foldRight[B](z: B)(op: (A, B) => B): B =
        val result = tup.tail.foldRight(z)(op)
        op(tup.head, result)

    end extension

  end given

end VecOps
