package graphics;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL20C;
import logger.GlobalLogger;

public class Shader {

    private int shaderProgramID;
    private boolean beingUsed = false;

    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token '" + secondPattern + "'");
            }
        } catch (IOException e) {
            e.printStackTrace();
            GlobalLogger.getGlobalLogger().getLogger().severe(
                    () -> String.format("Failed to open shader file: filepath=%1$s", filepath));
            throw new RuntimeException(
                    String.format("Failed to open shader file: filepath=%1$s", filepath));
        }
    }

    public void compile() {
        int vertexID, fragmentID;

        vertexID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexID, vertexSource);
        GL20.glCompileShader(vertexID);

        int success = GL20C.glGetShaderi(vertexID, GL20.GL_COMPILE_STATUS);
        if (success == GL11.GL_FALSE) {
            GlobalLogger.getGlobalLogger().getLogger().severe(() -> String
                    .format("Failed to compile vertex shader: filepath=%1$s", filepath));
            throw new RuntimeException(
                    String.format("Failed to compile vertex shader: filepath=%1$s", filepath));
        }

        fragmentID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentID, fragmentSource);
        GL20.glCompileShader(fragmentID);

        success = GL20C.glGetShaderi(fragmentID, GL20.GL_COMPILE_STATUS);
        if (success == GL11.GL_FALSE) {
            GlobalLogger.getGlobalLogger().getLogger().severe(() -> String
                    .format("Failed to compile fragment shader: filepath=%1$s", filepath));
            throw new RuntimeException(
                    String.format("Failed to compile fragment shader: filepath=%1$s", filepath));
        }

        shaderProgramID = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgramID, vertexID);
        GL20.glAttachShader(shaderProgramID, fragmentID);
        GL20.glLinkProgram(shaderProgramID);

        success = GL20.glGetProgrami(shaderProgramID, GL20.GL_LINK_STATUS);
        if (success == GL11.GL_FALSE) {
            GlobalLogger.getGlobalLogger().getLogger().severe(() -> String
                    .format("Failed to link vertex and fragment shader: filepath=%1$s", filepath));
            throw new RuntimeException(String
                    .format("Failed to link vertex and fragment shader: filepath=%1$s", filepath));
        }
    }

    public void use() {
        if (!beingUsed) {
            GL20.glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }

    public void detach() {
        GL20.glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = GL20.glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        GL20.glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadIntArray(String varName, int[] array) {
        int varLocation = GL20.glGetUniformLocation(shaderProgramID, varName);
        use();
        GL20.glUniform1iv(varLocation, array);
    }
}
