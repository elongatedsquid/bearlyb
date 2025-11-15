package bearlyb.testing

import munit.FunSuite

import bearlyb.rect.*
import bearlyb.vectors.Vec.given
import org.lwjgl.sdl.SDL_Rect
import org.lwjgl.sdl.SDLRect.*
import org.lwjgl.system.MemoryStack.stackPush
import scala.util.Using

class rect extends FunSuite:

  test("Rect and Line intersection"):
    def compare(
        // x: Int, y: Int, w: Int, h: Int,
        x1: Int,
        y1: Int,
        x2: Int,
        y2: Int
    ): Boolean =
      val (x, y, w, h) = (0, 0, 10, 10)
      val a = Rect(x, y, w, h)
      val intersectionA = a.intersection(x1, y1, x2, y2)

      val intersectionB: Option[(near: Point[Int], far: Point[Int])] =
        Using(stackPush()): stack =>
          val b = SDL_Rect.malloc(stack).set(x, y, w, h)
          val (px1, py1, px2, py2) = (x1, y1, x2, y2).vmap(stack.ints)
          val didIntersect =
            SDL_GetRectAndLineIntersection(b, px1, py1, px2, py2)
          if didIntersect then
            Some(((px1.get(0), py1.get(0)), (px2.get(0), py2.get(0))))
          else None
        .get

      intersectionA == intersectionB
    end compare

    assert(compare(-2, 5, 11, 5))
    assert(compare(5, -25, 5, 16))
    assert(compare(-45, 100, 78, 200))
    assert(compare(3, 4, 7, 8))
    assert(compare(0, 0, 0, 0))

  test("enclose points"):
    assertEquals(Rect.enclosePoints((0, 0)), Rect(0, 0, 1, 1))
    assertEquals(
      Rect.enclosePoints((0, 1), (2, 3), (-1, -3)),
      Rect(-1, -3, 4, 7)
    )

end rect
