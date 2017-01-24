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
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import soundclip.Utils;
import soundclip.controls.LongSpinnerValueFactory;
import soundclip.core.Project;

/**
 * The dialog for editing project properties
 */
public class ProjectPropertiesDialog extends BorderPane
{
    private Project model;

    @FXML private Label statsLabel;
    @FXML private Label projectPathField;
    @FXML private TextField nameField;
    @FXML private Spinner<Long> panicHardStopSpinner;

    public ProjectPropertiesDialog(Project p)
    {
        model = p;
        FXMLLoader loader = Utils.load(this, "ui/dialogs/ProjectPropertiesDialog.fxml");

        statsLabel.setText(model.getCueCount() + " cues in " + model.getCueListCount() + " lists");
        projectPathField.setText(model.getPath());
        nameField.setText(model.getName());

        // Set the value factory and force the editor to commit a value if focus is lost
        // see http://stackoverflow.com/a/32349847/1200316
        LongSpinnerValueFactory factory = new LongSpinnerValueFactory(0, Long.MAX_VALUE, model.getPanicHardStopBefore(), 100);
        panicHardStopSpinner.setValueFactory(factory);
        TextFormatter<Long> formatter = new TextFormatter<>(factory.getConverter(), factory.getValue());
        panicHardStopSpinner.getEditor().setTextFormatter(formatter);
        factory.valueProperty().bindBidirectional(formatter.valueProperty());
    }

    public void present()
    {
        Stage dialog = new Stage();
        dialog.setResizable(false);
        dialog.setTitle("Project Properties");
        dialog.setScene(new Scene(this, 800, 150));
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }

    @FXML
    protected void doCancel(ActionEvent e)
    {
        ((Stage)getScene().getWindow()).close();
    }

    @FXML
    protected void doSave(ActionEvent e)
    {
        nameField.getStyleClass().remove("has-error");
        panicHardStopSpinner.getStyleClass().remove("has-error");

        boolean validationErrors = false;
        if(nameField.getText().isEmpty())
        {
            nameField.getStyleClass().add("has-error");
            validationErrors = true;
        }

        if(panicHardStopSpinner.getValue() < 0)
        {
            panicHardStopSpinner.getStyleClass().add("has-error");
            validationErrors = true;
        }
        if(validationErrors) return;

        model.setName(nameField.getText());
        model.setPanicHardStopBefore(panicHardStopSpinner.getValue());
        ((Stage)getScene().getWindow()).close();
    }
}
