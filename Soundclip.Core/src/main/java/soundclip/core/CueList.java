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

import soundclip.core.interop.Signal;

import java.util.*;

/**
 * A named, ordered, collection of cues
 */
public class CueList implements Iterable<ICue>
{
    private String name;
    private final TreeMap<CueNumber, ICue> cues;

    /** A signal that is triggered when the name of the cue list changes */
    public final Signal<String> onNameChanged = new Signal<>();
    /** A signal that is triggered when a cue is added to the list */
    public final Signal<ICue> onCueAdded = new Signal<>();
    /** A signal that is triggered when a cue is removed from the list */
    public final Signal<ICue> onCueRemoved = new Signal<>();

    /** A wrapper iterator to iterate over all cues in the list in order */
    private class CueListIterator implements Iterator<ICue>
    {
        private final Iterator<java.util.Map.Entry<CueNumber, ICue>> entrySetIterator;

        CueListIterator(Iterator<java.util.Map.Entry<CueNumber, ICue>> entrySetIterator)
        {
            this.entrySetIterator = entrySetIterator;
        }

        @Override
        public boolean hasNext()
        {
            return entrySetIterator.hasNext();
        }

        @Override
        public ICue next()
        {
            return entrySetIterator.next().getValue();
        }
    }

    protected CueList()
    {
        cues = new TreeMap<>();
        name = "Untitled Cue List";
    }

    protected CueList(String name)
    {
        this();
        this.name = name;
    }

    protected CueList(Set<ICue> cues)
    {
        this();
        for(ICue c : cues)
        {
            this.cues.put(c.getNumber(), c);
        }
    }

    protected CueList(String name, Set<ICue> cues)
    {
        this();
        for(ICue c : cues)
        {
            this.cues.put(c.getNumber(), c);
        }
        this.name = name;
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
        if(cues.containsKey(cue.getNumber())) cue.setNumber(new CueNumber(cue.getNumber(), 5));

        cues.put(cue.getNumber(), cue);

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
        ICue removed = cues.remove(cue);

        if(removed != null)
        {
            onCueRemoved.post(removed);
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

    @Override
    public Iterator<ICue> iterator()
    {
        return new CueListIterator(cues.entrySet().iterator());
    }
}
