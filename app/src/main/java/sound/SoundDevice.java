package sound;

import java.nio.IntBuffer;
import java.util.logging.Logger;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryUtil;
import logger.GlobalLogger;

public final class SoundDevice {
    private static SoundDevice soundDevice = null;

    private boolean initialized = false;

    private Logger globalLogger;

    private SoundDevice() {
        globalLogger = GlobalLogger.getGlobalLogger().getLogger();
    }

    public static SoundDevice getSoundDevice() {
        if (soundDevice == null) {
            soundDevice = new SoundDevice();
        }

        return soundDevice;
    }

    public void initialize() {
        if (initialized) {
            return;
        }

        long defaultSoundDevice = ALC10.alcOpenDevice((CharSequence) null);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(defaultSoundDevice);
        if (!alcCapabilities.OpenALC10) {
            globalLogger.severe("defaultSoundDevice does NOT support ALC10");
            throw new RuntimeException("defaultSoundDevice does NOT support ALC10!");
        } else {
            globalLogger.info("defaultSoundDevice supports ALC10");
        }

        long alcContext = ALC10.alcCreateContext(defaultSoundDevice, (IntBuffer) null);

        ALC10.alcMakeContextCurrent(alcContext);

        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
        if (!alCapabilities.OpenAL10) {
            globalLogger.severe("alcContext does NOT support AL10");
            throw new RuntimeException("alcContext does NOT support AL10!");
        } else {
            globalLogger.info("alcContext supports AL10");
        }

        initialized = true;

    }

    public void terminate() {
        if (!initialized) {
            globalLogger.warning("soundDevice is NOT even initialized");
            return;
        }

        long currentContext = ALC10.alcGetCurrentContext();
        long currentSoundDevice = ALC10.alcGetContextsDevice(currentContext);

        ALC10.alcMakeContextCurrent(MemoryUtil.NULL);

        ALC10.alcDestroyContext(currentContext);
        ALC10.alcCloseDevice(currentSoundDevice);

        initialized = false;
    }
}
