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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.core.cues.ICue;
import soundclip.core.cues.IFadeableCue;
import soundclip.core.cues.impl.FXAudioCue;
import soundclip.core.cues.impl.NoteCue;
import soundclip.core.interop.Signal;

import java.io.IOException;
import java.util.*;

/**
 * A named, ordered, collection of cues
 */
public class CueList implements Iterable<ICue>
{
    private static Logger Log = LogManager.getLogger(CueList.class);

    private String name;
    private final ObservableList<ICue> backingList;
    private final SortedList<ICue> cues;

    /** A signal that is triggered when the name of the cue list changes */
    public final Signal<String> onNameChanged = new Signal<>();
    /** A signal that is triggered when a cue is added to the list */
    public final Signal<ICue> onCueAdded = new Signal<>();
    /** A signal that is triggered when a cue is removed from the list */
    public final Signal<ICue> onCueRemoved = new Signal<>();

    public CueList()
    {
        backingList = FXCollections.observableArrayList();
        cues = new SortedList<>(backingList);
        cues.setComparator(Comparator.comparing(ICue::getNumber));
        name = "Untitled Cue List";
    }

    public CueList(String name)
    {
        this();
        this.name = name;
    }

    public CueList(Set<ICue> cues)
    {
        this();
        this.backingList.addAll(cues);
    }

    public CueList(String name, Set<ICue> cues)
    {
        this(cues);
        this.name = name;
    }

    /** Panic-stop all cues in the list */
    public void panic(boolean hard)
    {
        Log.debug("PANIC! {} (in list {})", hard ? "Hard-stopping all cues" : "Stopping all cues gracefully", name);
        cues.parallelStream().forEach((cue) ->
        {
            if(hard || !(cue instanceof IFadeableCue))
            {
                Log.debug("PANIC! Stopping {} - {}", cue.getNumber(), cue.getName());
                cue.stop();
            }
            else
            {
                Log.debug("PANIC! Fading out {} - {}", cue.getNumber(), cue.getName());
                ((IFadeableCue)cue).fadeOut();
            }
        });
    }

    /**
     * Adds the cue to the cue list. If a cue with the same number exists in the list,
     * the newly added cue will be suffixed with ".5"
     *
     * @param cue the cue to insert
     */
    public void add(ICue cue)
    {
        // Append .5 to the number if the cue number already exists in the list
        if(cues.stream().filter(c -> c.getNumber().equals(cue.getNumber())).count() != 0)
        {
            Log.warn("Duplicate cue found in list ({}). Appending .5 suffix", cue.getNumber());
            cue.setNumber(new CueNumber(cue.getNumber(), 5));
        }

        backingList.add(cue);

        onCueAdded.post(cue);
    }

    /**
     * Remove the specified cue from the cue list
     *
     * @param cue
     */
    public void remove(ICue cue)
    {
        remove(cue.getNumber());
    }

    /**
     * Remove the cue specified by the provided number from the cue list
     *
     * @param cue
     */
    public void remove(CueNumber cue)
    {
        Optional<ICue> removed = cues.stream().filter(c -> c.getNumber().equals(cue)).findFirst();

        if(removed.isPresent())
        {
            backingList.remove(removed.get());
            onCueRemoved.post(removed.get());
        }
    }

    /** @return the name of the Cue List */
    public String getName()
    {
        return name;
    }

    /** Sets the name of the cue list */
    public void setName(String name)
    {
        this.name = name;

        onNameChanged.post(name);
    }

    /** @return the number of cues in the list */
    public int size()
    {
        return cues.size();
    }

    /** @return {@code true} iff the list is empty */
    public boolean isEmpty() { return cues.isEmpty(); }

    /** @return the index of the specified cue, or -1 if the cue is not in the list */
    public int indexOf(ICue c) { return cues.indexOf(c); }

    /** @return the first {@link ICue} in the list */
    public ICue first()
    {
         return cues.size() > 0 ? cues.get(0) : null;
    }

    /**
     * @return the cue before the specified cue
     * @param c the cue
     */
    public ICue previous(ICue c)
    {
        int i = cues.indexOf(c);
        if(i <= 0) return null;

        return cues.get(i-1);
    }

    /**
     * @return the cue following the specified cue
     * @param c the cue
     */
    public ICue next(ICue c)
    {
        int i = cues.indexOf(c);
        if(i == cues.size() - 1 || i == -1) return null;

        return cues.get(i+1);
    }

    /** @return the last {@link ICue} in the list */
    public ICue last()
    {
        return cues.size() > 0 ? cues.get(cues.size() - 1) : null;
    }

    public SortedList<ICue> getCues() { return cues; }

    @Override
    public Iterator<ICue> iterator()
    {
        return cues.iterator();
    }

    void load(JsonNode cueList)
    {
        Log.debug("Loading CueList {}", name);

        // name is initialized by the project
        for(JsonNode cue : cueList.get("cues"))
        {
            CueNumber number = new CueNumber(cue.get("number").asText());
            String typeName = cue.get("type").asText();
            ICue c;

            Log.debug("\tLoading Cue {} ({})", number, typeName);

            if(typeName.equals(FXAudioCue.class.getCanonicalName()))
            {
                c = new FXAudioCue(number);
                c.load(cue);
            }
            else if(typeName.equals(NoteCue.class.getCanonicalName()))
            {
                c = new NoteCue(number);
                c.load(cue);
            }
            else
            {
                Log.warn("Unable to load cue of type {} (Unknown cue type)", typeName);
                continue;
            }

            add(c);
        }
    }

    void serialize(JsonGenerator w) throws IOException
    {
        Log.debug("Writing cue list '{}'", name);
        w.writeStartObject();
        {
            w.writeStringField("name", name);
            w.writeArrayFieldStart("cues");
            {
                for(ICue c : this)
                {
                    Log.debug("\tWriting cue {} - {}", c.getNumber(), c.getName());
                    c.serialize(w);
                }
            }
            w.writeEndArray();
        }
        w.writeEndObject();
    }
}
