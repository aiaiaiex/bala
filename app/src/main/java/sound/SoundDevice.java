package sound;

import java.nio.IntBuffer;
import java.util.logging.Logger;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryUtil;
import utilities.GlobalLogger;

public final class SoundDevice {
    private static final Logger LOGGER = GlobalLogger.getLogger();

    private static SoundDevice soundDevice = null;

    private boolean initialized = false;

    private SoundDevice() {}

    public static SoundDevice getSoundDevice() {
        LOGGER.fine(GlobalLogger.METHOD_CALL);

        if (soundDevice == null) {
            soundDevice = new SoundDevice();
            LOGGER.fine(() -> String.format("New soundDevice: %1$s", soundDevice));
        }

        LOGGER.fine(() -> String.format("Method returned: %1$s", soundDevice));
        return soundDevice;
    }

    public void initialize() {
        LOGGER.fine(GlobalLogger.METHOD_CALL);

        LOGGER.fine("Check if soundDevice is already initialized");
        if (initialized) {
            LOGGER.fine("soundDevice is already initialized");
            LOGGER.fine(GlobalLogger.METHOD_RETURN);
            return;
        }

        LOGGER.fine("Open default sound device");
        long defaultSoundDevice = ALC10.alcOpenDevice((CharSequence) null);

        LOGGER.fine("Check capabilities of defaultSoundDevice");
        ALCCapabilities alcCapabilities = ALC.createCapabilities(defaultSoundDevice);
        if (!alcCapabilities.OpenALC10) {
            LOGGER.severe("defaultSoundDevice does NOT support ALC10");
            throw new RuntimeException("defaultSoundDevice does NOT support ALC10!");
        } else {
            LOGGER.info("defaultSoundDevice supports ALC10");
        }

        LOGGER.fine("Create an ALC context using defaultSoundDevice");
        long alcContext = ALC10.alcCreateContext(defaultSoundDevice, (IntBuffer) null);

        LOGGER.fine("Makes alcContext current");
        ALC10.alcMakeContextCurrent(alcContext);

        LOGGER.fine("Check capabilities of alcContext");
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
        if (!alCapabilities.OpenAL10) {
            LOGGER.severe("alcContext does NOT support AL10");
            throw new RuntimeException("alcContext does NOT support AL10!");
        } else {
            LOGGER.info("alcContext supports AL10");
        }

        LOGGER.fine("Set initialized to true");
        initialized = true;

        LOGGER.fine(GlobalLogger.METHOD_RETURN);
    }

    public void terminate() {
        LOGGER.fine(GlobalLogger.METHOD_CALL);

        LOGGER.fine("Check if soundDevice is even initialized");
        if (!initialized) {
            LOGGER.warning("soundDevice is NOT even initialized");
            LOGGER.fine(GlobalLogger.METHOD_RETURN);
            return;
        }

        LOGGER.fine("Get current ALC context");
        long currentContext = ALC10.alcGetCurrentContext();
        LOGGER.fine("Get current sound device");
        long currentSoundDevice = ALC10.alcGetContextsDevice(currentContext);

        LOGGER.fine("Make no ALC context current");
        ALC10.alcMakeContextCurrent(MemoryUtil.NULL);

        LOGGER.fine("Destroy previously current ALC context");
        ALC10.alcDestroyContext(currentContext);
        LOGGER.fine("Close current sound device");
        ALC10.alcCloseDevice(currentSoundDevice);

        LOGGER.fine("Set initialized to false");
        initialized = false;

        LOGGER.fine(GlobalLogger.METHOD_RETURN);
    }
}
