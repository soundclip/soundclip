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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.core.cues.impl.NoteCue;
import soundclip.core.interop.Signal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * A project is the basic unit of work. Each project contains a collection of cue lists
 * and metadata for the project.
 */
public class Project implements Iterable<CueList>
{
    private static Logger Log = LogManager.getLogger(Project.class);

    private String projectPath;
    private String name;
    private Date lastModified;
    private boolean dirty;

    private long panicHardStopBefore;

    private final ArrayList<CueList> cueLists;

    /** A signal triggered when the project name is changed */
    public final Signal<String> onRenamed = new Signal<>();
    /** A signal triggered when the project path is set for the first time */
    public final Signal<String> onPathSet = new Signal<>();

    /** A signal triggered when a cue list is added */
    public final Signal<CueList> onCueListAdded = new Signal<>();
    /** A signal triggered when a cue list is removed */
    public final Signal<CueList> onCueListRemoved = new Signal<>();

    /**
     * Creates a new, empty Project
     */
    public Project()
    {
        projectPath = null;
        name = "Untitled Project";
        lastModified = null;
        cueLists = new ArrayList<>();

//        CueList defaultCueList = appendCueList("Default Cue List");
//        defaultCueList.add(new NoteCue(new CueNumber(1), "This is the default cue list", "With some default notes"));
    }

    /**
     * Loads the project at the specified path on disk
     *
     * @param fromPath the path to load the project from
     *
     * @throws IOException if the project could not be loaded successfully
     */
    public Project(String fromPath) throws IOException
    {
        this();

        cueLists.clear();

        // TODO: Load project
    }

    public CueList appendCueList(String name)
    {
        CueList c = new CueList(name);

        cueLists.add(c);
        onCueListAdded.post(c);

        return c;
    }

    public void removeCueList(String name)
    {
        CueList list = cueLists.stream().filter(l -> l.getName().equals(name)).findFirst().orElse(null);

        if(list != null) removeCueList(list);
    }

    public void removeCueList(CueList list)
    {
        boolean removed = cueLists.remove(list);

        if(removed) onCueListRemoved.post(list);
    }

    /** @return the path to the project on the filesystem */
    public String getPath()
    {
        return projectPath;
    }

    /**
     * Sets the path to the project on the filesystem
     *
     * @throws IllegalStateException If the project was already saved (It already has a path)
     */
    public void setPath(String projectPath)
    {
        if (this.projectPath != null) throw new IllegalStateException("The project has already been saved");

        this.projectPath = projectPath;

        onPathSet.post(this.projectPath);
    }

    /** @return the name of the project */
    public String getName()
    {
        return name;
    }

    /** Sets the name of the project */
    public void setName(String name)
    {
        this.name = name;

        onRenamed.post(this.name);
    }

    /** @return The last date and time the project was modified */
    public Date getLastModified()
    {
        return lastModified;
    }

    /** @return The number of cue lists in the project */
    public int getCueListCount() { return cueLists.size(); }

    /** @return The number of cues in all cue lists */
    public int getCueCount() { return cueLists.stream().mapToInt(CueList::size).sum(); }

    /** @return the number of milliseconds before which all cues will be hard-stopped if another panic is issued */
    public long getPanicHardStopBefore() { return panicHardStopBefore; }

    /** Set the number of milliseconds that must expire before a hard stop will not be performed */
    public void setPanicHardStopBefore(long panicHardStopBefore) { this.panicHardStopBefore = panicHardStopBefore; }

    /** Panic all cues. If {@param hard} is {@code true}, don't fade out gracefully */
    public void panic(boolean hard)
    {
        Log.warn("PANIC! {}", hard ? "Hard-stopping all cues" : "Stopping all cues gracefully");
        cueLists.parallelStream().forEach((list) -> list.panic(hard));
    }

    /**
     * Saves the project
     *
     * @throws IllegalStateException if a project path has not yet been set
     */
    public void save()
    {
        if (projectPath == null) throw new IllegalStateException("The project path has not been specified");

        // TODO: Serialize project
    }

    @Override
    public Iterator<CueList> iterator()
    {
        return cueLists.iterator();
    }

    public boolean isDirty()
    {
        return dirty;
    }
}
