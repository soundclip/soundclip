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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import soundclip.Utils;

import java.util.regex.Pattern;

/**
 * A field that takes text input in the format MM:ss.SSS
 *
 * Based off of a modified example of code by Alex Michael Raj
 * See http://stackoverflow.com/a/17208419/1200316
 */
public class TimePicker extends TextField
{
    private ObjectProperty<Duration> time = new SimpleObjectProperty<>(Duration.ZERO);
    private final Pattern timePattern = Pattern.compile("\\d\\d:\\d\\d\\.\\d\\d");

    private volatile boolean timeUpdateMutex = false;

    public TimePicker(){
        this(Duration.ZERO);
    }

    public TimePicker(Duration initial){
        super(Utils.durationToString(initial));

        time.addListener((observable, oldValue, newValue) -> {
            if(!timeUpdateMutex){
                timeUpdateMutex = true;

                this.setText(Utils.durationToString(newValue));

                timeUpdateMutex = false;
            }
        });

        textProperty().addListener((observable, oldValue, newValue) -> {
            if(!timeUpdateMutex){
                timeUpdateMutex = true;

                String[] components = newValue.split("[:\\.]");

                setTime(Duration.millis(
                        Double.parseDouble(components[0]) * 60000 +
                                Double.parseDouble(components[1]) * 1000 +
                                Double.parseDouble(components[2])
                                       ));

                timeUpdateMutex = false;
            }
        });
    }

    @Override
    public void appendText(String text){
        // Ignored, always 9 characters
    }

    @Override
    public boolean deleteNextChar() {
        boolean success = false ;

        // If there's a selection, delete it:
        final IndexRange selection = getSelection();
        if (selection.getLength()>0) {
            int selectionEnd = selection.getEnd();
            this.deleteText(selection);
            this.positionCaret(selectionEnd);
            success = true;
        } else {
            // If the caret precedes a digit, replace that digit with a zero and move the caret forward.
            // Else just move the caret forward.
            int caret = this.getCaretPosition();
            if (caret % 3 != 2) { // not preceding a separator
                String currentText = this.getText();
                setText(currentText.substring(0, caret) + "0" + currentText.substring(caret+1));
                success = true;
            }
            this.positionCaret(Math.min(caret + 1, this.getText().length()));
        }
        return success;
    }

    @Override
    public boolean deletePreviousChar() {
        boolean success = false;
        // If there's a selection, delete it:
        final IndexRange selection = getSelection();
        if (selection.getLength()>0) {
            int selectionStart = selection.getStart();
            this.deleteText(selection);
            this.positionCaret(selectionStart);
            success = true;
        } else {
            // If the caret is after a digit, replace that digit with a zero and move the caret backward.
            // Else just move the caret back.
            int caret = this.getCaretPosition();
            if (caret % 3 != 0) { // not following a separator
                String currentText = this.getText();
                setText(currentText.substring(0, caret-1) + "0" + currentText.substring(caret));
                success = true;
            }
            this.positionCaret(Math.max(caret-1, 0));
        }
        return success;
    }

    @Override
    public void deleteText(IndexRange range) {
        this.deleteText(range.getStart(), range.getEnd());
    }

    @Override
    public void deleteText(int begin, int end) {
        // Replace all digits in the given range with zero:
        StringBuilder builder = new StringBuilder(this.getText());
        for (int c = begin; c<end; c++) {
            if (c % 3 != 2) { // Not at a colon:
                builder.replace(c, c+1, "0");
            }
        }
        this.setText(builder.toString());
    }

    @Override
    public void insertText(int index, String text) {
        // Handle an insert by replacing the range from index to index+text.length() with text,
        // if that results in a valid string:
        StringBuilder builder = new StringBuilder(this.getText());
        builder.replace(index, index+text.length(), text);
        final String testText = builder.toString();
        if (validate(testText)) {
            this.setText(testText);
        }
        this.positionCaret(index + text.length());
    }

    @Override
    public void replaceSelection(String replacement) {
        final IndexRange selection = this.getSelection();
        if (selection.getLength()==0) {
            this.insertText(selection.getStart(), replacement);
        } else {
            this.replaceText(selection.getStart(), selection.getEnd(), replacement);
        }
    }

    @Override
    public void replaceText(IndexRange range, String text) {
        this.replaceText(range.getStart(), range.getEnd(), text);
    }

    @Override
    public void replaceText(int begin, int end, String text) {
        if (begin==end) {
            this.insertText(begin, text);
        } else {
            // only handle this if text.length() is equal to the number of characters being replaced,
            // and if the replacement results in a valid string:
            if (text.length() == end - begin) {
                StringBuilder builder = new StringBuilder(this.getText());
                builder.replace(begin, end, text);
                String testText = builder.toString();
                if (validate(testText)) {
                    this.setText(testText);
                }
                this.positionCaret(end);
            }
        }
    }

    private boolean validate(String time) {
        if (! timePattern.matcher(time).matches()) {
            return false ;
        }
        String[] tokens = time.split("[:\\.]");
        assert tokens.length == 3 ;
        try {
            int mins = Integer.parseInt(tokens[0]);
            int secs = Integer.parseInt(tokens[1]);
            int millis = Integer.parseInt(tokens[2]);
            return !(mins < 0 || mins > 59) && !(secs < 0 || secs > 59) && !(millis < 0 || millis > 999);
        } catch (NumberFormatException nfe) {
            // regex matching should assure we never reach this catch block
            assert false ;
            return false ;
        }
    }

    public Duration getTime() {
        return time.get();
    }

    public ObjectProperty<Duration> timeProperty() {
        return time;
    }

    public void setTime(Duration time) {
        this.time.set(time);
    }
}
