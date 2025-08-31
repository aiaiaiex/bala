package object;

public class ShaderMetadata {
    private String vertexShaderFilePath;
    private String fragmentShaderFilePath;

    private ShaderMetadata() {}

    public ShaderMetadata(String vertexShaderFilePath, String fragmentShaderFilePath) {
        this.vertexShaderFilePath = vertexShaderFilePath;
        this.fragmentShaderFilePath = fragmentShaderFilePath;
    }

    public String getVertexShaderFilePath() {
        return vertexShaderFilePath;
    }

    public String getFragmentShaderFilePath() {
        return fragmentShaderFilePath;
    }
}
