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
package soundclip.input;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * A collection of key codes
 */
public class KeyCombination
{
    private final KeyCode key;
    private final boolean control;
    private final boolean alt;
    private final boolean shift;
    private final boolean meta;

    public KeyCombination(KeyCode key)
    {
        this(key, false, false, false, false);
    }

    public KeyCombination(KeyCode key, boolean control, boolean alt, boolean shift, boolean meta)
    {
        this.key = key;
        this.control = control;
        this.alt = alt;
        this.shift = shift;
        this.meta = meta;
    }

    public KeyCombination(String string)
    {
        KeyCode parsedCode = null;
        boolean hasControl = false;
        boolean hasAlt = false;
        boolean hasShift = false;
        boolean hasMeta = false;

        String[] parts = string.split("\\+");

        for(String s : parts)
        {
            String p = s.trim().toLowerCase();
            switch (p)
            {
                case "shift":
                    hasShift = true;
                    break;
                case "ctrl":
                case "control":
                    hasControl = true;
                    break;
                case "alt":
                    hasAlt = true;
                    break;
                case "super":
                case "meta":
                case "win":
                    hasMeta = true;
                    break;
                default:
                    if (parsedCode != null) throw new IllegalArgumentException("Multiple keys specified");
                    parsedCode = KeyCode.getKeyCode(s.trim());
                    break;
            }
        }

        if(parsedCode == null) throw new IllegalArgumentException("No Key Specified");

        key = parsedCode;
        control = hasControl;
        alt = hasAlt;
        shift = hasShift;
        meta = hasMeta;
    }

    public boolean match(KeyEvent e)
    {
        return key == e.getCode() &&
               control == e.isControlDown() &&
               alt == e.isAltDown() &&
               shift == e.isShiftDown() &&
               meta == e.isMetaDown();
    }

    @Override
    public String toString()
    {
        return (meta ? "meta+" : "") + (control ? "ctrl+" : "") + (alt ? "alt+" : "") + (shift ? "shift+" : "") + key.getName();
    }
}
