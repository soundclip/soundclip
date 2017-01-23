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
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.Soundclip;
import soundclip.Utils;
import soundclip.core.Project;

import java.io.File;
import java.io.IOException;

/**
 * A dialog to create a new project
 */
public class NewProjectDialog extends BorderPane
{
    private static final Logger Log = LogManager.getLogger(NewProjectDialog.class);

    private Project createdProject = null;

    @FXML private TextField projectName;
    @FXML private TextField projectPath;

    private NewProjectDialog()
    {
        FXMLLoader loader = Utils.load(this, "ui/dialogs/NewProjectDialog.fxml");
    }

    public static Project present()
    {
        NewProjectDialog d = new NewProjectDialog();

        Stage dialog = new Stage();
        dialog.setResizable(false);
        dialog.setTitle("New Project");
        dialog.setScene(new Scene(d, 640, 120));
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();

        return d.createdProject;
    }

    @FXML
    protected void onCancel(ActionEvent e)
    {
        ((Stage)getScene().getWindow()).close();
    }

    @FXML
    protected void onCreateProject(ActionEvent e)
    {
        projectName.getStyleClass().remove("has-error");
        projectPath.getStyleClass().remove("has-error");

        boolean validationErrors = false;
        if(projectName.getText().isEmpty())
        {
            projectName.getStyleClass().add("has-error");
            validationErrors = true;
        }
        if(projectPath.getText().isEmpty())
        {
            projectPath.getStyleClass().add("has-error");
            validationErrors = true;
        }

        if(validationErrors) return;

        try
        {
            File p = new File(projectPath.getText());
            if(p.exists())
            {
                Log.warn("Overwriting project at {}", p.getAbsolutePath());
                if(!p.delete())
                {
                    Log.warn("Unable to overwrite {}", p.getAbsolutePath());
                }
            }

            createdProject = new Project(projectPath.getText(), projectName.getText());
            ((Stage)getScene().getWindow()).close();
        }
        catch (IOException ex)
        {
            new ExceptionDialog(ex).show();
        }
    }

    @FXML
    protected void showFileChooser(ActionEvent e)
    {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("SoundClip Projects", "*.scproj")
                                    );
        fc.setTitle("New Project");

        String lastDir = Soundclip.Instance().getGlobalSettings().getLastFileChooserDirectory();
        if(lastDir != null && !lastDir.isEmpty())
        {
            fc.setInitialDirectory(new File(lastDir));
        }

        File result = fc.showSaveDialog(getScene().getWindow());

        if(result != null) projectPath.setText(result.getAbsolutePath());
    }
}
