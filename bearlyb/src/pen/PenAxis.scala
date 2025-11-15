package bearlyb.pen

enum PenAxis:
  /** Pen pressure.  Unidirectional: 0 to 1.0 */
  case Pressure

  /** Pen horizontal tilt angle. Bidirectional: -90.0 to 90.0 (left-to-right).
    */
  case XTilt

  /** Pen vertical tilt angle. Bidirectional: -90.0 to 90.0 (top-to-down). */
  case YTilt

  /** Pen distance to drawing surface. Unidirectional: 0.0 to 1.0 */
  case Distance

  /** Pen barrel rotation. Bidirectional: -180 to 179.9 (clockwise, 0 is facing
    * up, -180.0 is facing down).
    */
  case Rotation

  /** Pen finger wheel or slider (e.g., Airbrush Pen). Unidirectional: 0 to 1.0
    */
  case Slider

  /** Pressure from squeezing the pen ("barrel pressure"). */
  case TangentialPressure

  def internal: Int = ordinal

end PenAxis

object PenAxis:

  private[bearlyb] def fromInternal(internal: Int) = PenAxis
    .fromOrdinal(internal)
