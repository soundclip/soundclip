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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import soundclip.Soundclip;
import soundclip.core.CueSupportFlags;
import soundclip.core.cues.ICue;
import soundclip.core.cues.IFadeableCue;

/**
 * The context menu for a cue list
 */
public class CueListContextMenu extends ContextMenu
{
    public CueListContextMenu(TableRow<ICue> row)
    {
        super();

        // HACK: JavaFX doesn't seem to show the menu if it doesn't have any items
        getItems().add(new SeparatorMenuItem());

        setOnShowing((e) -> {
            // We need to resolve the model now because when the context menu was created it may not have one
            ICue model = row.getItem();

            ObservableList<MenuItem> items = getItems();
            items.clear();

            if(model.isPerformingAction())
            {
                if(CueSupportFlags.Supports(model, CueSupportFlags.RESUME))
                {
                    if(model.isPaused())
                    {
                        MenuItem play = new MenuItem("Resume");
                        play.getStyleClass().add("go-cue");
                        play.setGraphic(new ImageView("img/ic_play_arrow_white_18dp_1x.png"));
                        play.setOnAction((a) -> model.go());
                        items.add(play);
                    }
                    else
                    {
                        MenuItem pause = new MenuItem("Pause");
                        pause.setGraphic(new ImageView("img/ic_pause_white_18dp_1x.png"));
                        pause.setOnAction((a) -> model.pause());
                        items.add(pause);
                    }
                }

                MenuItem stopCue = new MenuItem("Stop");
                stopCue.setGraphic(new ImageView("img/ic_stop_white_18dp_1x.png"));
                stopCue.setOnAction((a) -> model.stop());
                items.add(stopCue);

                if(model instanceof IFadeableCue)
                {
                    MenuItem fadeOut = new MenuItem("Fade Out");
                    fadeOut.setGraphic(new ImageView("img/ic_signal_cellular_null_white_18dp_1x.png"));
                    fadeOut.getGraphic().setScaleX(-1);
                    // TODO: Configurable?
                    fadeOut.setOnAction((a) -> ((IFadeableCue)model).fadeOut(Duration.seconds(3)));
                    items.add(fadeOut);
                }
            }
            else if(Soundclip.Instance().isWorkspaceLocked())
            {
                MenuItem play = new MenuItem("Go");
                play.getStyleClass().add("go-cue");
                play.setGraphic(new ImageView("img/ic_play_arrow_white_18dp_1x.png"));
                play.setOnAction((a) -> model.go());
                items.add(play);

                if(model instanceof IFadeableCue)
                {
                    MenuItem fadeIn = new MenuItem("Fade In");
                    fadeIn.setGraphic(new ImageView("img/ic_signal_cellular_null_white_18dp_1x.png"));
                    // TODO: Configurable?
                    fadeIn.setOnAction((a) -> ((IFadeableCue)model).fadeIn(Duration.seconds(3)));
                    items.add(fadeIn);
                }
            }
            else
            {
                MenuItem edit = new MenuItem("Edit Cue");
                edit.setGraphic(new ImageView("img/ic_edit_white_18dp_1x.png"));
                edit.setOnAction((a) -> Soundclip.Instance().getActiveCueListView().ifPresent((view) -> view.showEditorFor(model)));
                items.add(edit);

                MenuItem delete = new MenuItem("Delete Cue");
                delete.setGraphic(new ImageView("img/ic_delete_white_18dp_1x.png"));
                delete.setOnAction((a) -> Soundclip.Instance().getActiveCueList().ifPresent((list) -> list.remove(model)));
                items.add(delete);
            }
        });

        setAutoHide(true);
    }
}
