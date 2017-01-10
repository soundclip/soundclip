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

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.Soundclip;
import soundclip.Utils;
import soundclip.controllers.MainWindow;
import soundclip.core.CueList;
import soundclip.core.CueNumber;
import soundclip.core.cues.IAudioCue;
import soundclip.core.cues.ICue;
import soundclip.core.cues.impl.NoteCue;
import soundclip.dialogs.AudioCueEditorDialog;
import soundclip.dialogs.NoteCueEditorDialog;

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

        preWaitCell.setCellFactory(column -> new TableCell<ICue, Duration>(){
            @Override
            protected void updateItem(Duration item, boolean empty) {
                super.updateItem(item, empty);
                if(item != null && !empty){
                    ICue model = getTableView().getItems().get(getIndex());
                    if(item.greaterThan(Duration.ZERO)){
                        setText(Utils.durationToString(item));

                        double percent = 100 * (item.toMillis() / model.getPreWaitDelay().toMillis());
                        setStyle("-fx-background-color: linear-gradient(" +
                                "from 0% 100% to " + String.format("%.3f", percent) +"% 100%, " +
                                "md-light-green-A700, md-light-green-A700 99.99%, transparent" +
                                ");");
                    }else{
                        setText(Utils.durationToString(model.getPreWaitDelay()));
                        setStyle("");
                    }

                }else{
                    setText("");
                }
            }
        });
        actionCell.setCellFactory(cell -> new TableCell<ICue, Duration>(){
            @Override
            protected void updateItem(Duration item, boolean empty) {
                super.updateItem(item, empty);
                if(item != null && !empty){
                    ICue model = getTableView().getItems().get(getIndex());

                    if(item.greaterThan(Duration.ZERO)){
                        setText(Utils.durationToString(item));

                        double percent = 100 * (item.toMillis() / model.getDuration().toMillis());
                        setStyle("-fx-background-color: linear-gradient(" +
                                "from 0% 100% to " + String.format("%.3f", percent) +"% 100%, " +
                                "md-light-green-A700, md-light-green-A700 99.99%, transparent" +
                                ");");
                    }else{
                        setText(Utils.durationToString(model.getDuration()));
                        setStyle("");
                    }


                    //TODO: Style only if playing
                }else{
                    setText("");
                }
            }
        });
        postWaitCell.setCellFactory(column -> new TableCell<ICue, Duration>(){
            @Override
            protected void updateItem(Duration item, boolean empty) {
                super.updateItem(item, empty);
                if(item != null && !empty){
                    ICue model = getTableView().getItems().get(getIndex());

                    if(item.greaterThan(Duration.ZERO)){
                        setText(Utils.durationToString(item));

                        double percent = 100 * (item.toMillis() / model.getPostWaitDelay().toMillis());
                        setStyle("-fx-background-color: linear-gradient(" +
                                "from 0% 100% to " + String.format("%.3f", percent) +"% 100%, " +
                                "md-light-green-A700, md-light-green-A700 99.99%, transparent" +
                                ");");
                    }else{
                        setText(Utils.durationToString(model.getPostWaitDelay()));
                        setStyle("");
                    }

                    //TODO: Style only if playing
                }else{
                    setText("");
                }
            }
        });

        tableView.setOnKeyReleased((e) -> {
            if(e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER)
            {
                e.consume();
                goNextCue();
            }
            else if(e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.BACK_SPACE)
            {
                e.consume();
                Soundclip.Instance().getCurrentProject().panic();
            }
        });

        tableView.setOnMouseClicked((e) -> {
            // TODO: Disregard if project is locked
            if(e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY)
            {
                ICue c = getSelectedCue();
                if(c instanceof IAudioCue)
                {
                    new AudioCueEditorDialog((IAudioCue)c).present();
                }
                else if(c instanceof NoteCue)
                {
                    new NoteCueEditorDialog((NoteCue)c).present();
                }
                else
                {
                    Log.fatal("Encountered an unknown cue type: {}", c.getClass().getTypeName());
                }
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((prop, oldValue, newValue) -> {
            NotesPane notes = Soundclip.Instance().getController().getNotesPane();

            notes.updateNotes(model.previous(newValue), newValue, model.next(newValue));
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

    public void goNextCue() {
        int i = getSelectedIndex();
        ICue c = getSelectedCue();

        Log.debug("GO {} - {}", c.getNumber(), c.getName());
        c.go();

        if(++i < model.size()) tableView.getSelectionModel().select(i);
    }

    public void focusPrevious() {
        int i = getSelectedIndex() - 1;
        if(i < 0 ) i = 0;
        if(i >= getModel().size() -1 ) i = getModel().size() - 1;

        tableView.getSelectionModel().select(i);
    }

    public void focusNext()
    {
        int i = getSelectedIndex() + 1;
        if(i < 0 ) i = 0;
        if(i >= getModel().size() -1 ) i = getModel().size() - 1;

        tableView.getSelectionModel().select(i);
    }

    public CueNumber getNextCueNumber()
    {
        if(model.size() == 0) return new CueNumber(1);

        ICue selectedCue = getSelectedCue();
        if(selectedCue == null || model.indexOf(selectedCue) == model.size() - 1 ) return new CueNumber(model.last().getNumber().getMajorNumber() + 1);

        return new CueNumber(selectedCue.getNumber(), 5);
    }
}
