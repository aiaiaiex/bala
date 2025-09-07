package physics;

import org.joml.Vector2f;
import component.Component;
import graphics.DebugDraw;

public class Box2DCollider extends Component {
    private Vector2f size = new Vector2f(1);
    private Vector2f origin = new Vector2f();
    private Vector2f offset = new Vector2f();

    public Vector2f getOffset() {
        return offset;
    }

    public void setOffset(Vector2f newOffset) {
        offset.set(newOffset);
    }

    public Vector2f getSize() {
        return size;
    }

    public void setSize(Vector2f size) {
        this.size = size;
    }

    public Vector2f getOrigin() {
        return origin;
    }

    @Override
    public void editorUpdate(float dt) {
        Vector2f center = new Vector2f(gameObject.transform.position).add(offset);
        DebugDraw.addBox2D(center, size, gameObject.transform.rotation);
    }
}
