package physics;

import object.GameObject;
import setting.EngineSettings;

public final class ColliderAdder {
    private ColliderAdder() {}

    public static void addCollider(GameObject gameObject) {
        if (EngineSettings.USE_CIRCLE_COLLIDER) {
            CircleCollider circleCollider = new CircleCollider();
            circleCollider.setRadius(gameObject.transform.scale.x / 2);

            gameObject.addComponent(circleCollider);
        } else {
            Box2DCollider boxCollider = new Box2DCollider();
            boxCollider.setSize(gameObject.transform.scale);

            gameObject.addComponent(boxCollider);
        }
    }
}
