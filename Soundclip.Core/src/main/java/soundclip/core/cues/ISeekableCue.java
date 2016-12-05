package soundclip.core.cues;

import java.time.Duration;

/**
 * A cue that can seek the playhead to a different location
 *
 * Ensure that {@link #getSupportedOperations()} has one (or both) of the following flags set:
 * <ul>
 *     <li>{@link soundclip.core.CueSupportFlags#SEEK_ABSOLUTE}</li>
 *     <li>{@link soundclip.core.CueSupportFlags#SEEK_RELATIVE}</li>
 * </ul>
 *
 * Note you can also specify {@link soundclip.core.CueSupportFlags#SEEK} if the implementation
 * supports both relative and absolute seeks.
 */
public interface ISeekableCue extends ICue
{
    /**
     * Seek the playhead by the specified offset
     *
     * @param offset the offset to seek by
     */
    void seekRelative(Duration offset);

    /**
     * Seek the playhead to the specified offset
     *
     * @param duration the offset to seek to
     */
    void seekAbsolute(Duration duration);
}
