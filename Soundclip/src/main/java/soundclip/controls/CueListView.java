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
import javafx.util.Duration;
import soundclip.Utils;
import soundclip.core.CueList;
import soundclip.core.cues.ICue;

/**
 * A view for {@link soundclip.core.CueList}
 */
public class CueListView extends Tab
{
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

        // TODO: Force ordering by cue number
        model.onCueAdded.whenTriggered((cue) -> tableView.getItems().add(cue));
        for(ICue cue : model)
        {
            tableView.getItems().add(cue);
        }

        model.onCueRemoved.whenTriggered((cue) -> tableView.getItems().remove(cue));

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
                                "-progress-green, -progress-green 99.99%, transparent" +
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
                                "-progress-green, -progress-green 99.99%, transparent" +
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
                                "-progress-green, -progress-green 99.99%, transparent" +
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

    }

    public CueList getModel()
    {
        return model;
    }

    public ICue getSelectedCue()
    {
         return (ICue) tableView.getSelectionModel().getSelectedItem();
    }
}
