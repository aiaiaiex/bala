package game;

import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import component.Component;
import object.GameObject;
import object.ObjectPool;
import setting.EngineSettings;
import sound.Sound;

public class Drop extends Component {
    private Vector2f finalHeight;
    private float jumpHeight = 0.5f;
    private float jumpSpeed = 2.0f;
    private transient boolean jumping = false;

    private transient Sound pickupSound;

    @Override
    public void start() {
        finalHeight = new Vector2f(gameObject.transform.position.y).add(0, jumpHeight);
        pickupSound = ObjectPool.getSound(EngineSettings.DEFAULT_PICKUP_SOUND);
    }

    @Override
    public void update(float deltaTime) {
        if (jumping) {
            if (gameObject.transform.position.y < finalHeight.y) {
                gameObject.transform.position.y += deltaTime * jumpSpeed;
            } else {
                gameObject.destroy();
            }
        }
    }

    @Override
    public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal) {
        if (obj.getComponent(Player.class) != null) {
            cleanup();
            contact.setEnabled(false);
        } else if (obj.getComponent(Enemy.class) != null) {
            contact.setEnabled(false);
        }
    }

    public void cleanup() {
        if (pickupSound != null) {
            pickupSound.play();
        }
        jumping = true;
    }
}
