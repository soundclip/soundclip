// Copyright (C) 2016  Nathan Lowe
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
package soundclip.core;

import soundclip.core.cues.ICue;

/**
 * Various flags to detect what features that different cue implementations support
 */
public final class CueSupportFlags
{

    /** Indicates that the cue implementation supports pausing and resuming */
    public static final int RESUME        = 0b0000001;
    /** Indicates that the cue implementation supports fading in */
    public static final int FADE_IN       = 0b0000010;
    /** Indicates that the cue implementation supports fading out */
    public static final int FADE_OUT      = 0b0000100;
    /** Indicates that the cue implementation supports fading in both directions */
    public static final int FADE          = FADE_IN | FADE_OUT;
    /** Indicates that the cue implementation supports seeking relative to the current playhead position */
    public static final int SEEK_RELATIVE = 0b0001000;
    /** Indicates that the cue implementation supports seeking directly to a certain offset */
    public static final int SEEK_ABSOLUTE = 0b0010000;
    /** Indicates that the cue implementation supports both relative and absolute seeking */
    public static final int SEEK          = SEEK_RELATIVE | SEEK_ABSOLUTE;
    /** Indicates that the cue implementation supports adjusting the pitch */
    public static final int AUDIO_PITCH   = 0b0100000;
    /** Indicates that the cue implementation supports adjusting the L/R pan */
    public static final int AUDIO_PAN     = 0b1000000;

    private CueSupportFlags() { throw new AssertionError("Don't do this"); }

    /**
     * Determines if the specified feature is supported by the implementation of the specified cue
     *
     * @param cue The cue to inspect
     * @param flag The flag to check
     * @return {@code true} iff the cue implementation indicates that the specified feature is supported
     */
    public static boolean Supports(ICue cue, int flag)
    {
        return (cue.getSupportedOperations() & flag) == flag;
    }
}
