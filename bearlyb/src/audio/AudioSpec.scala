package bearlyb.audio

import org.lwjgl.sdl.SDL_AudioSpec
import org.lwjgl.system.MemoryStack

case class AudioSpec(format: AudioFormat, channels: Int, freq: Int):

  private[bearlyb] def internal(stack: MemoryStack): SDL_AudioSpec =
    SDL_AudioSpec
      .malloc(stack)
      .format(format.internal)
      .channels(channels)
      .freq(freq)

object AudioSpec:

  extension (spec: AudioSpec | Null)

    def internal(stack: MemoryStack): SDL_AudioSpec | Null = spec match
      case null                              => null
      case AudioSpec(format, channels, freq) =>
        SDL_AudioSpec
          .malloc(stack)
          .format(format.internal)
          .channels(channels)
          .freq(freq)
    end internal

  private[bearlyb] def fromInternal(internal: SDL_AudioSpec): AudioSpec =
    val format = AudioFormat.fromInternal(internal.format)
    new AudioSpec(format, internal.channels, internal.freq)

end AudioSpec
