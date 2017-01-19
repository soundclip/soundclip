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

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.Soundclip;
import soundclip.Utils;
import soundclip.controls.RecentProjectView;
import soundclip.core.Project;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * The Welcome Dialog
 */
public class WelcomeWindow extends BorderPane
{
    private static final Logger Log = LogManager.getLogger(WelcomeWindow.class);

    private final ObservableList<Map.Entry<String, String>> recentProjects;
    @FXML private ListView<Map.Entry<String, String>> recentProjectList;
    @FXML private Label versionLabel;

    @FXML private HBox newProject;
    @FXML private HBox openProject;

    private Project loadedProject = null;

    public WelcomeWindow()
    {
        FXMLLoader fxmlLoader = Utils.load(this, "ui/dialogs/WelcomeWindow.fxml");

        versionLabel.setText(Soundclip.VERSION.toString());
        recentProjects = recentProjectList.getItems();
        recentProjectList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        recentProjectList.setCellFactory((view) -> new ListCell<Map.Entry<String, String>>(){
            @Override
            public void updateItem(Map.Entry<String, String> v, boolean empty)
            {
                super.updateItem(v, empty);
                if(!empty && v != null)
                {
                    RecentProjectView recentProject = new RecentProjectView(v.getValue(), v.getKey());
                    recentProject.onRemoveClicked.whenTriggered((r) -> {
                        int idx = recentProjects.indexOf(recentProjects.filtered((i) -> i.getKey().equals(v.getKey())).get(0));
                        recentProjects.remove(idx);
                        Soundclip.Instance().getGlobalSettings().removeRecentProject(v.getKey());
                    });
                    recentProject.prefWidthProperty().bind(recentProjectList.widthProperty().subtract(18));
                    recentProject.setMaxWidth(Control.USE_PREF_SIZE);
                    setGraphic(recentProject);
                }
            }
        });

        recentProjects.addAll(Soundclip.Instance().getGlobalSettings().getRecentProjects().entrySet());

        recentProjectList.setOnMouseExited((e) -> recentProjectList.getSelectionModel().clearSelection());

        recentProjectList.setOnMouseClicked((e) -> {
            if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2)
            {
                Map.Entry<String, String> item = recentProjectList.getSelectionModel().getSelectedItem();
                try
                {
                    loadedProject = new Project(item.getKey());
                    ((Stage)getScene().getWindow()).close();
                }
                catch(Exception ex)
                {
                    Log.warn("Unable to load project", ex);
                    ExceptionDialog d = new ExceptionDialog(ex);
                    d.setTitle("Exception");
                    d.setHeaderText("Unable to Open Project");
                    d.setContentText("SoundClip was unable to open the project because an exception was thrown");
                    d.show();
                }
            }
        });

        newProject.setOnMouseClicked((e) -> {
            if(e.getButton() == MouseButton.PRIMARY)
            {
                Log.debug("TODO: Create new project");
            }
        });

        openProject.setOnMouseClicked((e) -> {
            if(e.getButton() == MouseButton.PRIMARY)
            {
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("SoundClip Projects", "*.scproj")
                                            );
                fc.setTitle("Open Project");

                String lastDir = Soundclip.Instance().getGlobalSettings().getLastFileChooserDirectory();
                if(lastDir != null && !lastDir.isEmpty())
                {
                    fc.setInitialDirectory(new File(lastDir));
                }

                File result = fc.showOpenDialog(getScene().getWindow());
                if(result != null)
                {
                    try
                    {
                        loadedProject = new Project(result.getAbsolutePath());
                        ((Stage)getScene().getWindow()).close();
                    }
                    catch (IOException ex)
                    {
                        Log.error("Unable to load project: ", ex);
                        ExceptionDialog d = new ExceptionDialog(ex);
                        d.setTitle("Exception");
                        d.setHeaderText("Unable to Open Project");
                        d.setContentText("SoundClip was unable to open the project because an exception was thrown");
                        d.show();
                    }
                }
            }
        });
    }

    public Project present()
    {
        Stage dialog = new Stage();
        dialog.setResizable(false);
        dialog.setTitle("Welcome to SoundClip");
        dialog.setScene(new Scene(this, 777, 460));
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();

        return loadedProject;
    }
}
