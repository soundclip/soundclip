package soundclip.core.cues;

/**
 * A wrapper interface for Audio Cues
 */
public interface IAudioCue extends IFadeableCue, ISeekableCue, IPitchableCue, IPannableCue
{
    /** @return The source audio file to be played */
    String getSource();

    /**
     * Sets the audio source to play
     * @param source the audio file to play
     */
    void setSource(String source);
}
