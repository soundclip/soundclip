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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 */
public class CueNumber implements Comparable<CueNumber>
{
    private final ArrayList<Integer> parts;

    private CueNumber()
    {
        parts = new ArrayList<>();
    }

    public CueNumber(int...parts)
    {
        this();

        for(int i : parts)
        {
            if(i < 0) throw new IllegalArgumentException("All parts must be positive");
            this.parts.add(i);
        }

        if(this.parts.size() == 0) throw new IllegalArgumentException("No number provided");
    }

    public CueNumber(String fromString)
    {
        this();

        if(fromString.trim().isEmpty()) throw new IllegalArgumentException("No number provided");

        for(String s : fromString.split("\\."))
        {
            int i = Integer.parseInt(s);

            if(i < 0) throw new IllegalArgumentException("All parts must be positive");
            parts.add(i);
        }

        if(parts.size() == 0) throw new IllegalArgumentException("No number provided");
    }

    @Override
    public String toString()
    {
        return parts.stream().map(Object::toString).collect(Collectors.joining("."));
    }

    @Override
    public int compareTo(CueNumber o)
    {
        if(o == null) throw new NullPointerException("The other cue is null");
        if(o == this) return 0;

        int compare;
        for(int i=0; i < Math.min(parts.size(), o.parts.size()); i++)
        {
            compare = parts.get(i).compareTo(o.parts.get(i));

            if(compare != 0) return compare;
        }

        return parts.size() == o.parts.size() ? 0 : parts.size() > o.parts.size() ? 1 : -1;
    }

    @Override
    public boolean equals(Object o)
    {
        return o == this || (o instanceof CueNumber && ((CueNumber)o).parts.equals(this.parts));
    }
}
