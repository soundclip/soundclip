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

import javafx.scene.control.TableCell;
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

    public ProgressCell(Function<ICue, Duration> referenceSelector, Function<ICue, Boolean> runningSelector)
    {
        super();
        getStyleClass().add("progress-cell");

        this.referenceSelector = referenceSelector;
        this.runningSelector = runningSelector;

        this.itemProperty().addListener((d) -> {
            try
            {
                ICue model = getTableView().getItems().get(getIndex());
                Duration reference = this.referenceSelector.apply(model);

                if(reference.equals(Duration.UNKNOWN) || reference.equals(Duration.ZERO))
                {
                    getStyleClass().add("cell-muted-text");
                }
                else
                {
                    getStyleClass().removeAll("cell-muted-text");
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
            ICue model = getTableView().getItems().get(getIndex());
            Duration reference = referenceSelector.apply(model);

            if(runningSelector.apply(model)){
                setText(Utils.durationToString(Soundclip.Instance().getGlobalSettings().shouldProgressCellsCountDown() ? reference.subtract(item) : item));
                double percentComplete = 100 * (item.toMillis() / reference.toMillis());
                if(model instanceof IFadeableCue && ((IFadeableCue)model).isFading())
                {
                    setProgress(percentComplete, "md-orange-800", "md-orange-A700");
                }
                else if(percentComplete > 75.0)
                {
                    setProgress(percentComplete, "md-yellow-800", "md-yellow-A700");
                }
                else
                {
                    setProgress(percentComplete, "md-green-800", "md-green-A700");
                }
            }else{
                setText(Utils.durationToString(reference));
                setStyle("");
            }
        }
    }

    private void setProgress(double percent, String color, String accent)
    {
        setStyle(
            "-fx-background-color: linear-gradient(" +
                 "from 0% 100% to " + String.format("%.3f", percent) +"% 100%, " +
                 color + ", " + color + " 99.99%, transparent" +
            ");" +
            "-fx-padding: -2px -2px 0px -2px;" +
            "-fx-border-color: " + accent + ";" +
            "-fx-border-width: 2px;"
        );
    }
}
