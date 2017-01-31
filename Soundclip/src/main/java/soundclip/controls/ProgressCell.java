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

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import soundclip.Utils;
import soundclip.core.cues.ICue;
import soundclip.core.cues.IFadeableCue;

import java.util.UUID;


/**
 * A table cell for rendering progress
 */
public class ProgressCell extends TableCell<ICue, Duration>
{
    private static final int PROGRESS_HEIGHT = 5;

    private final BorderPane root;
    private final Label elapsed;
    private final ProgressBar progress;

    private UUID modelGUID = null;

    public ProgressCell()
    {
        super();
        getStylesheets().add("/css/controls/ProgressCell.css");

        root = new BorderPane();

        elapsed = new Label();
        elapsed.setAlignment(Pos.CENTER);
        elapsed.setTextAlignment(TextAlignment.CENTER);
        root.setCenter(elapsed);

        progress = new ProgressBar();
        progress.setMinHeight(PROGRESS_HEIGHT);
        progress.setPrefHeight(PROGRESS_HEIGHT);
        progress.setMaxHeight(PROGRESS_HEIGHT);
        root.setBottom(progress);
    }

    @Override
    protected void updateItem(Duration item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if(item != null && !empty){
            ICue model = getTableView().getItems().get(getIndex());

            if(model.isPerformingAction()){
                elapsed.setText(Utils.durationToString(item));
                double percentComplete = item.toMillis() / model.getDuration().toMillis();
                progress.setProgress(percentComplete);
                if(model instanceof IFadeableCue && ((IFadeableCue)model).isFading())
                {
                    progress.setStyle("-fx-accent: md-orange-A700;");
                }
                else if(percentComplete > 0.75)
                {
                    progress.setStyle("-fx-accent: md-yellow-A700;");
                }
                else
                {
                    progress.setStyle("");
                }
            }else{
                elapsed.setText(Utils.durationToString(model.getDuration()));
                progress.setProgress(0.0d);
            }

            if(modelGUID == null || !modelGUID.equals(model.getGUID()))
            {
                setGraphic(root);
                modelGUID = model.getGUID();
            }
        }else{
            setGraphic(null);
        }
    }
}
