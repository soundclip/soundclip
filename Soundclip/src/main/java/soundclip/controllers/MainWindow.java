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
package soundclip.controllers;

import javafx.scene.control.Tab;
import soundclip.Soundclip;
import soundclip.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import soundclip.controls.CueListView;
import soundclip.controls.MenuBar;
import soundclip.controls.NotesPane;
import soundclip.core.CueList;
import soundclip.core.Project;

/**
 * The main application window
 */
public class MainWindow extends BorderPane
{
    @FXML private TabPane cueStackContainer;
    @FXML private NotesPane notesPane;
    @FXML private MenuBar menuBar;

    public MainWindow()
    {
        FXMLLoader fxmlLoader = Utils.load(this, "ui/MainWindow.fxml");

        Project project = Soundclip.Instance().getCurrentProject();

        project.onCueListAdded.whenTriggered((list) ->
        {
            cueStackContainer.getTabs().add(new CueListView(list));
            syncHeaderVisibility();
        });

        project.onCueListRemoved.whenTriggered((list) ->
        {
           Tab toRemove = cueStackContainer.getTabs().stream().filter((tab) -> ((CueListView)tab).getModel() == list).findFirst().orElse(null);

           if(toRemove != null)
            {
                cueStackContainer.getTabs().remove(toRemove);
                syncHeaderVisibility();
            }
        });

        for(CueList list : project)
        {
            cueStackContainer.getTabs().add(new CueListView(list));
        }

        syncHeaderVisibility();
    }

    public TabPane getCueStackContainer()
    {
        return cueStackContainer;
    }

    private void syncHeaderVisibility()
    {
        if(cueStackContainer.getTabs().size() > 1)
        {
            cueStackContainer.getStyleClass().remove("hide-tabs");
        }
        else
        {
            cueStackContainer.getStyleClass().add("hide-tabs");
        }
    }

    public MenuBar getMenuBar()
    {
        return menuBar;
    }
}
