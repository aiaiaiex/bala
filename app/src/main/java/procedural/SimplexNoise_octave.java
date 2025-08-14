package procedural;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Optimized 2D/3D Simplex Noise generator with baked-in scaling to [-0.70, 0.70].
 * 
 * - Uses deterministic patterns if a seed is provided. - Uses random patterns each run if no seed
 * is specified. - High performance: avoids unnecessary object creation in the noise loop. - Scaled
 * so output always falls within [-0.70, 0.70].
 */
public class SimplexNoise_octave {

    private static final int GRAD3[][] = {{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1},
            {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}};

    private static final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
    private static final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;

    private final int[] perm = new int[512]; // permutation table
    private final int[] permMod12 = new int[512]; // gradient index table

    /**
     */
    public SimplexNoise_octave() {
        this(new SecureRandom().nextInt());
    }

    /**
     * Constructor — deterministic noise with given seed.
     * 
     * @param seed The seed for permutation shuffling
     */
    public SimplexNoise_octave(int seed) {
        int[] p = new int[256];
        for (int i = 0; i < 256; i++)
            p[i] = i;

        // Shuffle using seed
        Random rand = new Random(seed);
        for (int i = 255; i > 0; i--) {
            int swapIndex = rand.nextInt(i + 1);
            int temp = p[i];
            p[i] = p[swapIndex];
            p[swapIndex] = temp;
        }

        // Duplicate the table for overflow-free lookups
        for (int i = 0; i < 512; i++) {
            perm[i] = p[i & 255];
            permMod12[i] = perm[i] % 12;
        }
    }

    /**
     * 2D Simplex noise scaled to [-0.70, 0.70].
     */
    public double noise(double xin, double yin) {
        double n0, n1, n2; // noise contributions

        // Skew input space to determine which simplex cell
        double s = (xin + yin) * F2;
        int i = fastFloor(xin + s);
        int j = fastFloor(yin + s);

        double t = (i + j) * G2;
        double X0 = i - t; // unskew origin
        double Y0 = j - t;
        double x0 = xin - X0;
        double y0 = yin - Y0;

        // Determine simplex corner offsets
        int i1, j1;
        if (x0 > y0) {
            i1 = 1;
            j1 = 0;
        } else {
            i1 = 0;
            j1 = 1;
        }

        double x1 = x0 - i1 + G2;
        double y1 = y0 - j1 + G2;
        double x2 = x0 - 1.0 + 2.0 * G2;
        double y2 = y0 - 1.0 + 2.0 * G2;

        // Hash the coordinates
        int ii = i & 255;
        int jj = j & 255;

        // Calculate contribution from first corner
        double t0 = 0.5 - x0 * x0 - y0 * y0;
        if (t0 < 0)
            n0 = 0.0;
        else {
            t0 *= t0;
            int gi0 = permMod12[ii + perm[jj]];
            n0 = t0 * t0 * dot(GRAD3[gi0], x0, y0);
        }

        // Second corner
        double t1 = 0.5 - x1 * x1 - y1 * y1;
        if (t1 < 0)
            n1 = 0.0;
        else {
            t1 *= t1;
            int gi1 = permMod12[ii + i1 + perm[jj + j1]];
            n1 = t1 * t1 * dot(GRAD3[gi1], x1, y1);
        }

        // Third corner
        double t2 = 0.5 - x2 * x2 - y2 * y2;
        if (t2 < 0)
            n2 = 0.0;
        else {
            t2 *= t2;
            int gi2 = permMod12[ii + 1 + perm[jj + 1]];
            n2 = t2 * t2 * dot(GRAD3[gi2], x2, y2);
        }

        // Scale result to [-0.70, 0.70]
        double rawNoise = 70.0 * (n0 + n1 + n2);
        if (rawNoise > 1)
            rawNoise = 1;
        else if (rawNoise < -1)
            rawNoise = -1;
        return rawNoise * 0.70;
    }

    // Faster floor without casting overhead
    private static int fastFloor(double x) {
        int xi = (int) x;
        return x < xi ? xi - 1 : xi;
    }

    // Dot product helper
    private static double dot(int[] g, double x, double y) {
        return g[0] * x + g[1] * y;
    }
}
