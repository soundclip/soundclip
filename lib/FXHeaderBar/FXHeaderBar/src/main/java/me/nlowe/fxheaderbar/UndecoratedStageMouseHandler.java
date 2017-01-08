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

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Undecorated stages in JavaFX do not provide resize handles.
 * This class implements the required event listeners to restore
 * this functionality
 * 
 * Based off the implementation outlined by Alexander Berg
 * http://stackoverflow.com/a/24017605/1200316
 */
class UndecoratedStageMouseHandler
{
    private final Stage stage;
    private final Scene scene;
    private final Node clipNode;

    private final int border;

    private volatile boolean dragging = false;
    private volatile Point2D mousePressedPoint = new Point2D(0,0);
    
    UndecoratedStageMouseHandler(Stage stage, Node clipNode, int border)
    {
        this.stage = stage;
        scene = this.stage.getScene();
        this.clipNode = clipNode;

        this.border = border;

        stage.addEventHandler(MouseEvent.MOUSE_MOVED, this::handleMouseMoved);
        stage.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        stage.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
        stage.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        stage.addEventHandler(MouseEvent.MOUSE_EXITED, this::handleMouseExited);
        stage.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, this::handleMouseExited);
        scene.getRoot().getChildrenUnmodifiable().forEach(this::applyToNode);
    }

    private void applyToNode(Node n)
    {
        n.addEventHandler(MouseEvent.MOUSE_MOVED, this::handleMouseMoved);
        n.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        n.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
        n.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        n.addEventHandler(MouseEvent.MOUSE_EXITED, this::handleMouseExited);
        n.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, this::handleMouseExited);
        if(n instanceof Parent) ((Parent)n).getChildrenUnmodifiable().forEach(this::applyToNode);
    }
    
    private void handleMouseMoved(MouseEvent e)
    {
        if(isInClippingNode(e))
        {
            scene.setCursor(Cursor.DEFAULT);
            return;
        }

        if(dragging || shouldConsumeEvent(e)) return;

        double sceneX = e.getSceneX();
        double sceneY = e.getSceneY();
        double sceneWidth = scene.getWidth();
        double sceneHeight = scene.getHeight();

        Cursor cursorEvent;

        if (sceneX < border && sceneY < border) {
            cursorEvent = Cursor.NW_RESIZE;
        } else if (sceneX < border && sceneY > sceneHeight - border) {
            cursorEvent = Cursor.SW_RESIZE;
        } else if (sceneX > sceneWidth - border && sceneY < border) {
            cursorEvent = Cursor.NE_RESIZE;
        } else if (sceneX > sceneWidth - border && sceneY > sceneHeight - border) {
            cursorEvent = Cursor.SE_RESIZE;
        } else if (sceneX < border) {
            cursorEvent = Cursor.W_RESIZE;
        } else if (sceneX > sceneWidth - border) {
            cursorEvent = Cursor.E_RESIZE;
        } else if (sceneY < border) {
            cursorEvent = Cursor.N_RESIZE;
        } else if (sceneY > sceneHeight - border) {
            cursorEvent = Cursor.S_RESIZE;
        } else {
            cursorEvent = Cursor.DEFAULT;
        }
        scene.setCursor(cursorEvent);
    }

    private void handleMouseExited(MouseEvent e)
    {
        if(!dragging && !shouldConsumeEvent(e)) scene.setCursor(Cursor.DEFAULT);
    }

    private void handleMousePressed(MouseEvent e)
    {
        if(shouldConsumeEvent(e)) return;
        mousePressedPoint = new Point2D(stage.getWidth() - e.getX(), stage.getHeight() - e.getY());
        dragging = true;
    }

    private void handleMouseReleased(MouseEvent e)
    {
        if(shouldConsumeEvent(e)) return;
        dragging = false;
    }
    
    private void handleMouseDragged(MouseEvent e)
    {
        if(shouldConsumeEvent(e)) return;
        Cursor cursor = scene.getCursor();
        double mouseEventX = e.getX();
        double mouseEventY = e.getY();

        if (!Cursor.DEFAULT.equals(cursor)) {
            if (!Cursor.W_RESIZE.equals(cursor) && !Cursor.E_RESIZE.equals(cursor)) {
                double minHeight = stage.getMinHeight() > (border*2) ? stage.getMinHeight() : (border*2);
                if (Cursor.NW_RESIZE.equals(cursor) || Cursor.N_RESIZE.equals(cursor) || Cursor.NE_RESIZE.equals(cursor)) {
                    if (stage.getHeight() > minHeight || mouseEventY < 0) {
                        stage.setHeight(stage.getY() - e.getScreenY() + stage.getHeight());
                        stage.setY(e.getScreenY());
                    }
                } else {
                    if (stage.getHeight() > minHeight || mouseEventY + mousePressedPoint.getY() - stage.getHeight() > 0) {
                        stage.setHeight(mouseEventY + mousePressedPoint.getY());
                    }
                }
            }

            if (!Cursor.N_RESIZE.equals(cursor) && !Cursor.S_RESIZE.equals(cursor)) {
                double minWidth = stage.getMinWidth() > (border*2) ? stage.getMinWidth() : (border*2);
                if (Cursor.NW_RESIZE.equals(cursor) || Cursor.W_RESIZE.equals(cursor) || Cursor.SW_RESIZE.equals(cursor)) {
                    if (stage.getWidth() > minWidth || mouseEventX < 0) {
                        stage.setWidth(stage.getX() - e.getScreenX() + stage.getWidth());
                        stage.setX(e.getScreenX());
                    }
                } else {
                    if (stage.getWidth() > minWidth || mouseEventX + mousePressedPoint.getX() - stage.getWidth() > 0) {
                        stage.setWidth(mouseEventX + mousePressedPoint.getX());
                    }
                }
            }
        }
    }

    private boolean isInClippingNode(MouseEvent e)
    {
        Point2D local = clipNode.screenToLocal(e.getScreenX(), e.getScreenY());
        Bounds b = clipNode.getLayoutBounds();
        return local.getX() >= 0 && local.getY() >= 0 && local.getX() <= b.getWidth() && local.getY() <= b.getHeight();
    }

    private boolean shouldConsumeEvent(MouseEvent e)
    {
        return isInClippingNode(e) || stage.isFullScreen();
    }
}
