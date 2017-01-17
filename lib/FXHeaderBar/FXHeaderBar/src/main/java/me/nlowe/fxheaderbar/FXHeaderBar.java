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

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * A GTK+ like HeaderBar for the stage. Best used in the <code>top</code>
 * section of the {@link javafx.scene.layout.BorderPane} with the stage style
 * set to {@link javafx.stage.StageStyle#UNDECORATED}
 */
public class FXHeaderBar extends ToolBar
{
    class Spacer extends Pane
    {
        Spacer()
        {
            HBox.setHgrow(this, Priority.ALWAYS);
        }
    }

    private final GridPane defaultTitlePane = new GridPane();

    private final HBox leftBox = new HBox();
    private final HBox rightBox = new HBox();

    private final Pane titleNodeContainer = new Pane(defaultTitlePane);
    private final ObjectProperty<Node> titleNode = new SimpleObjectProperty<>(defaultTitlePane);
    private final StringProperty title = new SimpleStringProperty("Untitled Window");
    private final StringProperty subtitle = new SimpleStringProperty();
    private final BooleanProperty showWindowControls = new SimpleBooleanProperty(true);
    private final BooleanProperty showMinimizeButton = new SimpleBooleanProperty(true);
    private final BooleanProperty useLightIconsProp = new SimpleBooleanProperty(false);
    private final BooleanProperty useFullScreenInsteadOfMaximize = new SimpleBooleanProperty(true);

    private final Button maximizeButton = new Button();

    private Point2D dragOffset = null;
    private double windowWidthBeforeFullscreen = 0;

    public FXHeaderBar()
    {
        setMinHeight(40);
        setMaxHeight(40);
        setPrefHeight(40);
        getStyleClass().add("header-bar");

        defaultTitlePane.getStyleClass().add("title-pane");

        defaultTitlePane.setHgap(10);
        defaultTitlePane.setVgap(2);
        defaultTitlePane.setAlignment(Pos.CENTER);

        ColumnConstraints centerColumn = new ColumnConstraints();
        centerColumn.setHalignment(HPos.CENTER);
        defaultTitlePane.getColumnConstraints().add(centerColumn);

        Label titleLabel = new Label();
        titleLabel.textProperty().bind(title);
        titleLabel.getStyleClass().add("title");

        subtitle.addListener((prop, oldValue, newVlaue) -> {
            if(defaultTitlePane.getChildren().size() == 1)
            {
                Label subtitleLabel = new Label();
                subtitleLabel.textProperty().bind(subtitle);
                subtitleLabel.getStyleClass().add("subtitle");
                defaultTitlePane.add(subtitleLabel, 0, 1);
            }
        });

        defaultTitlePane.add(titleLabel, 0, 0);

        titleNode.addListener((prop, oldNode, newNode) -> {
            titleNodeContainer.getChildren().clear();
            titleNodeContainer.getChildren().add(newNode);
        });

        setTitleNode(defaultTitlePane);

        Button minimizeButton = new Button();
        minimizeButton.setId("fxheaderbar-windowctl-minimize");
        minimizeButton.getStyleClass().add("window-control");
        minimizeButton.visibleProperty().bind(showMinimizeButton);
        minimizeButton.setOnAction((e) -> {
            Stage w = (Stage)getScene().getWindow();
            w.setIconified(!w.isIconified());
        });

        maximizeButton.setId("fxheaderbar-windowctl-maximize");
        maximizeButton.getStyleClass().addAll("maximize", "window-control");
        maximizeButton.setOnAction((e) -> toggleMaximize());

        Button closeButton = new Button();
        closeButton.setId("fxheaderbar-windowctl-close");
        closeButton.getStyleClass().add("window-control");
        closeButton.setOnAction((e) -> ((Stage)getScene().getWindow()).close());

        HBox windowControls = new HBox();
        windowControls.getStyleClass().add("window-control-container");
        windowControls.visibleProperty().bind(showWindowControls);
        windowControls.getChildren().addAll(
                new Separator(Orientation.VERTICAL),
                minimizeButton,
                maximizeButton,
                closeButton
        );

        getItems().addAll(
                leftBox,
                new Spacer(),
                titleNodeContainer,
                new Spacer(),
                rightBox,
                windowControls
        );

        setOnMousePressed((e) -> {
            Stage w = (Stage)getScene().getWindow();
            dragOffset = new Point2D(w.getX() - e.getScreenX(), w.getY() - e.getScreenY());
        });
        setOnMouseDragged((e) -> {
            Stage w = (Stage)getScene().getWindow();

            if(isTechnicallyMaximized())
            {
                setMaximizedImpl(false);
                dragOffset = new Point2D((windowWidthBeforeFullscreen * dragOffset.getX()) / w.getWidth(), dragOffset.getY());
            }

            w.setX(e.getScreenX() + dragOffset.getX());
            w.setY(e.getScreenY() + dragOffset.getY());
        });
        setOnMouseClicked((e) -> {
            if(e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY)
            {
                toggleMaximize();
            }
        });

        useLightIconsProp.addListener((prop, oldValue, newValue) -> {
            if(!newValue)
            {
                minimizeButton.getStyleClass().remove("white");
                maximizeButton.getStyleClass().remove("white");
                closeButton.getStyleClass().remove("white");
            }
            else
            {
                minimizeButton.getStyleClass().add("white");
                maximizeButton.getStyleClass().add("white");
                closeButton.getStyleClass().add("white");
            }
        });

        getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    private boolean isTechnicallyMaximized()
    {
        Stage w = (Stage)getScene().getWindow();

        if(useFullScreenInsteadOfMaximize.getValue())
        {
            return w.isFullScreen();
        }
        else
        {
            return w.isMaximized();
        }
    }

    private void setMaximizedImpl(boolean value)
    {
        Stage w = (Stage)getScene().getWindow();

        if(useFullScreenInsteadOfMaximize.getValue())
        {
            w.setFullScreen(value);
        }
        else
        {
            w.setMaximized(value);
        }
    }

    private void toggleMaximize()
    {
        Stage w = (Stage)getScene().getWindow();

        if(useFullScreenInsteadOfMaximize.getValue())
        {
            w.setFullScreen(!w.isFullScreen());
        }
        else
        {
            w.setMaximized(!w.isMaximized());
        }
    }

    public FXHeaderBar(String title)
    {
        this();
        setTitle(title);
    }

    public FXHeaderBar(String title, String subtitle)
    {
        this(title);
        setSubtitle(subtitle);
    }

    public FXHeaderBar(Node node)
    {
        this();
        setTitleNode(node);
    }

    /**
     * Synchronize the stage title, min/maximize button icon, and add resize handles
     * to the undecorated stage
     *
     * @param w The stage to sync to
     */
    public void syncToStage(Stage w)
    {
        w.titleProperty().bindBidirectional(title);

        ChangeListener<Boolean> maximizeListener = (prop, oldValue, newValue) -> {
            maximizeButton.getStyleClass().removeAll("maximize", "restore");
            maximizeButton.getStyleClass().add(newValue ? "restore" : "maximize");
            if(newValue) windowWidthBeforeFullscreen = w.getWidth();
        };

        w.fullScreenProperty().addListener(maximizeListener);
        w.maximizedProperty().addListener(maximizeListener);

        w.setFullScreenExitHint("");
        if(w.getStyle() == StageStyle.UNDECORATED)
        {
            UndecoratedStageMouseHandler undecoratedStageMouseHandler = new UndecoratedStageMouseHandler(w, this, 4);
        }
    }

    public Node getTitleNode()
    {
        return titleNode.get();
    }

    public ObjectProperty<Node> titleNodeProperty()
    {
        return titleNode;
    }

    public void setTitleNode(Node titleNode)
    {
        this.titleNode.set(titleNode);
    }

    public String getTitle()
    {
        return title.get();
    }

    public StringProperty titleProperty()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title.set(title);
    }

    public String getSubtitle()
    {
        return subtitle.get();
    }

    public StringProperty subtitleProperty()
    {
        return subtitle;
    }

    public void setSubtitle(String subtitle)
    {
        this.subtitle.set(subtitle);
    }

    public ObservableList<Node> getLeft()
    {
        return leftBox.getChildren();
    }

    public HBox getLeftContainer()
    {
        return leftBox;
    }

    public ObservableList<Node> getRight()
    {
        return rightBox.getChildren();
    }

    public HBox getRightContainer()
    {
        return rightBox;
    }

    public boolean isShowWindowControls()
    {
        return showWindowControls.get();
    }

    public BooleanProperty showWindowControlsProperty()
    {
        return showWindowControls;
    }

    public void setShowWindowControls(boolean showWindowControls)
    {
        this.showWindowControls.set(showWindowControls);
    }

    public boolean isShowMinimizeButton()
    {
        return showMinimizeButton.get();
    }

    public BooleanProperty showMinimizeButtonProperty()
    {
        return showMinimizeButton;
    }

    public void setShowMinimizeButton(boolean showMinimizeButton)
    {
        this.showMinimizeButton.set(showMinimizeButton);
    }

    public boolean shouldUseLightIcons()
    {
        return useLightIconsProp.getValue();
    }

    public BooleanProperty useLightIconsPropProperty()
    {
        return useLightIconsProp;
    }

    public void useLightIcons(boolean bool)
    {
        useLightIconsProp.setValue(bool);
    }

    public void useLightIcons()
    {
        useLightIcons(true);
    }

    public boolean usingFullScreenInsteadOfMaximize()
    {
        return useFullScreenInsteadOfMaximize.get();
    }

    public BooleanProperty useFullScreenInsteadOfMaximizeProperty()
    {
        return useFullScreenInsteadOfMaximize;
    }

    public void shouldUseFullscreenInsteadOfMaximize(boolean v)
    {
        useFullScreenInsteadOfMaximize.setValue(v);
    }
}
