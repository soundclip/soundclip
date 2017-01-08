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
package me.nlowe.fxheaderbar;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * A sample application using the FX Header Bar
 */
public class SampleWindow extends Application
{
    public static void main(String[] args)
    {
        Application.launch(args);
    }

    private FXHeaderBar headerBar = new FXHeaderBar();

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // Make the stage undecorated since the HeaderBar contains window controls
        primaryStage.initStyle(StageStyle.UNDECORATED);

        BorderPane root = new BorderPane();
        root.setTop(headerBar);

        headerBar.setTitle("fxheaderbar Sample Application");
        headerBar.setSubtitle("Example Subtitle");

        primaryStage.setScene(new Scene(root, 640, 480));

        headerBar.syncToStage(primaryStage);
        primaryStage.show();
    }
}
