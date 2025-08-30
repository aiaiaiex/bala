package component;

class Frame {
    public Sprite sprite;
    public float frameTime;

    private Frame() {}

    public Frame(Sprite sprite, float frameTime) {
        this.sprite = sprite;
        this.frameTime = frameTime;
    }
}
