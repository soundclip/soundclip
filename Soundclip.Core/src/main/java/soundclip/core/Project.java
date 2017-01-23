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

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.core.cues.impl.NoteCue;
import soundclip.core.interop.Signal;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * A project is the basic unit of work. Each project contains a collection of cue lists
 * and metadata for the project.
 */
public class Project implements Iterable<CueList>, AutoCloseable
{
    private static Logger Log = LogManager.getLogger(Project.class);

    private String projectPath;
    private String name;
    private ZonedDateTime lastModified = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.of("UTC"));
    private boolean dirty;

    private long panicHardStopBefore = 3 * 1000;
    private long lastPanicAt = 0;

    private final ArrayList<CueList> cueLists;

    /** A signal triggered when the project name is changed */
    public final Signal<String> onRenamed = new Signal<>();
    /** A signal triggered when the project path is set for the first time */
    public final Signal<String> onPathSet = new Signal<>();

    /** A signal triggered when a cue list is added */
    public final Signal<CueList> onCueListAdded = new Signal<>();
    /** A signal triggered when a cue list is removed */
    public final Signal<CueList> onCueListRemoved = new Signal<>();

    private Project()
    {
        projectPath = null;
        name = "Untitled Project";
        lastModified = null;
        cueLists = new ArrayList<>();

        appendCueList("Default Cue List");
    }

    /**
     * Loads the project at the specified path on disk
     *
     * @param path the path to load the project from
     *
     * @throws IOException if the project could not be loaded successfully
     */
    public Project(String path) throws IOException
    {
        this(path, null);
    }

    public Project(String path, String name) throws IOException
    {
        this();

        if(path == null) throw new IllegalArgumentException("Project path cannot be null");

        projectPath = path;
        if(name != null) this.name = name;

        File f = new File(path);

        if(!f.exists())
        {
            Log.info("Creating new project at '{}'", path);
            save();
            return;
        }

        if(!f.isFile()) throw new IllegalArgumentException("File does not exist or is not a file '" + path + "'");
        Log.info("Loading project from '{}'", path);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode project = mapper.readTree(f);

        try
        {
            this.name = project.get("name").asText();
            lastModified = ZonedDateTime.parse(project.get("lastModified").asText());
            panicHardStopBefore = project.get("panicHardStopBefore").asLong();

            cueLists.clear();

            for(JsonNode cueList : project.get("cueLists"))
            {
                CueList c = appendCueList(cueList.get("name").asText());
                c.load(cueList);
            }
        }
        catch (NullPointerException ex)
        {
            Log.fatal("Unable to read project (missing field)", ex);
            throw new IllegalArgumentException("The specified project is missing required fields", ex);
        }

        Log.info("Project loaded successfully");
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
    public ZonedDateTime getLastModified()
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

    /** Panic all cues. */
    public void panic()
    {
        long now = System.currentTimeMillis();
        panic(now - lastPanicAt <= getPanicHardStopBefore());
        lastPanicAt = now;
    }

    /** Panic all cues. If {@param hard} is {@code true}, don't fade out gracefully */
    public void panic(boolean hard)
    {
        Log.warn("PANIC! {}", hard ? "Hard-stopping all cues" : "Stopping all cues gracefully");
        cueLists.forEach((list) -> list.panic(hard));
    }

    /**
     * Saves the project
     *
     * @throws IllegalStateException if a project path has not yet been set
     */
    public void save() throws IOException
    {
        if (projectPath == null) throw new IllegalStateException("The project path has not been specified");

        File p = new File(projectPath);
        new File(p.getParent()).mkdirs();

        Log.info("Saving project '{}' to '{}'", name, projectPath);

        lastModified = ZonedDateTime.now();
        ObjectMapper m = new ObjectMapper();
        JsonFactory f = m.getFactory();

        try(JsonGenerator writer = f.createGenerator(p, JsonEncoding.UTF8))
        {
            writer.useDefaultPrettyPrinter();

            writer.writeStartObject();
            {
                writer.writeStringField("name", name);
                writer.writeStringField("lastModified", lastModified.format(DateTimeFormatter.ISO_INSTANT));
                writer.writeNumberField("panicHardStopBefore", panicHardStopBefore);

                writer.writeArrayFieldStart("cueLists");
                {
                    for(CueList c : this)
                    {
                        c.serialize(writer);
                    }
                }
                writer.writeEndArray();
            }
            writer.writeEndObject();
        }

        Log.info("Project saved");
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

    @Override
    public void close() throws Exception
    {
        for(CueList list : this)
        {
            list.close();
        }
    }
}
