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

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import me.nlowe.fxheaderbar.FXHeaderBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.Soundclip;
import soundclip.Utils;
import soundclip.core.Project;
import soundclip.dialogs.*;
import soundclip.dialogs.editors.AudioCueEditorDialog;
import soundclip.dialogs.editors.FadeCueEditorDialog;
import soundclip.dialogs.editors.NoteCueEditorDialog;

import java.io.File;
import java.io.IOException;
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
    @FXML private MenuItem renumberCueListItem;
    @FXML private MenuItem renameCueListItem;
    @FXML private ToggleButton lockWorkspace;
    @FXML private Button pause;
    @FXML private Button panic;

    private Date lastPanicTime = Date.from(Instant.EPOCH);

    private Timeline panicIndicator;
    private Timeline pauseIndicator;

    public MenuBar(){
        FXMLLoader fxmlLoader = Utils.load(this, "ui/controls/MenuBar.fxml");

        init(Soundclip.Instance().getCurrentProject());
        Soundclip.Instance().onProjectChanged.whenTriggered(this::init);

        initAddItems(addItem.getItems());
        useLightIcons();

        lockWorkspace.selectedProperty().bindBidirectional(Soundclip.Instance().workspaceLockedProperty());

        addItem.disableProperty().bind(Soundclip.Instance().workspaceLockedProperty());
        renumberCueListItem.disableProperty().bind(Soundclip.Instance().workspaceLockedProperty());
        renameCueListItem.disableProperty().bind(Soundclip.Instance().workspaceLockedProperty());
    }

    private void init(Project p)
    {
        setTitle(p.getName());
        p.onRenamed.whenTriggered(this::setTitle);

        setSubtitle(p.getPath() == null ? "Not Saved" : p.getPath());
        p.onPathSet.whenTriggered(this::setSubtitle);

        p.onPanic.whenTriggered(e -> indicatePanic(e.getKey(), e.getValue()));

        p.pauseTransportPropertyProperty().addListener((prop, oldValue, newValue) -> {
            if(pauseIndicator != null)
            {
                pauseIndicator.stop();
                pause.getStyleClass().removeAll("outline");
            }
            if(newValue)
            {
                pause.getStyleClass().add("outline");
                pauseIndicator = new Timeline(
                        new KeyFrame(Duration.seconds(0.5), (e) -> {
                            pause.getStyleClass().remove("outline");
                        }),
                        new KeyFrame(Duration.seconds(1), (e) -> {
                            pause.getStyleClass().add("outline");
                        })
                );
                pauseIndicator.setCycleCount(Timeline.INDEFINITE);
                pauseIndicator.play();
            }
        });
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

        MenuItem addFadeCue = new MenuItem("Fade Cue");
        addFadeCue.setOnAction(this::doAddFadeCue);
        addFadeCue.getStyleClass().add("add-fade-cue");
        i.add(addFadeCue);

        i.add(new SeparatorMenuItem());

        MenuItem addCueList = new MenuItem("Cue List");
        addCueList.setOnAction(this::doAddCueList);
        addCueList.getStyleClass().add("add-cue-list");
        i.add(addCueList);
    }

    private void indicatePanic(Duration d, boolean hard)
    {
        if(panicIndicator != null)
        {
            panicIndicator.stop();
            panic.getStyleClass().remove("outline");
        }
        if(!hard)
        {
            panic.getStyleClass().add("outline");
            panicIndicator = new Timeline(
                    new KeyFrame(Duration.seconds(0.25), (e) -> {
                        panic.getStyleClass().remove("outline");
                    }),
                    new KeyFrame(Duration.seconds(0.5), (e) -> {
                        panic.getStyleClass().add("outline");
                    })
            );
            panicIndicator.setOnFinished((e) -> panic.getStyleClass().remove("outline"));
            panicIndicator.setCycleCount((int) (d.toSeconds() * 2));
            panicIndicator.play();
        }
    }

    @FXML
    protected void doOpenProject(ActionEvent event)
    {
        Project p = Soundclip.Instance().getCurrentProject();

        if(p.getPath() == null || p.isDirty())
        {
            Log.debug("TODO: save currently open project");
        }

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
                Soundclip.Instance().getGlobalSettings().setLastFileChooserDirectory(result.getParent());
                Soundclip.Instance().setCurrentProject(new Project(result.getAbsolutePath()));
            }
            catch (IOException e)
            {
                Log.error("Unable to load project: ", e);
                ExceptionDialog d = new ExceptionDialog(e);
                d.setTitle("Exception");
                d.setHeaderText("Unable to Open Project");
                d.setContentText("SoundClip was unable to open the project because an exception was thrown");
            }
        }
    }

    @FXML
    protected void doSaveProject(ActionEvent event)
    {
        Project p = Soundclip.Instance().getCurrentProject();
        try
        {
            p.save();
        }
        catch (IOException e)
        {
            Log.error("Unable to save project: {}", e);
            ExceptionDialog d = new ExceptionDialog(e);
            d.setTitle("Exception");
            d.setHeaderText("Unable to save project");
            d.setContentText("SoundClip was unable to save the project because an exception was thrown.");
        }
    }

    @FXML
    protected void doNewProject(ActionEvent event)
    {
        Project p = NewProjectDialog.present();

        if(p != null) Soundclip.Instance().setCurrentProject(p);
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

    private void doAddFadeCue(ActionEvent actionEvent)
    {
        Optional<CueListView> view = Soundclip.Instance().getActiveCueListView();
        if(!view.isPresent())
        {
            Log.warn("No cue list selected. Someone messed up");
            return;
        }

        CueListView selectedCueList = view.get();
        FadeCueEditorDialog editor = new FadeCueEditorDialog(selectedCueList.getNextCueNumber());
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
    protected void doPause(ActionEvent event)
    {
        Soundclip.Instance().getCurrentProject().toggleTransport();
    }

    @FXML
    protected void doPanic(ActionEvent event)
    {
        Project p = Soundclip.Instance().getCurrentProject();
        p.panic(new Date().getTime() - lastPanicTime.getTime() < p.getPanicHardStopBefore());
    }

    @FXML
    protected void onRenumberCueList(ActionEvent event)
    {
        Log.debug("TODO: Renumber cue list-");
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
    protected void onProjectProperties()
    {
        new ProjectPropertiesDialog(Soundclip.Instance().getCurrentProject()).present();
    }

    @FXML
    protected void onCloseProject(ActionEvent event)
    {
        Platform.runLater(() -> {
            Soundclip.Instance().returnToWelcomeScreen();
        });
    }

    @FXML
    protected void onAbout(ActionEvent event)
    {
        AboutDialog.present();
    }
}
