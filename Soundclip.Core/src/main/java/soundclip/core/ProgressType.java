package soundclip.core;

/**
 * What to do when moving the cue list playhead
 */
public enum ProgressType
{
    /** Just focus the next cue */
    FOCUS,
    /** Focus and trigger the next cue, then follow that cue's ProgressType for the following cue */
    TRIGGER,
    /** Don't focus the next cue automatically */
    HOLD;

    public static ProgressType fromOrdinal(int selectedIndex)
    {
        switch(selectedIndex)
        {
            case 0: return FOCUS;
            case 1: return TRIGGER;
            case 2: return HOLD;
            default: throw new IndexOutOfBoundsException();
        }
    }
}
