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
package soundclip.input;

import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import soundclip.Settings;
import soundclip.Soundclip;
import soundclip.controls.CueListView;
import soundclip.core.cues.ICue;
import soundclip.core.cues.IFadeableCue;

import java.io.IOException;

/**
 *
 */
public class KeyManager
{
    private final Soundclip instance;
    private final KeyMap keys;

    public KeyManager(Settings settings, Soundclip instance)
    {
        this.keys = settings.getKeyMap();
        this.instance = instance;
    }

    public void Check(KeyEvent e)
    {
        if(keys.getGoKeys().stream().anyMatch(k -> k.match(e)))
        {
            e.consume();
            instance.getActiveCueListView().ifPresent(CueListView::goNextCue);
        }
        else if(keys.getTogglePauseKeys().stream().anyMatch(k -> k.match(e)))
        {
            e.consume();
            instance.getCurrentProject().toggleTransport();
        }
        else if(keys.getPanicKeys().stream().anyMatch(k -> k.match(e)))
        {
            e.consume();
            instance.getCurrentProject().panic();
        }
        else if(keys.getFadeOutKeys().stream().anyMatch(k -> k.match(e)))
        {
            e.consume();
            instance.getActiveCueListView().ifPresent(c -> {
                ICue cue = c.getSelectedCue();
                if(cue instanceof IFadeableCue)
                {
                    // TODO: Make setting for duration
                    ((IFadeableCue)cue).fadeOut(Duration.seconds(3));
                }
            });
        }
        else if(keys.getFocusNextCue().stream().anyMatch(k -> k.match(e)))
        {
            e.consume();
            instance.getActiveCueListView().ifPresent(CueListView::focusNext);
        }
        else if(keys.getFocusPreviousCue().stream().anyMatch(k -> k.match(e)))
        {
            e.consume();
            instance.getActiveCueListView().ifPresent(CueListView::focusPrevious);
        }
        else if(keys.getFocusNextList().stream().anyMatch(k -> k.match(e)))
        {
            e.consume();
            instance.getController().focusNextList();
        }
        else if(keys.getFocusPreviousList().stream().anyMatch(k -> k.match(e)))
        {
            e.consume();
            instance.getController().focusPreviousList();
        }
        else if(keys.getSaveProject().stream().anyMatch(k -> k.match(e)))
        {
            e.consume();
            try
            {
                instance.getCurrentProject().save();
                instance.getGlobalSettings().save();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        else if(keys.getLockWorkspace().stream().anyMatch(k -> k.match(e)))
        {
            e.consume();
            instance.setWorkspaceLocked(true);
        }
        else if(keys.getUnlockWorkspace().stream().anyMatch(k -> k.match(e)))
        {
            e.consume();
            instance.setWorkspaceLocked(false);
        }
    }

}
