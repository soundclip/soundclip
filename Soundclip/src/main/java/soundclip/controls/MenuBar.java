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

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import me.nlowe.fxheaderbar.FXHeaderBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.Soundclip;
import soundclip.Utils;
import soundclip.core.CueNumber;
import soundclip.core.Project;
import soundclip.dialogs.AboutDialog;
import soundclip.dialogs.AudioCueEditorDialog;
import soundclip.dialogs.NoteCueEditorDialog;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * The top menu bar for the main window
 */
public class MenuBar extends FXHeaderBar
{
    private static Logger Log = LogManager.getLogger(MenuBar.class);

    @FXML private MenuButton addItem;
    @FXML private ToggleButton lockWorkspace;

    private Date lastPanicTime = Date.from(Instant.EPOCH);

    public MenuBar(){
        FXMLLoader fxmlLoader = Utils.load(this, "ui/controls/MenuBar.fxml");

        Project p = Soundclip.Instance().getCurrentProject();

        setTitle(p.getName());
        p.onRenamed.whenTriggered(this::setTitle);

        setSubtitle(p.getPath() == null ? "Not Saved" : p.getPath());
        p.onPathSet.whenTriggered(this::setSubtitle);

        initAddItems(addItem.getItems());
        useLightIcons();
    }

    private void initAddItems(ObservableList<MenuItem> i)
    {
        MenuItem addAudioCue = new MenuItem("Audio Cue");
        addAudioCue.setOnAction(this::doAddAudioCue);
        addAudioCue.getStyleClass().add("add-audio-cue");
        i.add(addAudioCue);

        MenuItem addNoteCue = new MenuItem("Note Cue");
        addNoteCue.setOnAction(this::doAddNoteCue);
        addNoteCue.getStyleClass().add("add-note-cue");
        i.add(addNoteCue);

        i.add(new SeparatorMenuItem());

        MenuItem addCueList = new MenuItem("Cue List");
        addCueList.setOnAction(this::doAddCueList);
        addCueList.getStyleClass().add("add-cue-list");
        i.add(addCueList);
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

    private void doAddAudioCue(ActionEvent event)
    {
        Optional<CueListView> view = Soundclip.Instance().getActiveCueListView();
        if(!view.isPresent())
        {
            Log.warn("No cue list selected. Someone messed up");
            return;
        }

        CueListView selectedCueList = view.get();

        AudioCueEditorDialog editor = new AudioCueEditorDialog(selectedCueList.getNextCueNumber());
        editor.present();

        if(editor.isSuccess())
        {
            selectedCueList.getModel().add(editor.getModel());
        }
    }

    private void doAddNoteCue(ActionEvent event)
    {
        Optional<CueListView> view = Soundclip.Instance().getActiveCueListView();
        if(!view.isPresent())
        {
            Log.warn("No cue list selected. Someone messed up");
            return;
        }

        CueListView selectedCueList = view.get();
        NoteCueEditorDialog editor = new NoteCueEditorDialog(selectedCueList.getNextCueNumber());
        editor.present();

        if(editor.isSuccess())
        {
            selectedCueList.getModel().add(editor.getModel());
        }
    }

    private void doAddCueList(ActionEvent event)
    {
        TextInputDialog dialog = new TextInputDialog("New Cue List");
        dialog.getDialogPane().getStylesheets().add("/css/theme.css");
        dialog.setGraphic(new ImageView(this.getClass().getResource("/img/ic_queue_music_white_18dp_1x.png").toString()));
        dialog.setTitle("Add Cue List");
        dialog.setHeaderText("Append Cue List to Project");
        dialog.setContentText("Cue List Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> Soundclip.Instance().getCurrentProject().appendCueList(name));
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
    protected void onRenameCueList(ActionEvent event)
    {
        TextInputDialog dialog = new TextInputDialog("New Name");
        dialog.getDialogPane().getStylesheets().add("/css/theme.css");
        dialog.setHeaderText(null);
        dialog.getDialogPane().setHeader(null);
        dialog.setGraphic(new ImageView(this.getClass().getResource("/img/ic_queue_music_white_48dp_2x.png").toString()));
        dialog.setTitle("Rename Cue List");
        dialog.setContentText("Cue List Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> Soundclip.Instance().getActiveCueList().ifPresent(c -> c.setName(name)));
    }

    @FXML
    protected void onAbout(ActionEvent event)
    {
        AboutDialog.present();
    }
}
