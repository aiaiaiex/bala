package game;

import java.util.List;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import camera.Camera;
import component.Component;
import input.Mouse;
import object.GameObject;
import object.GameObjectGenerator;
import object.ObjectPool;
import physics.Rigidbody2D;
import scene.Scene;
import setting.EngineSettings;
import sound.Sound;
import window.Window;

public class Enemy extends Component {

    private transient Rigidbody2D rb;

    private transient Vector2f maxVelocity = new Vector2f(2.0f, 2.0f);
    private transient Vector2f velocity = new Vector2f(0.0f, 0.0f);
    private transient Vector2f acceleration = new Vector2f(0.0f, 0.0f);
    private transient Vector2f maxForce = new Vector2f(maxVelocity.x / 2, maxVelocity.y / 2);

    private transient float width = EngineSettings.GRID_WIDTH;
    private transient Camera camera;
    private transient Vector2f playerPosition;
    private transient Sound deathSound;

    private transient Mouse mouse;
    private transient Scene scene;

    private transient List<GameObject> enemies;
    // For flocking demo with collidable terrain.
    // private transient float contactCooldownDuration = 1.0f;
    // private transient float contactCooldown = 0.0f;

    public Vector2f getVelocity() {
        return velocity;
    }

    @Override
    public void start() {
        rb = gameObject.getComponent(Rigidbody2D.class);
        scene = Window.getScene();
        camera = scene.getCamera();
        acceleration = Window.getPhysics().getGravity();
        deathSound = ObjectPool.getSound(EngineSettings.DEFAULT_ENEMY_DEATH_SOUND);

        if (EngineSettings.FOLLOW_MOUSE) {
            mouse = Mouse.getMouse();
        }

        if (EngineSettings.USE_FLOCKING) {
            enemies = scene.getEnemies();

            // For flocking demo without following player.
            // Random rng = new Random();
            // velocity = new Vector2f(
            // rng.nextFloat() * (rng.nextBoolean() ? maxVelocity.x : -maxVelocity.x),
            // rng.nextFloat() * (rng.nextBoolean() ? maxVelocity.y : -maxVelocity.y));
        }
    }

    @Override
    public void update(float deltaTime) {

        // For flocking demo with collidable terrain.
        // contactCooldown -= deltaTime;

        if ((gameObject.transform.position.x < camera.position.x * camera.getZoom())
                || (gameObject.transform.position.x > camera.position.x
                        + camera.getProjectionSize().x * camera.getZoom())
                || ((gameObject.transform.position.y < camera.position.y * camera.getZoom())
                        || (gameObject.transform.position.y > camera.position.y
                                + camera.getProjectionSize().y * camera.getZoom()))) {
            return;
        }

        if (!EngineSettings.FOLLOW_MOUSE) {
            playerPosition = new Vector2f(
                    camera.position.x + ((camera.getProjectionSize().x / 2) * camera.getZoom()),
                    camera.position.y + ((camera.getProjectionSize().y / 2)) * camera.getZoom());
        } else {
            playerPosition = mouse.getWorld();
        }

        acceleration.zero();

        if (EngineSettings.USE_FLOCKING) {

            Vector2f separateSum = new Vector2f();
            Vector2f alignSum = new Vector2f();
            Vector2f coherenceSum = new Vector2f();

            int neighborCount = 0;
            int separateCount = 0;

            for (GameObject gameObject : enemies) {
                Enemy enemy = gameObject.getComponent(Enemy.class);
                Vector2f enemyVelocity = enemy.getVelocity();

                float distance =
                        gameObject.transform.position.distance(this.gameObject.transform.position);

                if ((distance > 0) && (distance < EngineSettings.FLOCKING_NEIGHBOR_DISTANCE)) {
                    alignSum.add(enemyVelocity);
                    coherenceSum.add(gameObject.transform.position);
                    neighborCount += 1;
                }

                if ((distance > 0) && (distance < EngineSettings.FLOCKING_SEPARATE_DISTANCE)) {
                    Vector2f positionDifference = new Vector2f(this.gameObject.transform.position);
                    positionDifference.sub(gameObject.transform.position);
                    positionDifference.normalize();
                    positionDifference.div(distance);
                    separateSum.add(positionDifference);
                    separateCount += 1;
                }
            }

            if (separateCount > 0) {
                moveToVector(separateSum, EngineSettings.FLOCKING_SEPARATE_MULTIPLIER);
            }
            if (neighborCount > 0) {
                moveToVector(alignSum, EngineSettings.FLOCKING_ALIGN_MULTIPLIER);

                coherenceSum.div(neighborCount);
                coherenceSum.sub(gameObject.transform.position);
                moveToVector(coherenceSum, EngineSettings.FLOCKING_COHERENCE_MULTIPLIER);
            }
        }


        float avoid = 1.0f;
        if (gameObject.transform.position.distance(playerPosition) < EngineSettings.GRID_WIDTH
                * 4) {
            avoid = -1.0f;
        }

        playerPosition.sub(gameObject.transform.position);
        moveToVector(playerPosition, 1.0f * avoid);

        acceleration.mul(deltaTime);
        velocity.add(acceleration);

        float length = velocity.length();
        velocity.normalize();
        velocity.mul(Math.min(length, maxVelocity.x));

        rb.setVelocity(velocity);


        if (velocity.x > 0) {
            gameObject.transform.scale.x = width;
        } else {
            gameObject.transform.scale.x = -width;
        }
    }

    private void moveToVector(Vector2f vector, float multiplier) {
        vector.normalize();
        vector.mul(maxVelocity);
        vector.sub(velocity);

        vector.mul(multiplier);

        float length = vector.length();
        vector.normalize();
        vector.mul(Math.min(length, maxForce.x));

        // vector.mul(multiplier);

        acceleration.add(vector);
    }

    @Override
    public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal) {
        if (obj.getComponent(Player.class) != null) {
            cleanup();
        } else if (obj.getComponent(Projectile.class) != null) {
            obj.getComponent(Projectile.class).cleanup();
            cleanup();
        }
        // } else if (EngineSettings.USE_FLOCKING && obj.transform.zIndex == 2) {
        // contact.setEnabled(false);
        // }

        // For flocking demo with collidable terrain.
        // } else if (obj.transform.zIndex == 1 && obj.getComponent(Drop.class) == null) {
        // if (contactCooldown <= 0.0f) {
        // if (contactNormal.x > 0 || contactNormal.x < 0) {
        // velocity.x *= -1;
        // acceleration.x = 0;
        // }
        // if (contactNormal.y > 0 || contactNormal.y < 0) {
        // velocity.y *= -1;
        // acceleration.y = 0;
        // }
        // contactCooldown = contactCooldownDuration;
        // }
        // }

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
