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
package soundclip.core.tests.interop;

import org.junit.Test;
import soundclip.core.interop.Signal;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Tests for {@link soundclip.core.interop.Signal}
 */
public class SignalTests
{
    @Test
    public void supportsMultipleListeners()
    {
        Signal<String> s = new Signal<>();

        final boolean[] listenersNotified = {false, false};

        long firstId  = s.whenTriggered((str) -> { assertThat(str, is("foobar")); listenersNotified[0] = true; });
        long secondId = s.whenTriggered((str) -> { assertThat(str, is("foobar")); listenersNotified[1] = true; });

        assertThat(firstId, is(equalTo(0L)));
        assertThat(secondId, is(equalTo(1L)));

        s.post("foobar");

        assertArrayEquals(new boolean[]{true, true}, listenersNotified);
    }

    @Test
    public void supportsUnsubscribe()
    {
        Signal<String> s = new Signal<>();

        final boolean[] listenersNotified = {false, false};

        long firstId  = s.whenTriggered((str) -> { assertThat(str, is("foobar")); listenersNotified[0] = true; });
        long secondId = s.whenTriggered((str) -> { assertThat(str, is("foobar")); listenersNotified[1] = true; });

        assertThat(firstId, is(equalTo(0L)));
        assertThat(secondId, is(equalTo(1L)));

        s.unsubscribe(0);

        s.post("foobar");

        assertArrayEquals(new boolean[]{false, true}, listenersNotified);
    }
}
