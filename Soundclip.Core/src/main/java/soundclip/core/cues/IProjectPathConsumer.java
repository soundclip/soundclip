package soundclip.core.cues;

/**
 * An object that needs to know about the project path
 */
public interface IProjectPathConsumer
{
    /**
     * Notify the cue that the project is located at the specified path
     * @param path the folder containing the project file
     */
    void consumeProjectPath(String path);
}
