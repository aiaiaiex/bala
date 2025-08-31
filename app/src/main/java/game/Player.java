package game;

import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import component.Component;
import input.Keyboard;
import object.GameObject;
import object.GameObjectGenerator;
import object.ObjectPool;
import physics.Rigidbody2D;
import scene.scenes.GameScene;
import setting.EngineSettings;
import sound.Sound;
import window.Window;

public class Player extends Component {

    private transient Rigidbody2D rb;


    private transient float speed = 1.5f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f maximumVelocity = new Vector2f(2.0f, 2.0f);
    private transient Vector2f acceleration = new Vector2f();

    private transient boolean isRight = true;
    private transient float width = EngineSettings.GRID_WIDTH;

    private transient int lives = 1;

    private transient float reloadTime = 0.5f;
    private transient float timeToReload = 1.0f;

    private transient Keyboard keyboard;
    private transient Sound deathSound;

    public Player() {
        keyboard = Keyboard.getKeyboard();
    }

    @Override
    public void start() {
        rb = gameObject.getComponent(Rigidbody2D.class);
        deathSound = ObjectPool.getSound(EngineSettings.DEFAULT_PLAYER_DEATH_SOUND);
    }

    @Override
    public void update(float dt) {
        if (reloadTime <= 0) {
            Vector2f position = new Vector2f(gameObject.transform.position)
                    .add(isRight ? new Vector2f(width, 0) : new Vector2f(-width, 0));
            GameObject projectile = GameObjectGenerator.generateProjectile(position);
            projectile.getComponent(Projectile.class).setIsRight(isRight);
            Window.getScene().addGameObjectToScene(projectile);

            reloadTime = timeToReload;
        } else {
            reloadTime -= dt;
        }

        if (keyboard.isKeyPressed(GLFW.GLFW_KEY_RIGHT) || keyboard.isKeyPressed(GLFW.GLFW_KEY_D)) {
            acceleration.x = speed;
            isRight = true;
        } else if (keyboard.isKeyPressed(GLFW.GLFW_KEY_LEFT)
                || keyboard.isKeyPressed(GLFW.GLFW_KEY_A)) {
            acceleration.x = -speed;
            isRight = false;
        } else {
            acceleration.x = 0;
            velocity.x = 0;
        }

        if (keyboard.isKeyPressed(GLFW.GLFW_KEY_UP) || keyboard.isKeyPressed(GLFW.GLFW_KEY_W)) {
            acceleration.y = speed;
        } else if (keyboard.isKeyPressed(GLFW.GLFW_KEY_DOWN)
                || keyboard.isKeyPressed(GLFW.GLFW_KEY_S)) {
            acceleration.y = -speed;
        } else {
            velocity.y = 0;
            acceleration.y = 0;
        }

        velocity.x += acceleration.x * dt;
        velocity.y += acceleration.y * dt;
        velocity.x = Math.max(Math.min(velocity.x, maximumVelocity.x), -maximumVelocity.x);
        velocity.y = Math.max(Math.min(velocity.y, maximumVelocity.y), -maximumVelocity.y);

        rb.setVelocity(velocity);

        if (isRight) {
            gameObject.transform.scale.x = width;
        } else {
            gameObject.transform.scale.x = -width;
        }
    }

    @Override
    public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal) {
        if (obj.getComponent(Enemy.class) != null) {
            lives -= 1;
            if (lives <= 0) {
                cleanup();
            }
        }

    }

    public void cleanup() {
        if (deathSound != null) {
            deathSound.play();
        }
        Window.changeScene(new GameScene());
        gameObject.destroy();
    }
}
