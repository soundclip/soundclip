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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import soundclip.Soundclip;
import soundclip.Utils;
import soundclip.controls.CommonCuePropertyEditor;
import soundclip.controls.TimePicker;
import soundclip.core.CueNumber;
import soundclip.core.cues.ICue;
import soundclip.core.cues.IFadeableCue;
import soundclip.core.cues.impl.FadeCue;

/**
 * The editor for {@link soundclip.core.cues.impl.FadeCue}s
 */
public class FadeCueEditorDialog extends BorderPane
{
    private FadeCue model;
    private boolean isNewCue = false;
    private boolean isSuccess = false;

    @FXML private CommonCuePropertyEditor commonEditor;
    @FXML private ComboBox<String> fadeType;
    @FXML private TimePicker fadeDuration;
    @FXML private Slider fadeValue;
    @FXML private ComboBox<IFadeableCue> fadeTarget;

    public FadeCueEditorDialog(CueNumber number)
    {
        this(new FadeCue(number));
        isNewCue = true;
    }

    public FadeCueEditorDialog(FadeCue c)
    {
        FXMLLoader loader = Utils.load(this, "ui/dialogs/editors/FadeCueEditorDialog.fxml");
        model = c;
        commonEditor.setModel(model);

        fadeType.getSelectionModel().selectedItemProperty().addListener((prop, oldValue, newValue) -> fadeValue.setDisable(fadeType.getSelectionModel().getSelectedIndex() == 0));
        fadeType.getSelectionModel().select(model.getFadeType().ordinal());

        fadeDuration.setTime(model.getDuration());
        fadeValue.setValue(model.getTargetValue());

        for(ICue cue : Soundclip.Instance().getActiveCueListView().get().getModel())
        {
            if(!(cue instanceof IFadeableCue)) continue;
            fadeTarget.getItems().add((IFadeableCue)cue);
        }
    }

    public void present()
    {
        Stage dialog = new Stage();
        dialog.setResizable(false);
        dialog.setTitle((isNewCue ? "Add" : "Edit") + " Fade Cue " + model.getNumber() + " - " + model.getName());
        dialog.setScene(new Scene(this, 640, 500));
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }

    private boolean validate()
    {
        fadeTarget.getStyleClass().remove("has-error");
        if(fadeTarget.getSelectionModel().getSelectedItem() == null)
        {
            fadeTarget.getStyleClass().add("has-error");
            return false;
        }

        return true;
    }

    @FXML
    protected void doApply(ActionEvent e)
    {
        if(!validate()) return;

        commonEditor.apply();
        model.setFadeType(FadeCue.FadeType.fromOrdinal(fadeType.getSelectionModel().getSelectedIndex()));
        model.setFadeDuration(fadeDuration.getTime());
        model.setTargetValue(fadeValue.getValue());
        model.setTarget(fadeTarget.getSelectionModel().getSelectedItem());

        isSuccess = true;
        ((Stage)getScene().getWindow()).close();
    }

    @FXML
    private void onCancel(ActionEvent e)
    {
        ((Stage)getScene().getWindow()).close();
    }

    public boolean isSuccess() { return isSuccess; }
    public boolean isNewCue() { return isNewCue; }
    public FadeCue getModel() { return model; }
}
