package procedural;

import org.joml.Vector3f;

public final class SimplexNoise {

    private static final int SEED = 5796;
    private static final float FREQUENCY = 0.01f;

    private static final int PRIME_X = 19;
    private static final int PRIME_Y = 2;
    private static final int PRIME_Z = 73;

    private static final float F_3 = 1.0f / 3.0f;
    private static final float G_3 = 1.0f / 6.0f;
    private static final float G_33 = G_3 * 3 - 1;

    private static final Vector3f[] GRADIENTS = {new Vector3f(1, 1, 0), new Vector3f(-1, 1, 0),
            new Vector3f(1, -1, 0), new Vector3f(-1, -1, 0), new Vector3f(1, 0, 1),
            new Vector3f(-1, 0, 1), new Vector3f(1, 0, -1), new Vector3f(-1, 0, -1),
            new Vector3f(0, 1, 1), new Vector3f(0, -1, 1), new Vector3f(0, 1, -1),
            new Vector3f(0, -1, -1), new Vector3f(1, 1, 0), new Vector3f(0, -1, 1),
            new Vector3f(-1, 1, 0), new Vector3f(0, -1, -1),};

    private SimplexNoise() {}

    public static float getNoise(float x, float y, float z) {
        x *= FREQUENCY;
        y *= FREQUENCY;
        z *= FREQUENCY;

        float t = (x + y + z) * F_3;
        int i = fastFloor(x + t);
        int j = fastFloor(y + t);
        int k = fastFloor(z + t);

        t = (i + j + k) * G_3;
        float x0 = x - (i - t);
        float y0 = y - (j - t);
        float z0 = z - (k - t);

        int i1, j1, k1;
        int i2, j2, k2;

        if (x0 >= y0) {
            if (y0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            } else if (x0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            } else {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
        } else {
            if (y0 < z0) {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            } else if (x0 < z0) {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            } else {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            }
        }

        float x1 = x0 - i1 + G_3;
        float y1 = y0 - j1 + G_3;
        float z1 = z0 - k1 + G_3;
        float x2 = x0 - i2 + F_3;
        float y2 = y0 - j2 + F_3;
        float z2 = z0 - k2 + F_3;
        float x3 = x0 + G_33;
        float y3 = y0 + G_33;
        float z3 = z0 + G_33;

        float n0, n1, n2, n3;

        t = (float) 0.6 - x0 * x0 - y0 * y0 - z0 * z0;
        if (t < 0)
            n0 = 0;
        else {
            t *= t;
            n0 = t * t * gradCoord(SEED, i, j, k, x0, y0, z0);
        }

        t = (float) 0.6 - x1 * x1 - y1 * y1 - z1 * z1;
        if (t < 0)
            n1 = 0;
        else {
            t *= t;
            n1 = t * t * gradCoord(SEED, i + i1, j + j1, k + k1, x1, y1, z1);
        }

        t = (float) 0.6 - x2 * x2 - y2 * y2 - z2 * z2;
        if (t < 0)
            n2 = 0;
        else {
            t *= t;
            n2 = t * t * gradCoord(SEED, i + i2, j + j2, k + k2, x2, y2, z2);
        }

        t = (float) 0.6 - x3 * x3 - y3 * y3 - z3 * z3;
        if (t < 0)
            n3 = 0;
        else {
            t *= t;
            n3 = t * t * gradCoord(SEED, i + 1, j + 1, k + 1, x3, y3, z3);
        }

        return 32 * (n0 + n1 + n2 + n3);
    }

    private static int fastFloor(float f) {
        return f >= 0 ? (int) f : (int) f - 1;
    }

    private static float gradCoord(int seed, int x, int y, int z, float xd, float yd, float zd) {
        int hash = seed;

        hash ^= PRIME_X * x;
        hash ^= PRIME_Y * y;
        hash ^= PRIME_Z * z;

        hash = hash * hash * hash * 60493;
        hash = (hash >> 13) ^ hash;

        Vector3f g = GRADIENTS[hash & 15];

        return xd * g.x + yd * g.y + zd * g.z;
    }
}
