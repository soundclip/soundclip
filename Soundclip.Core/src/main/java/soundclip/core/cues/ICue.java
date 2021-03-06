package soundclip.core.cues;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.util.Duration;
import soundclip.core.CueNumber;
import soundclip.core.CueSupportFlags;
import soundclip.core.ProgressType;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * The basic cue interface
 */
public interface ICue extends  IProgressProvider
{
    /** @return the unique ID of this cue in the project */
    UUID getGUID();

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

    Duration getPreWaitDelay();
    void setPreWaitDelay(Duration delay);

    Duration getPostWaitDelay();
    void setPostWaitDelay(Duration delay);

    /** Get the {@link ProgressType} for the cue */
    ProgressType getProgressType();
    /** Set the {@link ProgressType} for the cue */
    void setProgressType(ProgressType type);

    /** @return the features supported by the cue. One or more of {@link CueSupportFlags} */
    int getSupportedOperations();

    /** @return A list of validation problems with the cue. Return an empty list if there are none */
    List<String> getValidationErrors();

    /** @return True iff the cue is paused */
    boolean isPaused();

    /** Triggers the main action of the cue */
    void go();

    /** Pauses the cue. Stops the cue instead if the implementation does not support resuming */
    void pause();

    /** Resume the cue from where it was paused */
    void resume();

    /** Stops the cue and resets the playback state */
    void stop();

    /** Initialize the cue from the specified json node */
    void load(JsonNode cue);

    /** Serialize the cue to the specified Json Generator */
    void serialize(JsonGenerator writer) throws IOException;
}
