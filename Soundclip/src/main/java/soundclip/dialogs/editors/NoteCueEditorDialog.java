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
package soundclip.dialogs.editors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import soundclip.Utils;
import soundclip.controls.CommonCuePropertyEditor;
import soundclip.core.CueNumber;
import soundclip.core.cues.impl.NoteCue;

/**
 * The editor for {@link soundclip.core.cues.impl.NoteCue}s
 */
public class NoteCueEditorDialog extends BorderPane
{
    private final NoteCue model;
    private boolean isNewCue = false;
    private boolean isSuccess = false;

    private Stage dialog;

    @FXML
    private CommonCuePropertyEditor commonEditor;

    public NoteCueEditorDialog(CueNumber number)
    {
        this(new NoteCue(number));
        isNewCue = true;
    }

    public NoteCueEditorDialog(NoteCue cue)
    {
        FXMLLoader fxmlLoader = Utils.load(this, "ui/dialogs/editors/NoteCueEditorDialog.fxml");

        model = cue;
        commonEditor.setModel(model);
    }

    public void present()
    {
        dialog = new Stage();
        dialog.setResizable(false);
        dialog.setTitle((isNewCue ? "Add" : "Edit") + " Note Cue " + model.getNumber() + " - " + model.getName());
        dialog.setScene(new Scene(this, 640, 500));
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }

    @FXML
    private void doApply(ActionEvent e)
    {
        isSuccess = true;
        commonEditor.apply();
        dialog.close();
    }

    @FXML
    private void onCancel(ActionEvent e)
    {
        dialog.close();
    }

    public boolean isSuccess() { return isSuccess; }
    public boolean isNewCue() { return isNewCue; }
    public NoteCue getModel() { return model; }
}
