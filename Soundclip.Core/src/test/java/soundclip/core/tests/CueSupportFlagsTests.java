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
package soundclip.core.tests;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import soundclip.core.CueSupportFlags;
import soundclip.core.ICue;

/**
 * Tests for {@link soundclip.core.CueSupportFlags}
 */
public class CueSupportFlagsTests
{
    @Test
    public void detectsSingleFlags()
    {
        ICue a = mock(ICue.class);
        when(a.getSupportedOperations()).thenReturn(CueSupportFlags.FADE);

        assertThat(CueSupportFlags.Supports(a, CueSupportFlags.FADE), is(true));

        ICue b = mock(ICue.class);
        when(b.getSupportedOperations()).thenReturn(CueSupportFlags.RESUME);

        assertThat(CueSupportFlags.Supports(b, CueSupportFlags.FADE), is(false));
    }

    @Test
    public void detectsMultiFlags()
    {
        ICue cue = mock(ICue.class);
        when(cue.getSupportedOperations()).thenReturn(CueSupportFlags.FADE | CueSupportFlags.RESUME);

        assertThat(CueSupportFlags.Supports(cue, CueSupportFlags.FADE), is(true));
        assertThat(CueSupportFlags.Supports(cue, CueSupportFlags.RESUME), is(true));
    }
}
