package procedural;

public class SimplexNoise {
    private SimplexNoise_octave noise;

    public SimplexNoise() {
        int seed = (int) System.currentTimeMillis();
        noise = new SimplexNoise_octave(seed);
    }

    public String getGreeting() {
        return "2D Simplex Noise Grid:";
    }

    public void printNoiseGrid(int width, int height, double scale) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double nx = x * scale;
                double ny = y * scale;
                double value = noise.noise(nx, ny);
                System.out.printf("%6.2f ", value);
            }
            System.out.println();
        }
    }
}
