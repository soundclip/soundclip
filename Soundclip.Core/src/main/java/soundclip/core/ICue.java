package soundclip.core;

import java.time.Duration;

/**
 * The basic cue interface
 */
public interface ICue
{
    CueNumber getNumber();
    String getName();
    Duration getDuration();

    int getSupportedOperations();
}
