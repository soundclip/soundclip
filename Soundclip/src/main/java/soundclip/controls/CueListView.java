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

import com.google.common.io.Files;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.Soundclip;
import soundclip.Utils;
import soundclip.core.CueList;
import soundclip.core.CueNumber;
import soundclip.core.ProgressType;
import soundclip.core.cues.IAudioCue;
import soundclip.core.cues.ICue;
import soundclip.core.cues.IFadeableCue;
import soundclip.core.cues.IProgressProvider;
import soundclip.core.cues.impl.FXAudioCue;
import soundclip.core.cues.impl.FadeCue;
import soundclip.core.cues.impl.NoteCue;
import soundclip.dialogs.editors.AudioCueEditorDialog;
import soundclip.dialogs.editors.FadeCueEditorDialog;
import soundclip.dialogs.editors.NoteCueEditorDialog;

import java.io.File;
import java.util.Arrays;

/**
 * A view for {@link soundclip.core.CueList}
 */
public class CueListView extends Tab
{
    private static final Logger Log = LogManager.getLogger(CueListView.class);

    private final CueList model;

    @FXML private TableView<ICue> tableView;

    @FXML private TableColumn<ICue, Double> numberCell;
    @FXML private TableColumn<ICue, String> nameCell;

    @FXML private TableColumn<ICue, Duration> preWaitCell;
    @FXML private TableColumn<ICue, Duration> actionCell;
    @FXML private TableColumn<ICue, Duration> postWaitCell;

    public CueListView(CueList model)
    {
        this.model = model;

        FXMLLoader loader = Utils.load(this, "ui/controls/CueListView.fxml");

        // TODO: Fix resizing. The table still shows that the user can resize the columns, but javafx won't let them
        // TODO: (as designed). However, this breaks the columns resizing when the window resizes and looks hideous.

        setText(model.getName());
        model.onNameChanged.whenTriggered(this::setText);

        tableView.setItems(model.getCues());

        preWaitCell.setCellFactory(cell -> new ProgressCell(ICue::getPreWaitDelay, ICue::isInPreWait));
        actionCell.setCellFactory(cell -> new ProgressCell(ICue::getDuration, ICue::isPerformingAction));
        postWaitCell.setCellFactory(cell -> new ProgressCell(ICue::getPostWaitDelay, ICue::isInPostWait));

        tableView.setRowFactory(view -> {
            final TableRow<ICue> row = new TableRow<>();

            row.contextMenuProperty().bind(
                Bindings.when(row.emptyProperty())
                        .then((ContextMenu)null)
                        .otherwise(new CueListContextMenu(row))
            );

            return row;
        });

        tableView.setOnMouseClicked((e) -> {
            ICue c = getSelectedCue();
            if(c == null) return;

            if(e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY && !Soundclip.Instance().isWorkspaceLocked())
            {
                e.consume();
                showEditorFor(c);
                Soundclip.Instance().getController().getNotesPane().updateNotes(model.previous(c), c, model.next(c));
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((prop, oldValue, newValue) -> {
            NotesPane notes = Soundclip.Instance().getController().getNotesPane();

            notes.updateNotes(model.previous(newValue), newValue, model.next(newValue));
        });

        tableView.setOnDragOver((e) -> {
            if(e.getDragboard().hasFiles() && !Soundclip.Instance().isWorkspaceLocked())
            {
                e.acceptTransferModes(TransferMode.COPY);
            }
            else
            {
                e.consume();
            }
        });

        tableView.setOnDragDropped((e) -> {
            boolean success = false;

            if(e.getDragboard().hasFiles() && !Soundclip.Instance().isWorkspaceLocked())
            {
                success = true;
                String[] extensions = new FXAudioCue(new CueNumber(1)).getSupportedExtensions();

                for(File f : e.getDragboard().getFiles())
                {
                    String fileExtension = Files.getFileExtension(f.getAbsolutePath());
                    if(Arrays.stream(extensions).anyMatch(fileExtension::equals))
                    {
                        IAudioCue cue = new FXAudioCue(getNextCueNumber());
                        cue.consumeProjectPath(Soundclip.Instance().getCurrentProject().getProjectLocation());
                        cue.setSource(f.getAbsolutePath());
                        cue.setName(f.getName());

                        Log.info("Adding dropped cue {}", cue);
                        model.add(cue);
                    }
                    else
                    {
                        Log.warn("Could not import '{}' (No audio backends support this file)", f.getAbsolutePath());
                    }
                }
            }

            e.setDropCompleted(success);
            e.consume();
        });
    }

    public CueList getModel()
    {
        return model;
    }

    public ICue getSelectedCue()
    {
         return tableView.getSelectionModel().getSelectedItem();
    }

    public int getSelectedIndex() { return tableView.getSelectionModel().getSelectedIndex(); }

    public void showEditorFor(ICue c)
    {
        if(c instanceof IAudioCue)
        {
            new AudioCueEditorDialog((IAudioCue)c).present();
        }
        else if(c instanceof NoteCue)
        {
            new NoteCueEditorDialog((NoteCue)c).present();
        }
        else if(c instanceof FadeCue)
        {
            new FadeCueEditorDialog((FadeCue)c).present();
        }
        else
        {
            Log.fatal("Encountered an unknown cue type: {}", c.getClass().getTypeName());
        }
    }

    public void goNextCue() {
        if(!Soundclip.Instance().isWorkspaceLocked() || Soundclip.Instance().getCurrentProject().isTransportPaused()) return;

        int i = getSelectedIndex();
        ICue c = getSelectedCue();

        Log.debug("GO {}", c);
        c.go();

        boolean triggerNext = c.getProgressType() == ProgressType.TRIGGER;

        if(++i < model.size() && c.getProgressType() != ProgressType.HOLD)
        {
            tableView.getSelectionModel().select(i);
            if(triggerNext)
            {
                goNextCue();
            }
        }
    }

    public void focusPrevious() {
        int i = getSelectedIndex() - 1;
        if(i < 0 ) i = 0;
        if(i >= getModel().size() -1 ) i = getModel().size() - 1;

        tableView.getSelectionModel().select(i);
        tableView.scrollTo(i);
    }

    public void focusNext()
    {
        int i = getSelectedIndex() + 1;
        if(i < 0 ) i = 0;
        if(i >= getModel().size() -1 ) i = getModel().size() - 1;

        tableView.getSelectionModel().select(i);
        tableView.scrollTo(i);
    }

    public CueNumber getNextCueNumber()
    {
        if(model.size() == 0) return new CueNumber(1);

        ICue selectedCue = getSelectedCue();
        if(selectedCue == null || model.indexOf(selectedCue) == model.size() - 1 ) return new CueNumber(model.last().getNumber().getMajorNumber() + 1);

        return new CueNumber(selectedCue.getNumber(), 5);
    }

    public void focusList()
    {
        tableView.requestFocus();
    }
}
