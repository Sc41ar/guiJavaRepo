//интерфейс с событиями для интерфейса
public interface  ControlsCallback{
    void worldGenerated(int worldHeight, int worldWidth);
    void viewModeChanged(int viewMode);
    boolean startStop();
    void stepRenderChanged(int delta);
}
