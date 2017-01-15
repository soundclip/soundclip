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
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Duration;
import soundclip.core.CueNumber;
import soundclip.core.cues.ICue;

import java.io.IOException;
import java.util.List;

/**
 * A simple note cue. This cue does nothing other than hold notes / a description
 */
public class NoteCue extends CueBase
{

    public NoteCue(CueNumber number)
    {
        setNumber(number);
        setName("Untitled Notes Cue");
    }

    public NoteCue(CueNumber number, String name, String notes)
    {
        this(number);

        setName(name);
        setNotes(notes);
    }

    @Override
    public Duration getDuration()
    {
        return Duration.ZERO;
    }

    @Override
    public int getSupportedOperations()
    {
        return 0;
    }

    @Override
    public List<String> getValidationErrors()
    {
        return null;
    }

    @Override
    public void go() {}

    @Override
    public void stop() {}

    @Override
    public void load(JsonNode cue)
    {
        // Cue Number is set by the cue list deserializer
        deserializeCommonFields(cue);
    }

    @Override
    public void serialize(JsonGenerator w) throws IOException
    {
        w.writeStartObject();
        {
            // include the type so the cue list deserializer can load the right cue
            w.writeStringField("type", getClass().getCanonicalName());
            serializeCommonFields(w);
        }
        w.writeEndObject();
    }

    @Override
    public ReadOnlyObjectProperty<Duration> preWaitProgressProperty()
    {
        return new SimpleObjectProperty<>(Duration.ZERO);
    }

    @Override
    public Duration getPreWaitProgress()
    {
        return Duration.ZERO;
    }

    @Override
    public ReadOnlyObjectProperty<Duration> progressProperty()
    {
        return new SimpleObjectProperty<>(Duration.ZERO);
    }

    @Override
    public Duration getProgress()
    {
        return Duration.ZERO;
    }

    @Override
    public ReadOnlyObjectProperty<Duration> postWaitProgressProperty()
    {
        return new SimpleObjectProperty<>(Duration.ZERO);
    }

    @Override
    public Duration getPostWaitProgress()
    {
        return Duration.ZERO;
    }

    @Override
    public boolean isPerformingAction()
    {
        return false;
    }
}
