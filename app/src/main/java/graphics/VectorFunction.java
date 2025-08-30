package graphics;

import org.joml.Vector2f;

public class VectorFunction {

    private VectorFunction() {}

    public static void rotate(Vector2f vector, float degree, Vector2f origin) {
        float x = vector.x - origin.x;
        float y = vector.y - origin.y;

        float cos = (float) Math.cos(Math.toRadians(degree));
        float sin = (float) Math.sin(Math.toRadians(degree));

        float rotatedX = (x * cos) - (y * sin);
        float rotatedY = (x * sin) + (y * cos);

        vector.x = rotatedX + origin.x;
        vector.y = rotatedY + origin.y;
    }
}
