package bearlyb.util

import org.lwjgl.sdl.SDLError.SDL_GetError
import org.lwjgl.system.MemoryStack
import scala.compiletime.ops.int.S
import java.nio.{ByteBuffer, IntBuffer, LongBuffer, FloatBuffer, DoubleBuffer}
import scala.util.Using

private[bearlyb] inline val NullPtr = org.lwjgl.system.MemoryUtil.NULL

private[bearlyb] def sdlError(): Nothing =
  throw bearlyb.BearlybException(s"SDL Error: ${SDL_GetError()}")

private[bearlyb] def withStack[T](body: MemoryStack ?=> T): T = Using
  .resource(MemoryStack.stackPush())(stk => body(using stk))

private[bearlyb] def stack(using stk: MemoryStack) = stk

extension (success: Boolean)

  private[bearlyb] def sdlErrorCheck[T](value: T = ()): T =
    if success then value else sdlError()

extension (ptr: Long)

  private[bearlyb] def sdlCreationCheck(): Long =
    if ptr != NullPtr then ptr else sdlError()

extension [T](obj: T)

  private[bearlyb] def sdlCreationCheck(): T =
    if obj != null then obj else sdlError()

sealed private[bearlyb] trait MallocMany[N <: Int]:
  type Return[+T] <: Tuple

  def mallocMany(stack: MemoryStack): Return[ByteBuffer]
  def mallocManyInt(stack: MemoryStack): Return[IntBuffer]
  def mallocManyLong(stack: MemoryStack): Return[LongBuffer]

  def mallocManyFloat(stack: MemoryStack): Return[FloatBuffer]

  def mallocManyDouble(stack: MemoryStack): Return[DoubleBuffer]

end MallocMany

private[bearlyb] object MallocMany:

  given MallocMany[0]:
    type Return[+T] = EmptyTuple

    def mallocMany(stack: MemoryStack): Return[ByteBuffer] = EmptyTuple

    def mallocManyInt(stack: MemoryStack): Return[IntBuffer] = EmptyTuple

    def mallocManyLong(stack: MemoryStack): Return[LongBuffer] = EmptyTuple

    def mallocManyFloat(stack: MemoryStack): Return[FloatBuffer] = EmptyTuple

    def mallocManyDouble(stack: MemoryStack): Return[DoubleBuffer] = EmptyTuple

  end given

  given [N <: Int, TailMalloc <: MallocMany[N]] => (tailMalloc: TailMalloc)
      => MallocMany[S[N]]:
    type Return[+T] = T *: tailMalloc.Return[T]

    def mallocMany(stack: MemoryStack): Return[ByteBuffer] = stack.malloc(1) *:
      tailMalloc.mallocMany(stack)

    def mallocManyInt(stack: MemoryStack): Return[IntBuffer] = stack
      .mallocInt(1) *: tailMalloc.mallocManyInt(stack)

    def mallocManyLong(stack: MemoryStack): Return[LongBuffer] = stack
      .mallocLong(1) *: tailMalloc.mallocManyLong(stack)

    def mallocManyFloat(stack: MemoryStack): Return[FloatBuffer] = stack
      .mallocFloat(1) *: tailMalloc.mallocManyFloat(stack)

    def mallocManyDouble(stack: MemoryStack): Return[DoubleBuffer] = stack
      .mallocDouble(1) *: tailMalloc.mallocManyDouble(stack)

  end given

end MallocMany

private[bearlyb] def mallocMany(
    n: Int,
    stack: MemoryStack
  )(using malloc: MallocMany[n.type]
  ): malloc.Return[ByteBuffer] = malloc.mallocMany(stack)

private[bearlyb] def mallocManyInt(
    n: Int,
    stack: MemoryStack
  )(using malloc: MallocMany[n.type]
  ): malloc.Return[IntBuffer] = malloc.mallocManyInt(stack)

private[bearlyb] def mallocManyLong(
    n: Int,
    stack: MemoryStack
  )(using malloc: MallocMany[n.type]
  ): malloc.Return[LongBuffer] = malloc.mallocManyLong(stack)

private[bearlyb] def mallocManyFloat(
    n: Int,
    stack: MemoryStack
  )(using malloc: MallocMany[n.type]
  ): malloc.Return[FloatBuffer] = malloc.mallocManyFloat(stack)

private[bearlyb] def mallocManyDouble(
    n: Int,
    stack: MemoryStack
  )(using malloc: MallocMany[n.type]
  ): malloc.Return[DoubleBuffer] = malloc.mallocManyDouble(stack)
