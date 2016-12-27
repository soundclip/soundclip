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

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.Soundclip;
import soundclip.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import soundclip.core.Project;
import soundclip.dialogs.AboutDialog;

import java.time.Instant;
import java.util.Date;

/**
 * The top menu bar for the main window
 */
public class MenuBar extends ToolBar
{
    private static Logger Log = LogManager.getLogger(MenuBar.class);

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private MenuButton addItem;
    @FXML private ToggleButton lockWorkspace;

    private Date lastPanicTime = Date.from(Instant.EPOCH);

    public MenuBar(){
        FXMLLoader fxmlLoader = Utils.load(this, "ui/controls/MenuBar.fxml");

        Project p = Soundclip.Instance().getCurrentProject();

        titleLabel.setText(p.getName());
        p.onRenamed.whenTriggered((name) -> titleLabel.setText(name));

        subtitleLabel.setText(p.getPath() == null ? "Not Saved" : p.getPath());
        p.onPathSet.whenTriggered((path) -> subtitleLabel.setText(path));

        MenuItem addCueList = new MenuItem("Cue List");
        addCueList.setId("soundclip.core.cuelist");
        addItem.getItems().add(addCueList);
    }

    @FXML
    protected void doOpenProject(ActionEvent event)
    {
        Project p = Soundclip.Instance().getCurrentProject();

        if(p.getPath() == null || p.isDirty())
        {
            Log.debug("TODO: Save currently open project");
        }

        Log.debug("TODO: Show file chooser for open project");
    }

    @FXML
    protected void doSaveProject(ActionEvent event)
    {
        Project p = Soundclip.Instance().getCurrentProject();

        if(p.getPath() == null)
        {
            Log.debug("TODO: Show save dialog");
        }

        Log.debug("TODO: Save project");
    }

    @FXML
    protected void doNewProject(ActionEvent event)
    {
        Project p = Soundclip.Instance().getCurrentProject();

        if(p.getPath() == null || p.isDirty())
        {
            Log.debug("TODO: Save currently open project");
        }

        Log.debug("TODO: Show file chooser for new project");
    }

    @FXML
    protected void doPanic(ActionEvent event)
    {
        Project p = Soundclip.Instance().getCurrentProject();
        p.panic(new Date().getTime() - lastPanicTime.getTime() < p.getPanicHardStopBefore());
    }

    @FXML
    protected void toggleWorkspaceLock(ActionEvent event)
    {
        boolean setLock = lockWorkspace.isSelected();

        Log.debug("TODO: Set project lock to {}", setLock);
    }

    @FXML
    protected void onAbout(ActionEvent event)
    {
        AboutDialog.present();
    }
}
