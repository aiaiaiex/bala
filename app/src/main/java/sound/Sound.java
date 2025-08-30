package sound;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.libc.LibCStdlib;
import logger.GlobalLogger;

public class Sound {
    private int bufferId;
    private int sourceId;
    private String filepath;

    private boolean isPlaying = false;

    public Sound(String filepath, boolean loops) {
        this.filepath = filepath;

        MemoryStack.stackPush();
        IntBuffer channelsBuffer = MemoryStack.stackMallocInt(1);
        MemoryStack.stackPush();
        IntBuffer sampleRateBuffer = MemoryStack.stackMallocInt(1);

        ShortBuffer rawAudioBuffer =
                STBVorbis.stb_vorbis_decode_filename(filepath, channelsBuffer, sampleRateBuffer);
        if (rawAudioBuffer == null) {
            GlobalLogger.getGlobalLogger().getLogger()
                    .warning(() -> String.format("filepath=%1$s not found", filepath));
            MemoryStack.stackPop();
            MemoryStack.stackPop();
            return;
        }

        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();
        MemoryStack.stackPop();
        MemoryStack.stackPop();

        int format = -1;
        if (channels == 1) {
            format = AL10.AL_FORMAT_MONO16;
        } else if (channels == 2) {
            format = AL10.AL_FORMAT_STEREO16;
        }

        bufferId = AL10.alGenBuffers();
        AL10.alBufferData(bufferId, format, rawAudioBuffer, sampleRate);

        sourceId = AL10.alGenSources();

        AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);
        AL10.alSourcei(sourceId, AL10.AL_LOOPING, loops ? 1 : 0);
        AL10.alSourcei(sourceId, AL10.AL_POSITION, 0);
        AL10.alSourcef(sourceId, AL10.AL_GAIN, 0.3f);

        LibCStdlib.free(rawAudioBuffer);
    }

    public void delete() {
        AL10.alDeleteSources(sourceId);
        AL10.alDeleteBuffers(bufferId);
    }

    public void play() {
        int state = AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE);
        if (state == AL10.AL_STOPPED) {
            isPlaying = false;
            AL10.alSourcei(sourceId, AL10.AL_POSITION, 0);
        }

        if (!isPlaying) {
            AL10.alSourcePlay(sourceId);
            isPlaying = true;
        }
    }

    public void stop() {
        if (isPlaying) {
            AL10.alSourceStop(sourceId);
            isPlaying = false;
        }
    }

    public String getFilepath() {
        return filepath;
    }

    public boolean isPlaying() {
        int state = AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE);
        if (state == AL10.AL_STOPPED) {
            isPlaying = false;
        }
        return isPlaying;
    }
}
