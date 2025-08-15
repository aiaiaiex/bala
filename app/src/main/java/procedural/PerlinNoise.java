// https://gist.github.com/alksily/7a85a1898e65c936f861ee93516e397d#file-noisegenerator-java
// https://gist.github.com/Fataho/5b422037a6fdcb21c9134ef34d2fa79a

package procedural;

import java.util.Random;

public class PerlinNoise {
    private double seed; // noise pattern
    private int[] p; // 512-length of permutation, avoid extra modulo wrapping
    private int[] permutation; // fixed table of 256 integers (0–255),
                               // Ken Perlin’s original permutation table

    // private long default_size; scale of noise grid (how zoomed in, not used yet)

    public PerlinNoise(double seed) {
        this.seed = seed;
        init();
    }

    public PerlinNoise() {
        this.seed = new Random().nextGaussian() * 255;
        init();
    }

    private void init() {
        // initialize permutation array
        this.p = new int[512];
        this.permutation = new int[] {151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233,
                7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247,
                120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88,
                237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48,
                27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41,
                55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132,
                187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198,
                173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82,
                85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213,
                119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22,
                39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228,
                251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14,
                239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45,
                127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195,
                78, 66, 215, 61, 156, 180};

        // this.default_size = 35;

        // populate it
        for (int i = 0; i < 256; i++) {
            p[256 + i] = p[i] = permutation[i];
        }

    }

    public void setSeed(double seed) {
        this.seed = seed;
    }

    // random generate if seed is not provided
    public double getSeed() {
        return this.seed;
    }

    // noise generate
    public double noise(double x, double y) {

        // four octaves
        double value = 0.0;
        double amplitude = 1.0; // details
        double frequency = 1.0; // zoom
        double maxValue = 0.0; // normalize [-1,1]

        for (int i = 0; i < 4; i++) { // 4 octaves
            value += smoothNoise(x * frequency, y * frequency) * amplitude;
            maxValue += amplitude;
            amplitude *= 0.5; // impact on each octave
            frequency *= 2.0; // increase detail
        }

        return (value / maxValue) * 0.70; // normalize from [-1,1] to [-0.7,0.7]
    }

    public double smoothNoise(double x, double y) {
        // offset each coordinate by the seed value / add seed
        x += this.seed;
        y += this.seed;

        // find grid cell (unit square containing the point)
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;

        // relative position / fractional offsets inside the square
        x -= Math.floor(x);
        y -= Math.floor(y);

        // smooths interpolation / fade transitions
        double u = fade(x);
        double v = fade(y);

        // hash gradients / pick pseudo-random gradients for each corner
        int A = p[X] + Y;
        int B = p[X + 1] + Y;

        // lerp (linear interpolate) – blends the 4 corner gradients based on fractional position
        return lerp(v, lerp(u, grad(p[A], x, y), grad(p[B], x - 1, y)),
                lerp(u, grad(p[A + 1], x, y - 1), grad(p[B + 1], x - 1, y - 1)));
    }

    // quintic fade function from Perlin’s paper / smooth derivatives at edges
    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    // linear interpolation between a and b
    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    // assign direction to each grid point
    private double grad(int hash, double x, double y) {
        int h = hash & 3;
        double u = h < 2 ? x : y;
        double v = h < 2 ? y : x;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}
