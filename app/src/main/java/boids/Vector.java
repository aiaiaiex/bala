package boids;

public class Vector {
    public float x;
    public float y;

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void add(Vector v) {
        x += v.x;
        y += v.y;
    }

    public void subtract(Vector v) {
        x -= v.x;
        y -= v.y;
    }

    public void multiply(float scalar) {
        x *= scalar;
        y *= scalar;
    }

    public void divide(float scalar) {
        x /= scalar;
        y /= scalar;
    }

    public void limit(float max) {
        if (magnitude() > max) {
            normalize();
            multiply(max);
        }
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public void normalize() {
        float mag = magnitude();
        if (mag > 0) {
            divide(mag);
        }
    }

    public float distance(Vector v) {
        return (float) Math.sqrt(Math.pow(x - v.x, 2) + Math.pow(y - v.y, 2));
    }

    public static Vector subtract(Vector a, Vector b) {
        return new Vector(a.x - b.x, a.y - b.y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
