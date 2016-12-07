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
package soundclip.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.Soundclip;
import soundclip.Utils;

/**
 * The controller for the about dialog
 */
public class AboutDialog extends VBox
{
    private static final Logger log = LogManager.getLogger(AboutDialog.class);

    @FXML private Label versionLabel;

    public static void present()
    {
        AboutDialog about = new AboutDialog();

        Stage dialog = new Stage();
        dialog.setResizable(false);
        dialog.setTitle("About soundclip");
        dialog.setScene(new Scene(about, 480, 320));
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }

    private AboutDialog(){
        FXMLLoader fxmlLoader = Utils.load(this, "ui/dialogs/AboutDialog.fxml");

        versionLabel.setText("Version: " + Soundclip.VERSION.toString());
    }

    @FXML
    protected void onProjectLinkClicked(ActionEvent click){
        // For this to work on linux, one of the following must be in the path:
        // {"google-chrome", "firefox", "opera", "konqueror", "mozilla"}
        // This means that on arch, /usr/bin/google-chrome-stable must be symlinked to /usr/bin/google-chrome
        Soundclip.Instance().getHostServices().showDocument("https://github.com/nlowe/soundclip");
    }

    @FXML
    protected void onLicenseLinkClicked(ActionEvent click){
        // For this to work on linux, one of the following must be in the path:
        // {"google-chrome", "firefox", "opera", "konqueror", "mozilla"}
        // This means that on arch, /usr/bin/google-chrome-stable must be symlinked to /usr/bin/google-chrome
        Soundclip.Instance().getHostServices().showDocument("https://www.gnu.org/licenses/gpl-3.0.html");
    }
}
