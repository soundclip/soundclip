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
package soundclip.core.cues.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.util.Duration;
import soundclip.core.CueNumber;
import soundclip.core.cues.ICue;

import java.io.IOException;
import java.util.List;

/**
 * The base implementation for cues
 */
public abstract class CueBase implements ICue
{
    private CueNumber number;
    private String name;
    private String notes;
    private Duration preWaitDelay;
    private Duration postWaitDelay;

    @Override
    public CueNumber getNumber()
    {
        return number;
    }

    @Override
    public void setNumber(CueNumber number)
    {
        this.number = number;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getNotes()
    {
        return notes;
    }

    @Override
    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    @Override
    public Duration getPreWaitDelay()
    {
        return preWaitDelay;
    }

    @Override
    public void setPreWaitDelay(Duration delay)
    {
        preWaitDelay = delay;
    }

    @Override
    public Duration getPostWaitDelay()
    {
        return postWaitDelay;
    }

    @Override
    public void setPostWaitDelay(Duration delay)
    {
        postWaitDelay = delay;
    }

    @Override
    public List<String> getValidationErrors()
    {
        return null;
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    protected void deserializeCommonFields(JsonNode cue)
    {
        setName(cue.get("name").asText());
        setNotes(cue.get("notes").asText(null));
    }

    protected void serializeCommonFields(JsonGenerator w) throws IOException
    {
        w.writeStringField("name", getName());
        w.writeStringField("number", getNumber().toString());
        w.writeStringField("notes", getNotes());
    }
}
