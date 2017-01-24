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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.controllers.MainWindow;
import soundclip.controls.CueListView;
import soundclip.core.CueList;
import soundclip.core.Project;
import soundclip.core.interop.Signal;
import soundclip.dialogs.WelcomeWindow;
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

    public final Signal<Project> onProjectChanged = new Signal<>();
    private final Settings globalSettings = new Settings();
    private Project currentProject;
    private OSCServer oscServer;

    private BooleanProperty workspaceLocked = new SimpleBooleanProperty(false);

    public static void main(String[] args)
    {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        singleton = this;
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("soundclip");
        primaryStage.getIcons().addAll(
            new Image(getClass().getClassLoader().getResourceAsStream("img/icon/icon_512x512.png")),
            new Image(getClass().getClassLoader().getResourceAsStream("img/icon/icon_256x256.png")),
            new Image(getClass().getClassLoader().getResourceAsStream("img/icon/icon_128x128.png")),
            new Image(getClass().getClassLoader().getResourceAsStream("img/icon/icon_64x64.png")),
            new Image(getClass().getClassLoader().getResourceAsStream("img/icon/icon_48x48.png")),
            new Image(getClass().getClassLoader().getResourceAsStream("img/icon/icon_32x32.png"))
        );

        log.info("Starting Up " + VERSION.toString());

        Project initialProject = null;

        if(globalSettings.getLastOpenProjectPath().isEmpty())
        {
            initialProject = new WelcomeWindow().present();
        }
        else
        {
            initialProject = new Project(globalSettings.getLastOpenProjectPath());
        }

        if(initialProject == null)
        {
            primaryStage.close();
        }
        else
        {
            setCurrentProject(initialProject);
            primaryController = new MainWindow();
            primaryStage.setScene(new Scene(primaryController, 800, 600));
            primaryController.getMenuBar().syncToStage(primaryStage);

            primaryStage.show();
        }
    }

    public void returnToWelcomeScreen()
    {
        getGlobalSettings().setLastOpenProjectPath("");
        primaryController.getScene().getWindow().hide();

        closeCurrentProject();
        Project p = new WelcomeWindow().present();

        if(p != null)
        {
            setCurrentProject(p);
            ((Stage)primaryController.getScene().getWindow()).show();
        }
        else
        {
            ((Stage)primaryController.getScene().getWindow()).close();
        }
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

    private void closeCurrentProject()
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
            oscServer = null;
        }

        if(currentProject != null)
        {
            try
            {
                currentProject.close();
            }
            catch (Exception e)
            {
                log.fatal("Unable to close project", e);
            }

            currentProject = null;
        }
    }

    public void setCurrentProject(Project currentProject)
    {
        closeCurrentProject();

        this.currentProject = currentProject;
        if(this.currentProject.getPath() != null && !this.currentProject.getPath().isEmpty())
        {
            globalSettings.setLastOpenProjectPath(this.currentProject.getPath());
            globalSettings.addRecentProject(this.currentProject);
        }

        try
        {
            oscServer = new OSCServer(this.currentProject);
            oscServer.listen();
        }
        catch (IOException e)
        {
            log.fatal("Unable to start OSC Server", e);
        }

        onProjectChanged.post(this.currentProject);
    }

    public Settings getGlobalSettings()
    {
        return globalSettings;
    }

    public boolean isWorkspaceLocked()
    {
        return workspaceLocked.getValue();
    }

    public BooleanProperty workspaceLockedProperty()
    {
        return workspaceLocked;
    }

    public void setWorkspaceLocked(boolean workspaceLocked)
    {
        this.workspaceLocked.setValue(workspaceLocked);
    }
}
