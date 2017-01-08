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
import java.util.stream.Collectors;

/**
 * Cue Numbers are comprised of one or more non-negative integers separated by a dot (".").
 *
 * The most significant part is the leftmost part. Therefore, 1.0.1 < 1.0 and so on.
 */
public class CueNumber implements Comparable<CueNumber>
{
    private final ArrayList<Integer> parts;
    private final String toString;

    /**
     * Construct a Cue Number from the specified integers. The 0th integer
     * is the most significant. The first part must be nonzero.
     *
     * @param fromInts the parts of the number
     */
    public CueNumber(int...fromInts)
    {
        parts = new ArrayList<>(fromInts.length);

        for(int i : fromInts)
        {
            if(i < 0) throw new IllegalArgumentException("All parts must be positive");
            parts.add(i);
        }

        for(int i = parts.size() - 1; i >= 0; i--)
        {
            if(parts.get(i) != 0) break;

            parts.remove(i);
        }

        if(parts.size() == 0) throw new IllegalArgumentException("No number provided");
        toString = parts.stream().map(Object::toString).collect(Collectors.joining("."));
    }

    /**
     * Construct a cue number from the specified string. The string should be comprised of
     * only non-negative integers separated by a dot. The first part must be nonzero.
     *
     * @param fromString the string to create the number from
     */
    public CueNumber(String fromString)
    {
        if(fromString.trim().isEmpty()) throw new IllegalArgumentException("No number provided");

        String[] stringParts = fromString.split("\\.");
        parts = new ArrayList<>(stringParts.length);

        for(String s : stringParts)
        {
            int i = Integer.parseInt(s);

            if(i < 0) throw new IllegalArgumentException("All parts must be positive");
            parts.add(i);
        }

        for(int i = this.parts.size() - 1; i >= 0; i--)
        {
            if(this.parts.get(i) != 0) break;

            this.parts.remove(i);
        }

        if(parts.size() == 0) throw new IllegalArgumentException("No number provided");
        toString = parts.stream().map(Object::toString).collect(Collectors.joining("."));
    }

    /**
     * Construct a new CueNumber prefixed by that of the specified number
     *
     * @param prefix the prefix of the newly constructed cue
     * @param suffix the suffix to append
     */
    public CueNumber(CueNumber prefix, int...suffix)
    {
        if(suffix.length == 0) throw new IllegalArgumentException("No suffix provided");

        parts = new ArrayList<>(prefix.parts.size() + suffix.length);
        parts.addAll(prefix.parts);

        for(int i : suffix)
        {
            if(i < 0) throw new IllegalArgumentException("All parts must be positive");
            parts.add(i);
        }

        for(int i = parts.size() - 1; i >= 0; i--)
        {
            if(parts.get(i) != 0) break;

            parts.remove(i);
        }

        toString = parts.stream().map(Object::toString).collect(Collectors.joining("."));
    }

    /**
     * Construct a new CueNumber prefixed by that of the specified number. The suffix should be comprised of
     * only non-negative integers separated by a dot.
     *
     * @param prefix the prefix of the newly constructed cue
     * @param suffix the suffix to append
     */
    public CueNumber(CueNumber prefix, String suffix)
    {
        if(suffix.length() == 0) throw new IllegalArgumentException("No suffix provided");

        String[] stringParts = suffix.split("\\.");
        parts = new ArrayList<>(prefix.parts.size() + stringParts.length);
        parts.addAll(prefix.parts);

        for(String s : stringParts)
        {
            int i = Integer.parseInt(s);

            if(i < 0) throw new IllegalArgumentException("All parts must be positive");
            parts.add(i);
        }

        for(int i = this.parts.size() - 1; i >= 0; i--)
        {
            if(this.parts.get(i) != 0) break;

            this.parts.remove(i);
        }

        toString = parts.stream().map(Object::toString).collect(Collectors.joining("."));
    }

    /** @return The first part of the cue number */
    public int getMajorNumber()
    {
        return parts.get(0);
    }

    @Override
    public String toString()
    {
        return toString;
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
