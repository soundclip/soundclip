package soundclip.core;

import java.time.Duration;

/**
 * The basic cue interface
 */
public interface ICue
{
    /** @return the number of the cue */
    CueNumber getNumber();
    /** Set the number of the cue */
    void setNumber(CueNumber number);

    /** @return the name of the cue */
    String getName();
    /** Set the name of the cue */
    void setName(String name);

    /** @return the duration of the cue */
    Duration getDuration();

    /** @return the features supported by the cue. One or more of {@link CueSupportFlags} */
    int getSupportedOperations();
}
