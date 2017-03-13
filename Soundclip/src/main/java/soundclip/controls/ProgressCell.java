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

import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import soundclip.Soundclip;
import soundclip.Utils;
import soundclip.core.cues.ICue;
import soundclip.core.cues.IFadeableCue;

import java.util.function.Function;

/**
 * A table cell for rendering progress
 */
public class ProgressCell extends TableCell<ICue, Duration>
{
    private final Function<ICue, Duration> referenceSelector;
    private final Function<ICue, Boolean> runningSelector;

    private final AnchorPane rootWrapper;
    private final ProgressBar progress;
    private final Text label;

    public ProgressCell(Function<ICue, Duration> referenceSelector, Function<ICue, Boolean> runningSelector)
    {
        super();
        getStylesheets().add("/css/controls/ProgressCell.css");
        getStyleClass().add("progress-cell");

        StackPane root = new StackPane();

        progress = new ProgressBar();
        AnchorPane progressContainer = new AnchorPane();
        AnchorPane.setLeftAnchor(progress, 0.0);
        AnchorPane.setRightAnchor(progress, 0.0);
        AnchorPane.setTopAnchor(progress, -2.0);
        AnchorPane.setBottomAnchor(progress, -2.0);
        progressContainer.getChildren().add(progress);
        root.getChildren().add(progressContainer);

        label = new Text(Utils.durationToString(Duration.ZERO));
        label.getStyleClass().add("progress-label");
        root.getChildren().add(label);

        rootWrapper = new AnchorPane();
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setTopAnchor(root, -2.0);
        AnchorPane.setBottomAnchor(root, -2.0);
        rootWrapper.getChildren().add(root);
        setGraphic(rootWrapper);

        this.referenceSelector = referenceSelector;
        this.runningSelector = runningSelector;

        this.itemProperty().addListener((d) -> {
            try
            {
                ICue model = getTableView().getItems().get(getIndex());
                Duration reference = this.referenceSelector.apply(model);

                if(reference.equals(Duration.UNKNOWN) || reference.equals(Duration.ZERO))
                {
                    label.getStyleClass().add("cell-muted-text");
                }
                else
                {
                    label.getStyleClass().removeAll("cell-muted-text");
                }
            }
            catch(ArrayIndexOutOfBoundsException ignore)
            {

            }

        });
    }

    @Override
    protected void updateItem(Duration item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if(item != null && !empty && getIndex() < getTableView().getItems().size()){
            rootWrapper.setVisible(true);
            ICue model = getTableView().getItems().get(getIndex());
            Duration reference = referenceSelector.apply(model);

            if(runningSelector.apply(model)){
                label.setText(Utils.durationToString(Soundclip.Instance().getGlobalSettings().shouldProgressCellsCountDown() ? reference.subtract(item) : item));
                double percentComplete = item.toMillis() / reference.toMillis();
                if(model instanceof IFadeableCue && ((IFadeableCue)model).isFading())
                {
                    progress.setProgress(percentComplete);
                    progress.setStyle(
                        "-fx-accent: md-orange-800;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: md-orange-A700;"
                    );
                }
                else if(percentComplete > 0.75)
                {
                    progress.setProgress(percentComplete);
                    progress.setStyle(
                        "-fx-accent: md-yellow-800;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: md-yellow-A700;"
                    );

                }
                else
                {
                    progress.setProgress(percentComplete);
                    progress.setStyle(
                        "-fx-accent: md-green-800;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: md-green-A700;"
                    );
                }
            }else{
                label.setText(Utils.durationToString(reference));
                progress.setProgress(0.0);
                progress.setStyle("");
            }
        }
        else
        {
            rootWrapper.setVisible(false);
        }
    }
}
