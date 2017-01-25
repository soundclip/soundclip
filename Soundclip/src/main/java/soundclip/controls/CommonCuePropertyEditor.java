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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import soundclip.Utils;
import soundclip.core.ProgressType;
import soundclip.core.cues.ICue;

/**
 * The editor pane for shared cue properties
 */
public class CommonCuePropertyEditor extends GridPane
{
    private ICue model;

    @FXML private TextField nameEditor;
    @FXML private TextArea notesEditor;

    @FXML private TimePicker preWaitDelay, postWaitDelay;

    @FXML private ComboBox<String> progressType;

    public CommonCuePropertyEditor()
    {
        FXMLLoader loader = Utils.load(this, "ui/controls/CommonCuePropertyEditor.fxml");
    }

    public void setModel(ICue model)
    {
        this.model = model;

        nameEditor.setText(model.getName());
        notesEditor.setText(model.getNotes());

        preWaitDelay.setTime(model.getPreWaitDelay());
        postWaitDelay.setTime(model.getPostWaitDelay());

        progressType.getSelectionModel().select(model.getProgressType().ordinal());
    }

    public void apply()
    {
        model.setName(nameEditor.getText());
        model.setNotes(notesEditor.getText());
        model.setPreWaitDelay(preWaitDelay.getTime());
        model.setPostWaitDelay(postWaitDelay.getTime());
        model.setProgressType(ProgressType.fromOrdinal(progressType.getSelectionModel().getSelectedIndex()));
    }
}
