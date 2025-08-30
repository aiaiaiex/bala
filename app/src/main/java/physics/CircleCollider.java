package physics;

import org.joml.Vector2f;
import component.Component;
import graphics.DebugDraw;
import window.Window;

public class CircleCollider extends Component {
    private float radius = 1f;
    private transient boolean resetFixtureNextFrame = false;
    protected Vector2f offset = new Vector2f();

    public float getRadius() {
        return radius;
    }

    public Vector2f getOffset() {
        return offset;
    }

    public void setOffset(Vector2f newOffset) {
        offset.set(newOffset);
    }

    public void setRadius(float radius) {
        resetFixtureNextFrame = true;
        this.radius = radius;
    }

    @Override
    public void editorUpdate(float dt) {
        Vector2f center = new Vector2f(gameObject.transform.position).add(offset);
        DebugDraw.addCircle(center, radius);

        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }

    @Override
    public void update(float dt) {
        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }

    public void resetFixture() {
        if (Window.getPhysics().isLocked()) {
            resetFixtureNextFrame = true;
            return;
        }
        resetFixtureNextFrame = false;

        if (gameObject != null) {
            Rigidbody2D rb = gameObject.getComponent(Rigidbody2D.class);
            if (rb != null) {
                Window.getPhysics().resetCircleCollider(rb, this);
            }
        }
    }
}
