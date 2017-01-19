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
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.Utils;
import soundclip.core.interop.Signal;

/**
 * The view for a recent project
 */
public class RecentProjectView extends BorderPane
{
    private static final Logger Log = LogManager.getLogger(RecentProjectView.class);

    @FXML private Label projectName;
    @FXML private Label projectPath;
    @FXML private BorderPane removeIcon;

    public final Signal<Boolean> onRemoveClicked = new Signal<>();

    public RecentProjectView(String projectName, String projectPath)
    {
        FXMLLoader loader = Utils.load(this, "ui/controls/RecentProjectView.fxml");

        this.projectName.setText(projectName);
        this.projectPath.setText(projectPath);

        removeIcon.setOnMouseClicked((e) -> {
            e.consume();
            onRemoveClicked.post(true);
        });

        removeIcon.managedProperty().bind(removeIcon.visibleProperty());

        this.setOnMouseEntered((e) -> removeIcon.setVisible(true));
        this.setOnMouseExited((e) -> removeIcon.setVisible(false));
    }
}
