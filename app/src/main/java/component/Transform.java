package component;

import org.joml.Vector2f;
import gui.Properties;

public class Transform extends Component {

    public Vector2f position;
    public Vector2f scale;
    public float rotation = 0.0f;
    public int zIndex;

    public Transform() {
        init(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position) {
        init(position, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale) {
        init(position, scale);
    }

    public void init(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
        zIndex = 0;
    }

    public Transform copy() {
        return new Transform(new Vector2f(position), new Vector2f(scale));
    }

    @Override
    public void imgui() {
        gameObject.name = Properties.inputText("Name: ", gameObject.name);
        Properties.drawVec2Control("Position", position);
        Properties.drawVec2Control("Scale", scale, 32.0f);
        rotation = Properties.dragFloat("Rotation", rotation);
        zIndex = Properties.dragInt("Z-Index", zIndex);
    }

    public void copy(Transform to) {
        to.position.set(position);
        to.scale.set(scale);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Transform))
            return false;

        Transform t = (Transform) o;
        return t.position.equals(position) && t.scale.equals(scale) && t.rotation == rotation
                && t.zIndex == zIndex;
    }
}
