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
package soundclip.core.tests.cues;

import org.junit.Test;
import soundclip.core.CueNumber;
import soundclip.core.cues.ICue;
import soundclip.core.cues.impl.NoteCue;

import java.time.Duration;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link soundclip.core.cues.impl.NoteCue}
 */
public class NoteCueTests
{
    @Test
    public void remembersSetCueNumber()
    {
        ICue cue = new NoteCue(new CueNumber(1));
        assertThat(cue.getNumber(), is(equalTo(new CueNumber(1))));

        cue.setNumber(new CueNumber(2));
        assertThat(cue.getNumber(), is(equalTo(new CueNumber(2))));
    }

    @Test
    public void remembersSetName()
    {
        ICue cue = new NoteCue(new CueNumber(1));

        cue.setName("My Cue");

        assertThat(cue.getName(), is(equalTo("My Cue")));
    }

    @Test
    public void remembersSetNote()
    {
        ICue cue = new NoteCue(new CueNumber(1));

        cue.setNotes("The quick brown fox jumps over the lazy dog");

        assertThat(cue.getNotes(), is(equalTo("The quick brown fox jumps over the lazy dog")));
    }

    @Test
    public void durationIsZero()
    {
        ICue cue = new NoteCue(new CueNumber(1));

        assertThat(cue.getDuration(), is(equalTo(Duration.ZERO)));
    }

    @Test
    public void noSupportFlagsSet()
    {
        ICue cue = new NoteCue(new CueNumber(1));

        assertThat(cue.getSupportedOperations(), is(equalTo(0)));
    }

    /**
     * Playback operations for the Note cue are all no-ops. This method simply calls
     * them to let the code coverage utility not mark them as uncovered.
     */
    @Test
    public void playbackOperationsAreNoOps()
    {
        ICue cue = new NoteCue(new CueNumber(1));

        cue.go();
        cue.pause();
        cue.resume();
        cue.stop();
    }
}
