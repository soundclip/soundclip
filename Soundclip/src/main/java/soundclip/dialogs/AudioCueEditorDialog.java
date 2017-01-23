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
package soundclip.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.Soundclip;
import soundclip.Utils;
import soundclip.controls.CommonCuePropertyEditor;
import soundclip.controls.TimePicker;
import soundclip.core.CueNumber;
import soundclip.core.cues.IAudioCue;
import soundclip.core.cues.impl.FXAudioCue;

import java.io.File;
import java.util.Objects;


/**
 * The editor for cues implementing {@link soundclip.core.cues.IAudioCue}
 */
public class AudioCueEditorDialog extends BorderPane
{
    private static final Logger Log = LogManager.getLogger(AudioCueEditorDialog.class);

    private final IAudioCue model;
    private boolean isNewCue = false;
    private boolean isSuccess = false;

    private Stage dialog;

    @FXML private CommonCuePropertyEditor commonEditor;

    @FXML private TextField audioFilePath;
    @FXML private Slider panSlider, pitchSlider;
    @FXML private TimePicker startOffset, endOffset, fadeInOver, fadeOutOver;

    public AudioCueEditorDialog(CueNumber nextNumber)
    {
        this(new FXAudioCue(nextNumber));
        model.consumeProjectPath(Soundclip.Instance().getCurrentProject().getProjectLocation());
        isNewCue = true;
    }

    public AudioCueEditorDialog(IAudioCue cue)
    {
        FXMLLoader fxmlLoader = Utils.load(this, "ui/dialogs/AudioCueEditorDialog.fxml");

        model = cue;
        commonEditor.setModel(model);

        audioFilePath.setText(model.getSource());

        panSlider.setValue(model.getPan());
        pitchSlider.setValue(model.getPitch());

        fadeInOver.setTime(model.getFadeInDuration());
        fadeOutOver.setTime(model.getFadeOutDuration());
    }

    public void present()
    {
        dialog = new Stage();
        dialog.setResizable(false);
        dialog.setTitle((isNewCue ? "Add" : "Edit") + " Audio Cue " + model.getNumber() + " - " + model.getName());
        dialog.setScene(new Scene(this, 640, 500));
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }

    @FXML
    private void showFileChooser(ActionEvent e)
    {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aif", "*.aiff"),
            new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.m4a", "*.m4v")
        );
        fc.setTitle("Select Audio File");

        String lastDir = Soundclip.Instance().getGlobalSettings().getLastFileChooserDirectory();
        if(lastDir != null && !lastDir.isEmpty())
        {
            fc.setInitialDirectory(new File(lastDir));
        }

        File result = fc.showOpenDialog(dialog);
        if(result != null)
        {
            audioFilePath.setText(result.getAbsolutePath());
        }
    }

    private boolean validate()
    {
        boolean success = true;

        if(audioFilePath.getText().isEmpty())
        {
            success = false;
            audioFilePath.getStyleClass().add("has-error");
        }

        return success;
    }

    @FXML
    private void doApply(ActionEvent e)
    {
        if(!validate()) return;

        isSuccess = true;

        commonEditor.apply();
        if(!audioFilePath.getText().equals(model.getSource())) model.setSource(audioFilePath.getText());
        model.setPan(panSlider.getValue());
        model.setPitch(pitchSlider.getValue());

        // TODO: Start and end offsets

        model.setFadeInDuration(fadeInOver.getTime());
        model.setFadeOutDuration(fadeOutOver.getTime());

        dialog.close();
    }

    @FXML
    private void onCancel(ActionEvent e)
    {
        dialog.close();
    }

    public boolean isSuccess() { return isSuccess; }
    public boolean isNewCue() { return isNewCue; }
    public IAudioCue getModel() { return model; }
}
