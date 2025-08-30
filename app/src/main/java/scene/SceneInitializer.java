package scene;

public abstract class SceneInitializer {
    public abstract void initialize(Scene scene);

    public abstract void loadResources(Scene scene);

    public abstract void terminate(Scene scene);

    public abstract void imGui();
}
