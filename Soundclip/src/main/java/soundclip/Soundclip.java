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
package soundclip;

import com.github.zafarkhaja.semver.Version;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.controllers.MainWindow;
import soundclip.controls.CueListView;
import soundclip.core.CueList;
import soundclip.core.Project;
import soundclip.osc.OSCServer;

import java.io.IOException;
import java.util.Optional;

/**
 * The main entry point for the application
 */
public class Soundclip extends Application
{
    private static final Logger log = LogManager.getLogger(Soundclip.class);

    public static final Version VERSION = Version.valueOf("0.1.0-alpha");

    private static Soundclip singleton;

    private MainWindow primaryController;

    private Project currentProject;
    private OSCServer oscServer;

    public static void main(String[] args)
    {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        singleton = this;

        log.info("Starting Up " + VERSION.toString());

        // TODO: Load last project
        setCurrentProject(new Project());

        primaryController = new MainWindow();

        primaryStage.setTitle("soundclip");
        primaryStage.getIcons().addAll(
                new Image(getClass().getClassLoader().getResourceAsStream("img/icon/icon_512x512.png")),
                new Image(getClass().getClassLoader().getResourceAsStream("img/icon/icon_256x256.png")),
                new Image(getClass().getClassLoader().getResourceAsStream("img/icon/icon_128x128.png")),
                new Image(getClass().getClassLoader().getResourceAsStream("img/icon/icon_64x64.png")),
                new Image(getClass().getClassLoader().getResourceAsStream("img/icon/icon_48x48.png")),
                new Image(getClass().getClassLoader().getResourceAsStream("img/icon/icon_32x32.png"))
        );
        primaryStage.setScene(new Scene(primaryController, 800, 600));
        primaryStage.show();
    }

    @Override
    public void stop()
    {
        if(oscServer != null)
        {
            try
            {
                oscServer.close();
            }
            catch (Exception e)
            {
                log.fatal("Error shutting down OSC Server", e);
            }
        }
    }

    public static Soundclip Instance() { return singleton; }

    public MainWindow getController()
    {
        return primaryController;
    }

    public Optional<CueListView> getActiveCueListView()
    {
        Optional<CueListView> result = Optional.empty();

        SingleSelectionModel<Tab> model = primaryController.getCueStackContainer().getSelectionModel();
        if(model == null) return result;

        Tab t = model.getSelectedItem();
        if (t == null) return result;

        return Optional.of((CueListView)t);
    }

    public Optional<CueList> getActiveCueList()
    {
        CueListView view = getActiveCueListView().orElse(null);

        return view == null ? Optional.empty() : Optional.of(view.getModel());
    }

    public Project getCurrentProject()
    {
        return currentProject;
    }

    public void setCurrentProject(Project currentProject)
    {
        if(oscServer != null)
        {
            try
            {
                oscServer.close();
            }
            catch (Exception e)
            {
                log.fatal("Error shutting down OSC Server", e);
            }
        }

        this.currentProject = currentProject;

        try
        {
            oscServer = new OSCServer(this.currentProject);
            oscServer.listen();
        }
        catch (IOException e)
        {
            log.fatal("Unable to start OSC Server", e);
        }
    }
}
