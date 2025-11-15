package bearlyb.audio

import bearlyb.util.*
import org.lwjgl.sdl.SDLAudio.*
import org.lwjgl.sdl.SDLStdinc.SDL_free
import org.lwjgl.sdl.{SDL_AudioSpec, SDL_AudioStreamCallbackI}

import java.nio.ByteBuffer

class AudioStream private (private[bearlyb] val internal: Long):
  def destroy(): Unit = SDL_DestroyAudioStream(internal)

  def pause(): Unit = SDL_PauseAudioStreamDevice(internal).sdlErrorCheck()

  def resume(): Unit = SDL_ResumeAudioStreamDevice(internal).sdlErrorCheck()

  def isPaused: Boolean = SDL_AudioStreamDevicePaused(internal)

  def clear(): Unit = SDL_ClearAudioStream(internal).sdlErrorCheck()

  def flush(): Unit = SDL_FlushAudioStream(internal).sdlErrorCheck()

  /** @return The number of converted/resampled bytes available */
  def available: Int =
    val avail = SDL_GetAudioStreamAvailable(internal)
    if avail == -1 then sdlError() else avail

  def device: Option[AudioDevice] = SDL_GetAudioStreamDevice(internal) match
    case 0 => None
    case n => Some(AudioDevice.fromInternal(n))

  def format: (input: AudioSpec, output: AudioSpec) = withStack:
    val input = SDL_AudioSpec.malloc(stack)
    val output = SDL_AudioSpec.malloc(stack)
    SDL_GetAudioStreamFormat(internal, input, output).sdlErrorCheck()
    (AudioSpec.fromInternal(input), AudioSpec.fromInternal(output))

  private def setFormat(
      input: AudioSpec | Null,
      output: AudioSpec | Null
  ): Unit = withStack:
    val src = input match
      case null            => null
      case spec: AudioSpec => spec.internal(stack)
    val dst = output match
      case null            => null
      case spec: AudioSpec => spec.internal(stack)
    SDL_SetAudioStreamFormat(internal, src, dst).sdlErrorCheck()

  def format_=(format: (input: AudioSpec, output: AudioSpec)): Unit =
    val (input, output) = format
    setFormat(input, output)

  def inputFormat: AudioSpec = withStack:
    val input = SDL_AudioSpec.malloc(stack)
    SDL_GetAudioStreamFormat(internal, input, null).sdlErrorCheck()
    AudioSpec.fromInternal(input)
  def inputFormat_=(spec: AudioSpec) = setFormat(spec, null)

  def outputFormat: AudioSpec = withStack:
    val output = SDL_AudioSpec.malloc(stack)
    SDL_GetAudioStreamFormat(internal, null, output).sdlErrorCheck()
    AudioSpec.fromInternal(output)
  def outputFormat_=(spec: AudioSpec) = setFormat(null, spec)

  /** @return the frequency ratio between 0.01 and 100 */
  def freqRatio: Float =
    val freqRatio = SDL_GetAudioStreamFrequencyRatio(internal)
    if freqRatio == 0.0f then sdlError() else freqRatio

  /** Sets the "frequency ratio" of this audio stream. The frequency ratio
    * controls how fast the audio plays on this stream and consequently the
    * pitch of the audio as well. The normal ratio is 1.0f. A ratio higher than
    * 1.0 makes the audio play faster and higher-pitched, wihle a ratio lower
    * than 1.0 makes the audio play slower and lower-pitched. The ratio must be
    * within 0.01 < ratio < 100
    *
    * @param ratio
    *   the new frequency ratio
    */
  def freqRatio_=(ratio: Float): Unit =
    if ratio < 0.01f || ratio > 100f then
      throw IllegalArgumentException(
        s"Ratio must be between 0.01 and 100, was $ratio"
      )
    SDL_SetAudioStreamFrequencyRatio(internal, ratio).sdlErrorCheck()

  def gain: Float =
    val result = SDL_GetAudioStreamGain(internal)
    if result == -1.0f then sdlError() else result

  def gain_=(gain: Float): Unit = SDL_SetAudioStreamGain(internal, gain)
    .sdlErrorCheck()

  private def setPutOrGetCallback(
      setter: (Long, SDL_AudioStreamCallbackI, Long) => Boolean,
      callback: AudioStreamCallback
  ): Unit =
    val internalCallback: SDL_AudioStreamCallbackI =
      (_, stream, additionalAmount, totalAmount) =>
        callback(
          AudioStream.fromInternal(stream),
          additionalAmount,
          totalAmount
        )

    setter(internal, internalCallback, NullPtr).sdlErrorCheck()

  end setPutOrGetCallback

  /** This callback is run before any data is obtained from the stream, which
    * gives it the opportunity to add more data
    *
    * @param callback
    */
  def setGetCallback(callback: AudioStreamCallback): Unit =
    setPutOrGetCallback(SDL_SetAudioStreamGetCallback, callback)

  def setPutCallback(callback: AudioStreamCallback): Unit =
    setPutOrGetCallback(SDL_SetAudioStreamPutCallback, callback)

  private def getChannelMap(
      target: Long => java.nio.IntBuffer
  ): Option[IArray[Int]] =
    target(internal).asInstanceOf[java.nio.IntBuffer | Null] match
      case null        => None
      case internalMap =>
        val result: Array[Int] = Array.ofDim(internalMap.remaining)
        internalMap.get(0, result)
        SDL_free(internalMap)
        Some(IArray.unsafeFromArray(result))

  private def setChannelMap(
      target: (Long, java.nio.IntBuffer | Null) => Boolean,
      map: Option[IArray[Int]]
  ): Unit = map match
    case None      => target(internal, null).sdlErrorCheck()
    case Some(map) =>
      withStack:
        val chmap = stack.mallocInt(map.length)
        chmap.put(0, map.unsafeArray)
        target(internal, chmap).sdlErrorCheck()

  def inputChannelMap: Option[IArray[Int]] =
    getChannelMap(SDL_GetAudioStreamInputChannelMap)

  def inputChannelMap_=(map: Option[IArray[Int]]) =
    setChannelMap(SDL_SetAudioStreamInputChannelMap, map)

  def outputChannelMap: Option[IArray[Int]] =
    getChannelMap(SDL_GetAudioStreamOutputChannelMap)

  def outputChannelMap_=(map: Option[IArray[Int]]): Unit =
    setChannelMap(SDL_SetAudioStreamOutputChannelMap, map)

  def queued: Int =
    val result = SDL_GetAudioStreamQueued(internal)
    if result == -1 then sdlError() else result

  def get(max: Int): Array[Byte] =
    val buf = ByteBuffer.allocate(max)
    val read = SDL_GetAudioStreamData(internal, buf)

    if read == -1 then sdlError() else buf.array().take(read)

  def put(data: IndexedSeq[Byte]): Unit = withStack:
    import org.lwjgl.system.MemoryUtil.{memAlloc, memFree}
    val buf = memAlloc(data.length)
    try
      buf.put(0, data.toArray)
      SDL_PutAudioStreamData(internal, buf).sdlErrorCheck()
    finally memFree(buf)

  def lock(): Unit = SDL_LockAudioStream(internal).sdlErrorCheck()

  def unlock(): Unit = SDL_UnlockAudioStream(internal).sdlErrorCheck()

  def bind(dev: AudioDevice): Unit = SDL_BindAudioStream(dev.internal, internal)
    .sdlErrorCheck()

  def unbind(): Unit = SDL_UnbindAudioStream(internal)

