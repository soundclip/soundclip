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
package soundclip.controls;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import soundclip.Utils;
import soundclip.core.cues.ICue;

/**
 * Displays notes for the previous cue, current cue, and the cue on deck
 */
public class NotesPane extends GridPane
{

    @FXML private TextArea previousNotes;
    @FXML private TextArea currentNotes;
    @FXML private TextArea onDeckNotes;

    public NotesPane()
    {
        FXMLLoader loader = Utils.load(this, "ui/controls/NotesPane.fxml");
    }

    public void updateNotes(ICue previous, ICue current, ICue onDeck)
    {
        if(previous != null) previousNotes.setText(previous.getNotes());
        if(current  != null) currentNotes.setText(current.getNotes());
        if(onDeck   != null) onDeckNotes.setText(onDeck.getNotes());
    }
}
