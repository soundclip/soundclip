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

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A dialog for exceptions
 */
public class ExceptionDialog extends Alert
{
    public ExceptionDialog(Throwable ex)
    {
        super(AlertType.ERROR);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        ex.printStackTrace(pw);

        Label label = new Label("Stack Trace:");

        TextArea exception = new TextArea(sw.toString());
        exception.setEditable(false);

        exception.setMaxWidth(Double.MAX_VALUE);
        exception.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(exception, Priority.ALWAYS);
        GridPane.setHgrow(exception, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(exception, 0, 1);

        getDialogPane().setExpandableContent(expContent);
    }
}
