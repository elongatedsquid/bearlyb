package bearlyb.render

import org.lwjgl.sdl.SDL_Vertex
import scala.util.Using.Releasable

opaque type VertexBuffer = SDL_Vertex.Buffer
object VertexBuffer:
  def apply[T: Numeric](vs: Vertex[T]*): VertexBuffer = from(vs)
  def from[T: Numeric as num](vs: Seq[Vertex[T]]): VertexBuffer =
    val sdlVerts = SDL_Vertex.calloc(vs.size)
    for (v, i) <- vs.zipWithIndex do
      val Vertex((px, py), (r, g, b, a), (tx, ty)) = v
      val current = sdlVerts.get(i)
      current.position$.set(num.toFloat(px), num.toFloat(py))
      current.color.set(r, g, b, a)
      current.tex_coord.set(num.toFloat(tx), num.toFloat(ty))

    sdlVerts

  extension (buf: VertexBuffer)
    private[bearlyb] def internal: SDL_Vertex.Buffer = buf

    inline def toSeq: Seq[Vertex[Float]] = buf.toIndexedSeq

    def toIndexedSeq: IndexedSeq[Vertex[Float]] =
      IndexedSeq.tabulate(buf.capacity): i =>
        Vertex.fromInternal(buf.get(i))

    def destroy(): Unit =
      summon[Releasable[VertexBuffer]].release(buf)

  extension (inline buf: VertexBuffer)
    inline def mapInPlace(inline f: Vertex[Float] => Vertex[Float]): VertexBuffer =
      for i <- 0 until buf.limit do
        val internalVert = buf.get(i)
        val newVert = f(Vertex.fromInternal(internalVert))
        newVert.internal(internalVert)
      buf

  given Releasable[VertexBuffer]:
    def release(buf: VertexBuffer): Unit = buf.free()
