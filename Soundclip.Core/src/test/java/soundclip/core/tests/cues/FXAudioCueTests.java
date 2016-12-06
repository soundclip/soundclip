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
import soundclip.core.CueSupportFlags;
import soundclip.core.cues.ICue;
import soundclip.core.cues.IFadeableCue;
import soundclip.core.cues.IPannableCue;
import soundclip.core.cues.IPitchableCue;
import soundclip.core.cues.impl.FXAudioCue;

import java.time.Duration;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link soundclip.core.cues.impl.FXAudioCue}
 */
public class FXAudioCueTests
{
    @Test
    public void remembersSetCueNumber()
    {
        ICue cue = new FXAudioCue(new CueNumber(1));
        assertThat(cue.getNumber(), is(equalTo(new CueNumber(1))));

        cue.setNumber(new CueNumber(2));
        assertThat(cue.getNumber(), is(equalTo(new CueNumber(2))));
    }

    @Test
    public void remembersSetName()
    {
        ICue cue = new FXAudioCue(new CueNumber(1));

        cue.setName("My Cue");

        assertThat(cue.getName(), is(equalTo("My Cue")));
    }

    @Test
    public void remembersSetNote()
    {
        ICue cue = new FXAudioCue(new CueNumber(1));

        cue.setNotes("The quick brown fox jumps over the lazy dog");

        assertThat(cue.getNotes(), is(equalTo("The quick brown fox jumps over the lazy dog")));
    }

    @Test
    public void setsCorrectSupportFlags()
    {
        ICue cue = new FXAudioCue(new CueNumber(1));

        assertThat(CueSupportFlags.Supports(cue, CueSupportFlags.FADE), is(true));
        assertThat(CueSupportFlags.Supports(cue, CueSupportFlags.RESUME), is(true));
        assertThat(CueSupportFlags.Supports(cue, CueSupportFlags.SEEK), is(true));
        assertThat(CueSupportFlags.Supports(cue, CueSupportFlags.AUDIO_PITCH), is(true));
        assertThat(CueSupportFlags.Supports(cue, CueSupportFlags.AUDIO_PAN), is(true));
    }

    @Test
    public void remembersFadeInDuration()
    {
        IFadeableCue cue = new FXAudioCue(new CueNumber(1));

        cue.setFadeInDuration(Duration.ofSeconds(1));

        assertThat(cue.getFadeInDuration(), is(equalTo(Duration.ofSeconds(1))));
    }

    @Test
    public void remembersFadeOutDuration()
    {
        IFadeableCue cue = new FXAudioCue(new CueNumber(1));

        cue.setFadeOutDuration(Duration.ofSeconds(1));

        assertThat(cue.getFadeOutDuration(), is(equalTo(Duration.ofSeconds(1))));
    }

    @Test
    public void remembersPitch()
    {
        IPitchableCue cue = new FXAudioCue(new CueNumber(1));

        cue.setPitch(1.234);

        assertThat(cue.getPitch(), is(equalTo(1.234)));
    }

    @Test
    public void remembersPan()
    {
        IPannableCue cue = new FXAudioCue(new CueNumber(1));

        cue.setPan(0.234);

        assertThat(cue.getPan(), is(equalTo(0.234)));
    }

    @Test
    public void clampsPanLow()
    {
        IPannableCue cue = new FXAudioCue(new CueNumber(1));

        cue.setPan(-1.5);

        assertThat(cue.getPan(), is(equalTo(-1.0)));
    }

    @Test
    public void clampsPanHigh()
    {
        IPannableCue cue = new FXAudioCue(new CueNumber(1));

        cue.setPan(1.5);

        assertThat(cue.getPan(), is(equalTo(1.0)));
    }

    @Test
    public void durationIsZeroIfNoFileSet()
    {
        ICue cue = new FXAudioCue(new CueNumber(1));

        assertThat(cue.getDuration(), is(equalTo(Duration.ZERO)));
    }
}
