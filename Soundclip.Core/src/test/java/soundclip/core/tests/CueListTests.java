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

import org.junit.Test;
import org.mockito.Matchers;
import soundclip.core.CueList;
import soundclip.core.CueNumber;
import soundclip.core.ICue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link soundclip.core.CueList}
 */
public class CueListTests
{
    @Test
    public void constructWithName()
    {
        CueList list = new CueList("MyCueList");

        assertThat(list.getName(), is(equalTo("MyCueList")));
    }

    @Test
    public void constructWithCues()
    {
        ICue a = mock(ICue.class);
        ICue b = mock(ICue.class);
        ICue c = mock(ICue.class);

        when(a.getNumber()).thenReturn(new CueNumber(1));
        when(b.getNumber()).thenReturn(new CueNumber(2));
        when(c.getNumber()).thenReturn(new CueNumber(3));

        CueList list = new CueList(new HashSet<>(Arrays.asList(a,b,c)));

        assertThat(list.size(), is(equalTo(3)));
        for(ICue cue : list)
        {
            assertThat(cue, isOneOf(a, b, c));
        }
    }

    @Test
    public void constructWithNameAndCues()
    {
        ICue a = mock(ICue.class);
        ICue b = mock(ICue.class);
        ICue c = mock(ICue.class);

        when(a.getNumber()).thenReturn(new CueNumber(1));
        when(b.getNumber()).thenReturn(new CueNumber(2));
        when(c.getNumber()).thenReturn(new CueNumber(3));

        CueList list = new CueList("MyCueList", new HashSet<>(Arrays.asList(a,b,c)));

        assertThat(list.getName(), is(equalTo("MyCueList")));
        assertThat(list.size(), is(equalTo(3)));
        for(ICue cue : list)
        {
            assertThat(cue, isOneOf(a, b, c));
        }
    }

    @Test
    public void appendsSuffixIfSameNumberAlreadyInList()
    {
        ICue a = mock(ICue.class);
        ICue b = mock(ICue.class);

        final CueNumber[] numberProxy = new CueNumber[]{new CueNumber(1)};
        when(a.getNumber()).thenReturn(new CueNumber(1));
        when(b.getNumber()).thenReturn(numberProxy[0]);
        doAnswer((n) -> {
            numberProxy[0] = (CueNumber)n.getArguments()[0];
            return null;
        }).when(b).setNumber(Matchers.any(CueNumber.class));

        CueList list = new CueList();

        list.add(a);
        list.add(b);

        assertThat(a.getNumber(), is(equalTo(new CueNumber(1))));
        assertThat(numberProxy[0], is(equalTo(new CueNumber(1,5))));
    }

    @Test
    public void addsCueInOrder()
    {
        ICue a = mock(ICue.class);
        ICue b = mock(ICue.class);

        when(a.getNumber()).thenReturn(new CueNumber(1));
        when(b.getNumber()).thenReturn(new CueNumber(2));

        CueList list = new CueList();

        list.add(b);
        list.add(a);

        Iterator<ICue> cues = list.iterator();

        assertThat(cues.next().getNumber(), is(lessThan(cues.next().getNumber())));
        assertThat(cues.hasNext(), is(false));
    }

    @Test
    public void callsSignalOnAddCue()
    {
        final ICue toAdd = mock(ICue.class);
        when(toAdd.getNumber()).thenReturn(new CueNumber(1));

        final boolean[] callbackCalledProxy = new boolean[]{false};
        CueList list = new CueList();
        list.onCueAdded.whenTriggered((c) ->
        {
            assertThat(c == toAdd, is(true));
            callbackCalledProxy[0] = true;
        });

        list.add(toAdd);

        assertThat(callbackCalledProxy[0], is(true));
    }

    @Test
    public void removeByCueRef()
    {
        ICue c = mock(ICue.class);
        when(c.getNumber()).thenReturn(new CueNumber(1));

        CueList list = new CueList();
        list.add(c);

        list.remove(c);
        assertThat(list.size(), is(equalTo(0)));
        assertThat(list.isEmpty(), is(true));
    }

    @Test
    public void removeByCueNumber()
    {
        ICue c = mock(ICue.class);
        when(c.getNumber()).thenReturn(new CueNumber(1));

        CueList list = new CueList();
        list.add(c);

        list.remove(new CueNumber(1));
        assertThat(list.size(), is(equalTo(0)));
        assertThat(list.isEmpty(), is(true));
    }

    @Test
    public void callsSignalOnRemoveCue()
    {
        final ICue cue = mock(ICue.class);
        when(cue.getNumber()).thenReturn(new CueNumber(1));

        CueList list = new CueList();

        final boolean[] callbackCalledProxy = new boolean[]{false};
        list.onCueRemoved.whenTriggered((c) ->
        {
            assertThat(c == cue, is(true));
            callbackCalledProxy[0] = true;
        });

        list.add(cue);

        list.remove(cue);

        assertThat(callbackCalledProxy[0], is(true));
    }

    @Test
    public void canUpdateName()
    {
        CueList list = new CueList("a");

        final boolean[] callbackCalledProxy = new boolean[]{false};
        list.onNameChanged.whenTriggered((name) ->
        {
            assertThat(name, is(equalTo("b")));
            callbackCalledProxy[0] = true;
        });

        list.setName("b");

        assertThat(list.getName(), is(equalTo("b")));
        assertThat(callbackCalledProxy[0], is(true));
    }
}
