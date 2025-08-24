package sound;

import java.util.logging.Logger;
import utilities.GlobalLogger;

public final class SoundDevice {
    private static final Logger LOGGER = GlobalLogger.getLogger();

    private static SoundDevice soundDevice = null;

    private SoundDevice() {}

    public static SoundDevice getSoundDevice() {
        LOGGER.fine(GlobalLogger.METHOD_CALL);

        if (soundDevice == null) {
            LOGGER.fine(() -> String.format("Old soundDevice: %1$s", soundDevice));
            soundDevice = new SoundDevice();
            LOGGER.fine(() -> String.format("New soundDevice: %1$s", soundDevice));
        }

        LOGGER.fine(() -> String.format("Method returned: %1$s", soundDevice));
        return soundDevice;
    }
}
