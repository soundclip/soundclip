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

import soundclip.core.CueNumber;
import soundclip.core.cues.ICue;

import java.time.Duration;
import java.util.List;

/**
 * A simple note cue. This cue does nothing other than hold notes / a description
 */
public class NoteCue implements ICue
{
    private CueNumber number;
    private String name;
    private String notes;

    public NoteCue(CueNumber number)
    {
        this.number = number;
        name = "Untitled Notes Cue";
        notes = "";
    }

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
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void stop() {}
}
