package soundclip.core.cues;

import soundclip.core.CueNumber;
import soundclip.core.CueSupportFlags;

import java.time.Duration;
import java.util.List;

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

    /** @return the notes associated with the cue */
    String getNotes();
    /** Sets the notes for the cue */
    void setNotes(String notes);

    /** @return the duration of the cue */
    Duration getDuration();

    /** @return the features supported by the cue. One or more of {@link CueSupportFlags} */
    int getSupportedOperations();

    /** @return A list of validation problems with the cue. Return an empty list if there are none */
    List<String> getValidationErrors();

    /** Triggers the main action of the cue */
    void go();

    /** Pauses the cue. Stops the cue instead if the implementation does not support resuming */
    void pause();

    /** Resume the cue from where it was paused */
    void resume();

    /** Stops the cue and resets the playback state */
    void stop();

}
