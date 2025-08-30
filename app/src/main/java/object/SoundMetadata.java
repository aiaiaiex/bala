package object;

public class SoundMetadata {
    private String filePath;
    private boolean isLooping;

    private SoundMetadata() {}

    public SoundMetadata(String filePath) {
        this(filePath, false);
    }

    public SoundMetadata(String filePath, boolean isLooping) {
        this.filePath = filePath;
        this.isLooping = isLooping;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isLooping() {
        return isLooping;
    }
}
