package procedural;

public final class PerlinNoise {

    private static final int SEED = 5796;
    private static final float FREQUENCY = 0.01f;

    private static final int PRIME_X = 19;
    private static final int PRIME_Y = 2;
    private static final int PRIME_Z = 73;

    private static final float[] GRADIENTS = {0, 1, 1, 0, 0, -1, 1, 0, 0, 1, -1, 0, 0, -1, -1, 0, 1,
            0, 1, 0, -1, 0, 1, 0, 1, 0, -1, 0, -1, 0, -1, 0, 1, 1, 0, 0, -1, 1, 0, 0, 1, -1, 0, 0,
            -1, -1, 0, 0, 0, 1, 1, 0, 0, -1, 1, 0, 0, 1, -1, 0, 0, -1, -1, 0, 1, 0, 1, 0, -1, 0, 1,
            0, 1, 0, -1, 0, -1, 0, -1, 0, 1, 1, 0, 0, -1, 1, 0, 0, 1, -1, 0, 0, -1, -1, 0, 0, 0, 1,
            1, 0, 0, -1, 1, 0, 0, 1, -1, 0, 0, -1, -1, 0, 1, 0, 1, 0, -1, 0, 1, 0, 1, 0, -1, 0, -1,
            0, -1, 0, 1, 1, 0, 0, -1, 1, 0, 0, 1, -1, 0, 0, -1, -1, 0, 0, 0, 1, 1, 0, 0, -1, 1, 0,
            0, 1, -1, 0, 0, -1, -1, 0, 1, 0, 1, 0, -1, 0, 1, 0, 1, 0, -1, 0, -1, 0, -1, 0, 1, 1, 0,
            0, -1, 1, 0, 0, 1, -1, 0, 0, -1, -1, 0, 0, 0, 1, 1, 0, 0, -1, 1, 0, 0, 1, -1, 0, 0, -1,
            -1, 0, 1, 0, 1, 0, -1, 0, 1, 0, 1, 0, -1, 0, -1, 0, -1, 0, 1, 1, 0, 0, -1, 1, 0, 0, 1,
            -1, 0, 0, -1, -1, 0, 0, 1, 1, 0, 0, 0, -1, 1, 0, -1, 1, 0, 0, 0, -1, -1, 0};

    private PerlinNoise() {}

    public static float getNoise(float x, float y, float z) {
        x *= FREQUENCY;
        y *= FREQUENCY;
        z *= FREQUENCY;

        int x0 = fastFloor(x);
        int y0 = fastFloor(y);
        int z0 = fastFloor(z);

        float xd0 = x - x0;
        float yd0 = y - y0;
        float zd0 = z - z0;
        float xd1 = xd0 - 1;
        float yd1 = yd0 - 1;
        float zd1 = zd0 - 1;

        float xs = interpQuintic(xd0);
        float ys = interpQuintic(yd0);
        float zs = interpQuintic(zd0);

        x0 *= PRIME_X;
        y0 *= PRIME_Y;
        z0 *= PRIME_Z;
        int x1 = x0 + PRIME_X;
        int y1 = y0 + PRIME_Y;
        int z1 = z0 + PRIME_Z;

        float xf00 = lerp(gradCoord(SEED, x0, y0, z0, xd0, yd0, zd0),
                gradCoord(SEED, x1, y0, z0, xd1, yd0, zd0), xs);
        float xf10 = lerp(gradCoord(SEED, x0, y1, z0, xd0, yd1, zd0),
                gradCoord(SEED, x1, y1, z0, xd1, yd1, zd0), xs);
        float xf01 = lerp(gradCoord(SEED, x0, y0, z1, xd0, yd0, zd1),
                gradCoord(SEED, x1, y0, z1, xd1, yd0, zd1), xs);
        float xf11 = lerp(gradCoord(SEED, x0, y1, z1, xd0, yd1, zd1),
                gradCoord(SEED, x1, y1, z1, xd1, yd1, zd1), xs);

        float yf0 = lerp(xf00, xf10, ys);
        float yf1 = lerp(xf01, xf11, ys);

        return lerp(yf0, yf1, zs) * 0.964921414852142333984375f;
    }

    private static int fastFloor(float f) {
        return f >= 0 ? (int) f : (int) f - 1;
    }

    private static float interpQuintic(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private static float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }

    private static float gradCoord(int seed, int xPrimed, int yPrimed, int zPrimed, float xd,
            float yd, float zd) {
        int hash = hash(seed, xPrimed, yPrimed, zPrimed);

        hash ^= hash >> 15;
        hash &= 63 << 2;

        float xg = GRADIENTS[hash];
        float yg = GRADIENTS[hash | 1];
        float zg = GRADIENTS[hash | 2];

        return xd * xg + yd * yg + zd * zg;
    }

    private static int hash(int seed, int xPrimed, int yPrimed, int zPrimed) {
        int hash = seed ^ xPrimed ^ yPrimed ^ zPrimed;

        hash *= 0x27d4eb2d;
        return hash;
    }
}
