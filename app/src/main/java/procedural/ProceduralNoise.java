package procedural;

import setting.EngineSettings;

public final class ProceduralNoise {
    private ProceduralNoise() {}

    public static float getNoise(float x, float y, float z) {
        if (EngineSettings.USE_PERLIN_NOISE) {
            return PerlinNoise.getNoise(x, y, z);
        } else {
            return SimplexNoise.getNoise(x, y, z);
        }
    }
}
