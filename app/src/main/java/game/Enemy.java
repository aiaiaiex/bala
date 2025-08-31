package game;

import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import camera.Camera;
import component.Component;
import object.GameObject;
import object.GameObjectGenerator;
import object.ObjectPool;
import physics.Rigidbody2D;
import setting.EngineSettings;
import sound.Sound;
import window.Window;

public class Enemy extends Component {

    private transient Rigidbody2D rb;

    private transient float minimumSpeed = 0.5f;
    private transient float maximumSpeed = 0.5f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();

    private transient float width = EngineSettings.GRID_WIDTH;
    private transient Camera camera;
    private transient Vector2f playerPosition;
    private transient Sound deathSound;

    @Override
    public void start() {
        rb = gameObject.getComponent(Rigidbody2D.class);
        camera = Window.getScene().getCamera();
        acceleration = Window.getPhysics().getGravity();
        deathSound = ObjectPool.getSound(EngineSettings.DEFAULT_ENEMY_DEATH_SOUND);
    }

    @Override
    public void update(float deltaTime) {
        if ((gameObject.transform.position.x < camera.position.x * camera.getZoom())
                || (gameObject.transform.position.x > camera.position.x
                        + camera.getProjectionSize().x * camera.getZoom())
                || ((gameObject.transform.position.y < camera.position.y * camera.getZoom())
                        || (gameObject.transform.position.y > camera.position.y
                                + camera.getProjectionSize().y * camera.getZoom()))) {
            return;
        }

        playerPosition = new Vector2f(
                camera.position.x + ((camera.getProjectionSize().x / 2) * camera.getZoom()),
                camera.position.y + ((camera.getProjectionSize().y / 2)) * camera.getZoom());

        Vector2f followVector = playerPosition.sub(gameObject.transform.position);

        velocity.x = followVector.x > 0
                ? Math.min(minimumSpeed + acceleration.x * deltaTime, maximumSpeed)
                : Math.min(-minimumSpeed - acceleration.x * deltaTime, -maximumSpeed);
        velocity.y = followVector.y > 0
                ? Math.min(minimumSpeed + acceleration.y * deltaTime, maximumSpeed)
                : Math.min(-minimumSpeed - acceleration.y * deltaTime, -maximumSpeed);

        rb.setVelocity(velocity);

        if (velocity.x > 0) {
            gameObject.transform.scale.x = width;
        } else {
            gameObject.transform.scale.x = -width;
        }
    }

    @Override
    public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal) {
        if (obj.getComponent(Player.class) != null) {
            cleanup();
        }

        if (obj.getComponent(Projectile.class) != null) {
            obj.getComponent(Projectile.class).cleanup();
            cleanup();
        }

    }

    public void cleanup() {
        if (deathSound != null) {
            deathSound.play();
        }
        Window.getScene().addGameObjectToScene(
                GameObjectGenerator.generateDrop(gameObject.transform.position));
        gameObject.destroy();
    }

}
