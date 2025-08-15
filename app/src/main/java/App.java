import procedural.PerlinNoise;

public class App {

    public static void main(String[] args) {
        // create object with fixed seed, so output will be the same not random
        PerlinNoise noiseGen = new PerlinNoise(42);

        System.out.println("2D Perlin Noise Grid :");

        // 10x10 2D noise grid
        for (int y = 0; y < 10; y++) { // height
            for (int x = 0; x < 10; x++) { // width
                double value = noiseGen.noise(x * 0.1, y * 0.1); // multiplied by 0.1 for
                                                                 // scaling coordinate
                System.out.printf("%.2f\t", value);
            }
            System.out.println();
        }
    }
}
