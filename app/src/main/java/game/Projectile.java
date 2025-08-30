package game;

import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import component.Component;
import object.GameObject;
import object.ObjectPool;
import physics.Rigidbody2D;
import setting.EngineSettings;
import sound.Sound;
import window.Window;

public class Projectile extends Component {
    private transient boolean isRight = true;
    private transient Rigidbody2D rb;
    private transient float minimumSpeed = 2.0f;
    private transient float maximumSpeed = 4.0f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();
    private transient float activeTime = 4.0f;
    private transient float remainingTime = activeTime;
    private transient float width = EngineSettings.GRID_WIDTH / 1.0f;

    private transient Sound releaseSound;
    private transient Sound impactSound;

    @Override
    public void start() {
        rb = gameObject.getComponent(Rigidbody2D.class);
        acceleration.y = Window.getPhysics().getGravity().y * 1;

        releaseSound = ObjectPool.getSound(EngineSettings.DEFAULT_RELEASE_SOUND);
        impactSound = ObjectPool.getSound(EngineSettings.DEFAULT_IMPACT_SOUND);

        if (releaseSound != null) {
            releaseSound.play();
        }
    }

    @Override
    public void update(float deltaTime) {
        remainingTime -= deltaTime;
        if (remainingTime <= 0) {
            cleanup();
            return;
        }

        if (isRight) {
            velocity.x = minimumSpeed;
        } else {
            gameObject.transform.scale.x = -width;
            velocity.x = -minimumSpeed;
        }

        velocity.y += acceleration.y * deltaTime;
        velocity.y = Math.max(Math.min(velocity.y, maximumSpeed), -maximumSpeed);

        rb.setVelocity(velocity);
    }

    @Override
    public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal) {
        if (obj.getComponent(Enemy.class) == null) {
            contact.setEnabled(false);
        }
    }

    public void cleanup() {
        if (impactSound != null) {
            impactSound.play();
        }
        gameObject.destroy();
    }

    public void setIsRight(boolean isRight) {
        this.isRight = isRight;
    }
}
