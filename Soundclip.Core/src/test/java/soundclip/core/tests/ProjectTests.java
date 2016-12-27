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
import soundclip.core.CueList;
import soundclip.core.CueNumber;
import soundclip.core.cues.ICue;
import soundclip.core.Project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link soundclip.core.Project}
 */
public class ProjectTests
{
    @Test
    public void canLoadProjectFromDisk() throws IOException
    {
        Project p = new Project(getClass().getClassLoader().getResource("SampleProject/Sample.scproj").getPath());

        assertThat(p, is(not(nullValue())));

        // TODO: Validate deserialization when we actually write the serialization / deserialization code
        assertThat(p.getLastModified(), is(nullValue()));
    }

    @Test
    public void canAddCueList()
    {
        Project p = new Project();

        final boolean[] callbackCalledProxy = new boolean[]{false};
        p.onCueListAdded.whenTriggered((c) ->
        {
            assertThat(c.getName(), is(equalTo("asdf")));
            callbackCalledProxy[0] = true;
        });

        CueList c = p.appendCueList("asdf");

        assertThat(p.getCueListCount(), is(equalTo(2)));
        assertThat(callbackCalledProxy[0], is(true));
        assertThat(c.getName(), is(equalTo("asdf")));
    }

    @Test
    public void canRemoveCueListByName()
    {
        Project p = new Project();
        CueList list = p.appendCueList("asdf");

        final boolean[] callbackCalledProxy = new boolean[]{false};
        p.onCueListRemoved.whenTriggered((c) ->
        {
            assertThat(c == list, is(true));
            callbackCalledProxy[0] = true;
        });

        p.removeCueList("asdf");

        assertThat(p.getCueListCount(), is(equalTo(1)));
        assertThat(callbackCalledProxy[0], is(true));
    }

    @Test
    public void canRemoveCueListByRef()
    {
        Project p = new Project();
        CueList list = p.appendCueList("asdf");

        final boolean[] callbackCalledProxy = new boolean[]{false};
        p.onCueListRemoved.whenTriggered((c) ->
        {
            assertThat(c == list, is(true));
            callbackCalledProxy[0] = true;
        });

        p.removeCueList(list);

        assertThat(p.getCueListCount(), is(equalTo(1)));
        assertThat(callbackCalledProxy[0], is(true));
    }

    @Test
    public void canSetPathOnlyOnce()
    {
        Project p = new Project();

        String path = "/path/to/project";
        p.setPath(path);

        assertThat(p.getPath(), is(equalTo(path)));

        try
        {
            p.setPath("/some/other/path");
            fail();
        }
        catch(Exception ex)
        {
            assertThat(ex, is(instanceOf(IllegalStateException.class)));
            assertThat(ex.getMessage(), is(equalTo("The project has already been saved")));
        }
    }

    @Test
    public void canSetName()
    {
        Project p = new Project();

        String name = "asdf";
        p.setName(name);

        assertThat(p.getName(), is(equalTo(name)));
    }

    @Test
    public void countsAllCuesInAllLists()
    {
        Project p = new Project();

        CueList a = p.appendCueList("a");
        CueList b = p.appendCueList("b");
        CueList c = p.appendCueList("c");

        ICue cueA1 = mock(ICue.class);
        when(cueA1.getNumber()).thenReturn(new CueNumber(1));
        a.add(cueA1);

        ICue cueB1 = mock(ICue.class);
        ICue cueB2 = mock(ICue.class);
        when(cueB1.getNumber()).thenReturn(new CueNumber(1));
        when(cueB2.getNumber()).thenReturn(new CueNumber(2));
        b.add(cueB1);
        b.add(cueB2);

        ICue cueC1 = mock(ICue.class);
        ICue cueC2 = mock(ICue.class);
        ICue cueC3 = mock(ICue.class);
        when(cueC1.getNumber()).thenReturn(new CueNumber(1));
        when(cueC2.getNumber()).thenReturn(new CueNumber(2));
        when(cueC3.getNumber()).thenReturn(new CueNumber(3));
        c.add(cueC1);
        c.add(cueC2);
        c.add(cueC3);

        assertThat(p.getCueCount(), is(equalTo(6)));
    }

    @Test
    public void saveProjectThrowsWhenPathNotSet()
    {
        Project p = new Project();

        try
        {
            p.save();
            fail();
        }
        catch(Exception ex)
        {
            assertThat(ex, is(instanceOf(IllegalStateException.class)));
            assertThat(ex.getMessage(), is(equalTo("The project path has not been specified")));
        }
    }

    @Test
    public void canSaveProject()
    {
        Project p = new Project();

        File tempProjectPath = Paths.get(System.getProperty("java.io.tempdir"), UUID.randomUUID().toString()).toFile();
        tempProjectPath.deleteOnExit();

        if(!tempProjectPath.exists()) tempProjectPath.mkdirs();
        File tempProject = new File(tempProjectPath, "test.scproj");

        p.setPath(tempProject.getPath());
        p.save();

        // TODO: Verify the project saved correctly when we actually implement serialization / deserialization
    }

    @Test
    public void canIterateOverCueLists()
    {
        Project p = new Project();
        p.appendCueList("a");
        p.appendCueList("b");

        Iterator<CueList> iter = p.iterator();

        assertThat(iter.next().getName(), is(equalTo("Default Cue List")));
        assertThat(iter.next().getName(), is(equalTo("a")));
        assertThat(iter.next().getName(), is(equalTo("b")));
        assertThat(iter.hasNext(), is(false));
    }
}
