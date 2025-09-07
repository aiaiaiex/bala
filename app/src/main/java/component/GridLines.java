package component;

import org.joml.Vector2f;
import org.joml.Vector4f;
import graphics.DebugDraw;
import setting.EngineSettings;
import window.Window;

public class GridLines extends Component {

        @Override
        public void editorUpdate(float dt) {
                Vector4f gridStarter = Window.getScene().getCamera().getGridStarter();

                float firstXPosition = gridStarter.x - (EngineSettings.GRID_WIDTH / 2);
                float firstYPosition = gridStarter.y - (EngineSettings.GRID_HEIGHT / 2);

                int columns = (int) gridStarter.z - 1;
                int rows = (int) gridStarter.z - 1;

                float rowWidth = (columns + 1) * EngineSettings.GRID_WIDTH;
                float columnHeight = (rows + 1) * EngineSettings.GRID_HEIGHT;

                for (int lineNumber = 1; lineNumber <= Math.max(columns, rows); lineNumber++) {
                        float x = firstXPosition + (EngineSettings.GRID_WIDTH * lineNumber);
                        float y = firstYPosition + (EngineSettings.GRID_HEIGHT * lineNumber);

                        if (lineNumber <= columns) {
                                DebugDraw.addLine2D(new Vector2f(x, firstYPosition),
                                                new Vector2f(x, firstYPosition + columnHeight),
                                                EngineSettings.GRID_COLOR);
                        }

                        if (lineNumber <= rows) {
                                DebugDraw.addLine2D(new Vector2f(firstXPosition, y),
                                                new Vector2f(firstXPosition + rowWidth, y),
                                                EngineSettings.GRID_COLOR);
                        }
                }
        }
}
