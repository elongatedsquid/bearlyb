package bearlyb.sensor

import java.nio.FloatBuffer

enum SensorData:
  /** Returned for an invalid sensor */
  case Invalid

  /** Unknown sensor type */
  case Unknown

  /** Accelerometer */
  case Accel(x: Float, y: Float, z: Float)

  /** Gyroscope */
  case Gyro(pitch: Float, yaw: Float, roll: Float)

  /** Accelerometer for left Joy-Con controller and Wii nunchuk */
  case AccelL(x: Float, y: Float, z: Float)

  /** Gyroscope for left Joy-Con controller */
  case GyroL(pitch: Float, yaw: Float, roll: Float)

  /** Accelerometer for right Joy-Con controller */
  case AccelR(x: Float, y: Float, z: Float)

  /** Gyroscope for right Joy-Con controller */
  case GyroR(pitch: Float, yaw: Float, roll: Float)

end SensorData

object SensorData:

  private[bearlyb] def fromInternal(
      sensor: Int,
      data: FloatBuffer
  ): SensorData =
    val (a, b, c) = (data.get(0), data.get(1), data.get(2))
    sensor match
      case -1 => Invalid
      case 0  => Unknown
      case 1  => Accel(a, b, c)
      case 2  => Gyro(a, b, c)
      case 3  => AccelL(a, b, c)
      case 4  => GyroL(a, b, c)
      case 5  => AccelR(a, b, c)
      case 6  => GyroR(a, b, c)
    end match
  end fromInternal

end SensorData
