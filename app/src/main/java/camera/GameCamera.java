package camera;

import component.Component;
import game.Player;
import object.GameObject;
import setting.EngineSettings;
import window.Window;

public class GameCamera extends Component {
    private transient GameObject player;
    private transient Camera camera;

    public GameCamera(Camera gameCamera) {
        this.camera = gameCamera;
    }

    @Override
    public void start() {
        player = Window.getScene().getGameObjectWith(Player.class);
        camera.clearColor.set(EngineSettings.BACKGROUND_COLOR);
    }

    @Override
    public void update(float dt) {
        if (player != null) {
            camera.position.x = player.transform.position.x
                    - (camera.getProjectionSize().x / 2) * camera.getZoom();

            camera.position.y = player.transform.position.y
                    - (camera.getProjectionSize().y / 2) * camera.getZoom();

            camera.clearColor.set(EngineSettings.BACKGROUND_COLOR);
        }
    }
}