end AudioStream

object AudioStream:

  def apply(
      srcSpec: AudioSpec | Null = null,
      dstSpec: AudioSpec | Null = null
  ): AudioStream =
    val internal = withStack:
      val srcinternal = srcSpec.internal(stack) match
        case spec: SDL_AudioSpec => spec.address
        case null                => NullPtr
      val dstinternal = dstSpec.internal(stack) match
        case spec: SDL_AudioSpec => spec.address
        case null                => NullPtr
      nSDL_CreateAudioStream(srcinternal, dstinternal)
      // .sdlCreationCheck()
    fromInternal(internal)
  end apply

  def unbind(stream: AudioStream, streams: AudioStream*): Unit =
    unbind(stream +: streams)

  def unbind(streams: Seq[AudioStream]): Unit = withStack:
    val targetBuf = stack.mallocPointer(streams.length)
    val inputBuf = streams.iterator.map(_.internal).toArray
    targetBuf.put(inputBuf)
    SDL_UnbindAudioStreams(targetBuf)

  def bind(dev: AudioDevice, stream: AudioStream, streams: AudioStream*): Unit =
    bind(dev, stream +: streams)

  def bind(dev: AudioDevice, streams: Seq[AudioStream]): Unit = withStack:
    val targetBuf = stack.mallocPointer(streams.length)
    val inputBuf = streams.iterator.map(_.internal).toArray
    targetBuf.put(inputBuf)
    targetBuf.rewind()
    SDL_BindAudioStreams(dev.internal, targetBuf).sdlErrorCheck()

  private[bearlyb] def fromInternal(internal: Long): AudioStream =
    new AudioStream(internal)

end AudioStream
