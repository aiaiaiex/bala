package boids;

public class Bird {
    private Vector position;
    private Vector velocity;
    private final int id;

    public Bird(float x, float y, int id) {
        this.position = new Vector(x, y);
        this.velocity =
                new Vector((float) (Math.random() * 2 - 1), (float) (Math.random() * 2 - 1));
        this.id = id;
    }

    public void update(Vector[] flockPositions, Vector[] flockVelocities, int index) {
        Vector separation = separate(flockPositions);
        Vector alignment = align(flockVelocities);
        Vector cohesion = cohere(flockPositions);

        separation.multiply(1.5f);
        alignment.multiply(1.0f);
        cohesion.multiply(1.0f);

        velocity.add(separation);
        velocity.add(alignment);
        velocity.add(cohesion);
        velocity.limit(4.0f);
        position.add(velocity);
    }

    private Vector separate(Vector[] positions) {
        return new Vector(0, 0);
    }

    private Vector align(Vector[] velocities) {
        return new Vector(0, 0);
    }

    private Vector cohere(Vector[] positions) {
        return new Vector(0, 0);
    }

    public String getOutput() {
        return String.format("Bird %d | Position: (%.2f, %.2f) | Velocity: (%.2f, %.2f)", id,
                position.x, position.y, velocity.x, velocity.y);
    }

    // Getter methods for position and velocity
    public Vector getPosition() {
        return position;
    }

    public Vector getVelocity() {
        return velocity;
    }
}
