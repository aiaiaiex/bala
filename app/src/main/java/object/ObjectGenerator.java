package object;

import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;
import component.Sprite;
import component.SpriteRenderer;
import game.Drop;
import game.Enemy;
import game.Player;
import game.Projectile;
import physics.CircleCollider;
import physics.Rigidbody2D;
import setting.EngineSettings;
import window.Window;

public class ObjectGenerator {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = Window.getScene().createGameObject("Sprite_Object_Gen");

        block.transform.zIndex = 0;

        block.transform.scale.x = sizeX;
        block.transform.scale.y = sizeY;

        SpriteRenderer renderer = new SpriteRenderer();

        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }

    public static GameObject generatePlayer(int index) {
        SpriteSheet players = ObjectPool.getSpriteSheet(EngineSettings.PLAYERS.getFilePath());
        GameObject player = generateSpriteObject(players.getSprite(index),
                EngineSettings.GRID_WIDTH, EngineSettings.GRID_HEIGHT);

        player.transform.zIndex = 4;

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(EngineSettings.GRID_WIDTH / 2.0f);
        player.addComponent(circleCollider);

        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.DYNAMIC);
        rb.setContinuousCollision(false);
        rb.setFixedRotation(true);
        player.addComponent(rb);

        player.addComponent(new Player());

        return player;
    }

    public static GameObject generateDrop() {
        return generateDrop(null);
    }

    public static GameObject generateDrop(Vector2f position) {
        SpriteSheet drops = ObjectPool.getSpriteSheet(EngineSettings.DROPS.getFilePath());
        GameObject drop = generateSpriteObject(drops.getSprite(0), EngineSettings.GRID_WIDTH,
                EngineSettings.GRID_HEIGHT);

        if (position != null) {
            drop.transform.position = position;
        }

        drop.transform.zIndex = 1;

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(EngineSettings.GRID_WIDTH / 2);
        drop.addComponent(circleCollider);

        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.STATIC);
        drop.addComponent(rb);

        drop.addComponent(new Drop());

        return drop;
    }

    public static GameObject generateEnemy(int index) {
        SpriteSheet enemies = ObjectPool.getSpriteSheet(EngineSettings.ENEMIES.getFilePath());
        GameObject enemy = generateSpriteObject(enemies.getSprite(index), EngineSettings.GRID_WIDTH,
                EngineSettings.GRID_HEIGHT);

        enemy.transform.zIndex = 2;

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(EngineSettings.GRID_WIDTH / 2);
        enemy.addComponent(circleCollider);

        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.DYNAMIC);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        enemy.addComponent(rb);

        enemy.addComponent(new Enemy());

        return enemy;
    }

    public static GameObject generateProjectile(Vector2f position) {
        SpriteSheet projectiles =
                ObjectPool.getSpriteSheet(EngineSettings.PROJECTILES.getFilePath());
        GameObject projectile = generateSpriteObject(projectiles.getSprite(0),
                EngineSettings.GRID_WIDTH / 1.0f, EngineSettings.GRID_HEIGHT / 1.0f);
        projectile.transform.position = position;

        projectile.transform.zIndex = 3;

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(EngineSettings.GRID_WIDTH / 2.0f);
        projectile.addComponent(circleCollider);

        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.DYNAMIC);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        projectile.addComponent(rb);

        projectile.addComponent(new Projectile());

        return projectile;
    }
}
