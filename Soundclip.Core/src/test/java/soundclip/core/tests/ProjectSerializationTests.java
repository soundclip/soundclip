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
import soundclip.core.Project;
import soundclip.core.cues.ICue;
import soundclip.core.cues.impl.NoteCue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests for serializing {@link soundclip.core.Project}s
 */
public class ProjectSerializationTests
{
    @Test
    public void canLoadProjectFromDisk() throws IOException
    {
        Project p = new Project(getClass().getClassLoader().getResource("SampleProject/Sample.scproj").getPath());

        assertThat(p, is(not(nullValue())));

        assertThat(p.getName(), is(equalTo("Sample Project")));
        assertThat(p.getLastModified(), is(equalTo(ZonedDateTime.parse("2016-12-03T23:25:27-05:00"))));
        assertThat(p.getPanicHardStopBefore(), is(equalTo(3000L)));

        assertThat(p.getCueListCount(), is(equalTo(2)));
        assertThat(p.getCueCount(), is(equalTo(3)));

        Iterator<CueList> cueLists = p.iterator();
        {
            CueList first = cueLists.next();
            assertThat(first.getName(), is(equalTo("Sample Cue List")));
            assertThat(first.size(), is(equalTo(2)));
            Iterator<ICue> firstList = first.iterator();
            {
                ICue firstListFirstCue = firstList.next();
                assertThat(firstListFirstCue, is(instanceOf(NoteCue.class)));
                assertThat(firstListFirstCue.getName(), is(equalTo("First Cue")));
                assertThat(firstListFirstCue.getNumber(), is(equalTo(new CueNumber(1))));
                assertThat(firstListFirstCue.getNotes(), is(equalTo("with some notes")));

                ICue firstListSecondCue = firstList.next();
                assertThat(firstListSecondCue, is(instanceOf(NoteCue.class)));
                assertThat(firstListSecondCue.getName(), is(equalTo("Second Cue")));
                assertThat(firstListSecondCue.getNumber(), is(equalTo(new CueNumber(2))));
                assertThat(firstListSecondCue.getNotes(), is(nullValue()));
            }

            assertThat(firstList.hasNext(), is(false));

            CueList second = cueLists.next();
            assertThat(second.getName(), is(equalTo("Second Cue List")));
            assertThat(second.size(), is(equalTo(1)));
            Iterator<ICue> secondList = second.iterator();
            {
                ICue secondListFirstCue = secondList.next();
                assertThat(secondListFirstCue, is(instanceOf(NoteCue.class)));
                assertThat(secondListFirstCue.getName(), is(equalTo("Cue in second list")));
                assertThat(secondListFirstCue.getNumber(), is(equalTo(new CueNumber(1))));
                assertThat(secondListFirstCue.getNotes(), is(equalTo("notes")));
            }
        }

        assertThat(cueLists.hasNext(), is(false));
    }

    @Test
    public void throwsWhenProjectIsMissingFields()
    {
        try
        {
            new Project(getClass().getClassLoader().getResource("MalformedProject/ProjectMissingFields.scproj").getPath());
            fail("Should not be able to load a malformed project file");
        }
        catch(Exception ex)
        {
            assertThat(ex, is(instanceOf(IllegalArgumentException.class)));
            assertThat(ex.getMessage(), is(equalTo("The specified project is missing required fields")));
        }
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
    public void canSaveProject() throws IOException
    {
        Project p = new Project();

        File tempProjectPath = Paths.get(System.getProperty("java.io.tmpdir"), "soundclip-"+UUID.randomUUID().toString()).toFile();
        tempProjectPath.deleteOnExit();

        if(!tempProjectPath.exists()) tempProjectPath.mkdirs();
        File tempProject = new File(tempProjectPath, "test.scproj");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            tempProject.delete();
            tempProjectPath.delete();
        }));

        p.setPath(tempProject.getPath());
        p.save();

        // TODO: Verify the project saved correctly when we actually implement serialization / deserialization
    }
}
