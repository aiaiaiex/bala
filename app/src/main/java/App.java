import procedural.SimplexNoise;

public class App {
    public static void main(String[] args) {
        SimplexNoise noise = new SimplexNoise();
        System.out.println(noise.getGreeting());
        noise.printNoiseGrid(15, 15, 0.1);
    }
}
