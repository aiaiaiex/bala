package physics;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;
import component.Transform;
import logger.GlobalLogger;
import object.GameObject;
import window.Window;

public class Physics {
    private Vec2 gravity = new Vec2(0, 0);
    private World world = new World(gravity);

    private float physicsTime = 0.0f;
    private float physicsTimeStep = 1.0f / Window.getWindow().getRefreshRate();
    private int velocityIterations = 8;
    private int positionIterations = 3;

    public Physics() {
        world.setContactListener(new PhysicsContactListener());
    }

    public Vector2f getGravity() {
        return new Vector2f(world.getGravity().x, world.getGravity().y);
    }

    public void add(GameObject go) {
        Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
        if (rb != null && rb.getRawBody() == null) {
            Transform transform = go.transform;

            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float) Math.toRadians(transform.rotation);
            bodyDef.position.set(transform.position.x, transform.position.y);
            bodyDef.angularDamping = rb.getAngularDamping();
            bodyDef.linearDamping = rb.getLinearDamping();
            bodyDef.fixedRotation = rb.isFixedRotation();
            bodyDef.bullet = rb.isContinuousCollision();
            bodyDef.gravityScale = rb.gravityScale;
            bodyDef.angularVelocity = rb.angularVelocity;
            bodyDef.userData = rb.gameObject;

            bodyDef.type = rb.getBodyType();

            Body body = world.createBody(bodyDef);
            body.m_mass = rb.getMass();
            rb.setRawBody(body);

            CircleCollider circleCollider;
            Box2DCollider boxCollider;

            if ((circleCollider = go.getComponent(CircleCollider.class)) != null) {
                addCircleCollider(rb, circleCollider);
            }

            if ((boxCollider = go.getComponent(Box2DCollider.class)) != null) {
                addBox2DCollider(rb, boxCollider);
            }
        }
    }

    public void destroyGameObject(GameObject go) {
        Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
        if (rb != null) {
            if (rb.getRawBody() != null) {
                world.destroyBody(rb.getRawBody());
                rb.setRawBody(null);
            }
        }
    }

    public void update(float dt) {
        physicsTime += dt;
        if (physicsTime > 0.0f) {
            physicsTime -= physicsTimeStep;
            world.step(physicsTimeStep, velocityIterations, positionIterations);
        }
    }

    public void setIsSensor(Rigidbody2D rb) {
        Body body = rb.getRawBody();
        if (body == null)
            return;

        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture.m_isSensor = true;
            fixture = fixture.m_next;
        }
    }

    public void setNotSensor(Rigidbody2D rb) {
        Body body = rb.getRawBody();
        if (body == null)
            return;

        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture.m_isSensor = false;
            fixture = fixture.m_next;
        }
    }

    public void resetCircleCollider(Rigidbody2D rb, CircleCollider circleCollider) {
        Body body = rb.getRawBody();
        if (body == null)
            return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addCircleCollider(rb, circleCollider);
        body.resetMassData();
    }

    public void resetBox2DCollider(Rigidbody2D rb, Box2DCollider boxCollider) {
        Body body = rb.getRawBody();
        if (body == null)
            return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addBox2DCollider(rb, boxCollider);
        body.resetMassData();
    }

    public void addBox2DCollider(Rigidbody2D rb, Box2DCollider boxCollider) {
        Body body = rb.getRawBody();
        if (body == null) {
            GlobalLogger.getGlobalLogger().getLogger().severe("Body must not be null");
            throw new RuntimeException(String.format("Body must not be null"));
        }

        PolygonShape shape = new PolygonShape();
        Vector2f size = new Vector2f(boxCollider.getSize()).mul(0.5f);
        Vector2f offset = boxCollider.getOffset();
        Vector2f origin = new Vector2f(boxCollider.getOrigin());
        shape.setAsBox(size.x, size.y, new Vec2(offset.x, offset.y), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = boxCollider.gameObject;
        fixtureDef.isSensor = rb.isSensor();
        body.createFixture(fixtureDef);
    }

    public void addCircleCollider(Rigidbody2D rb, CircleCollider circleCollider) {
        Body body = rb.getRawBody();
        if (body == null) {
            GlobalLogger.getGlobalLogger().getLogger().severe("Body must not be null");
            throw new RuntimeException(String.format("Body must not be null"));
        }

        CircleShape shape = new CircleShape();
        shape.setRadius(circleCollider.getRadius());
        shape.m_p.set(circleCollider.getOffset().x, circleCollider.getOffset().y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = circleCollider.gameObject;
        fixtureDef.isSensor = rb.isSensor();
        body.createFixture(fixtureDef);
    }

    private int fixtureListSize(Body body) {
        int size = 0;
        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            size++;
            fixture = fixture.m_next;
        }
        return size;
    }

    public boolean isLocked() {
        return world.isLocked();
    }
}
