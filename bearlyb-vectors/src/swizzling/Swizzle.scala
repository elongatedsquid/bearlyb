package bearlyb.vectors.swizzling

import scala.compiletime.ops.int.S
import scala.compiletime.{constValue, erasedValue}
import scala.quoted.*

type Swizzle[T <: Tuple, Indices <: Tuple] <: Tuple = Indices match
  case EmptyTuple => EmptyTuple
  case 0 *: xs    => Tuple.Elem[T, 0] *: Swizzle[T, xs]
  case S[n] *: xs => Tuple.Elem[T, S[n]] *: Swizzle[T, xs]

extension [T <: Tuple](tup: T)

  inline def swiz[I <: Tuple]: Swizzle[T, I] = inline erasedValue[I] match
    case _: EmptyTuple   => EmptyTuple
    case _: (0 *: xs)    => tup(0) *: tup.swiz[xs]
    case _: (S[x] *: xs) => tup(constValue[S[x]]) *: tup.swiz[xs]

// macro
extension [T <: Tuple](tup: T)

  transparent inline def swizzle(inline indices: Int*) =
    ${ swizzleMacro('tup, 'indices) }

private def swizzleMacro[T <: Tuple: Type](
    tup: Expr[T],
    indices: Expr[Seq[Int]]
)(using Quotes): Expr[Tuple] =
  import quotes.reflect.*

  val idxs = indices.valueOrAbort
  val tupleType = idxs
    .map(i => ConstantType(IntConstant(i)).asType)
    .foldRight[Type[? <: Tuple]](Type.of[EmptyTuple]):
      case ('[type i <: Int; i], '[type t <: Tuple; t]) => Type.of[i *: t]
      case _ => report.errorAndAbort("Unhandled types")

  tupleType match
    case '[type t <: Tuple; t] => '{ $tup.swiz[t] }

end swizzleMacro
