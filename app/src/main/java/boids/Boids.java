package boids;

public class Boids {
    private Bird[] flock;
    private int frameCount = 1;

    public Boids(int numBirds) {
        flock = new Bird[numBirds];
        for (int i = 0; i < numBirds; i++) {
            flock[i] =
                    new Bird((float) (Math.random() * 800), (float) (Math.random() * 600), i + 1);
        }
    }

    public void update() {
        System.out.println("\nFrame " + frameCount + ":");
        System.out.println("------------------------");

        // Get current positions and velocities for flocking calculations
        Vector[] positions = new Vector[flock.length];
        Vector[] velocities = new Vector[flock.length];
        for (int i = 0; i < flock.length; i++) {
            positions[i] = flock[i].getPosition();
            velocities[i] = flock[i].getVelocity();
        }

        // Update each bird with flock behavior
        for (int i = 0; i < flock.length; i++) {
            flock[i].update(positions, velocities, i);
            System.out.println(flock[i].getOutput());
        }

        frameCount++;
    }
}
