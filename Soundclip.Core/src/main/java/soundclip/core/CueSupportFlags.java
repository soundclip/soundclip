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

/**
 * Various flags to detect what features that different cue implementations support
 */
public class CueSupportFlags
{

    /** Indicates that the cue implementation supports pausing and resuming */
    public static final int RESUME   = 0b001;
    /** Indicates that the cue implementation supports fading in */
    public static final int FADE_IN  = 0b010;
    /** Indicates that the cue implementation supports fading out */
    public static final int FADE_OUT = 0b100;
    /** Indicates that the cue implementation supports fading in both directions */
    public static final int FADE   = FADE_IN | FADE_OUT;

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
