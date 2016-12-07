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
package soundclip.core.cues.impl;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import soundclip.core.CueNumber;
import soundclip.core.CueSupportFlags;
import soundclip.core.cues.IAudioCue;

import java.util.LinkedList;
import java.util.List;

/**
 * An audio cue backed by the JavaFX MediaPlayer implementation
 *
 * Supports any audio formats supported by the implementation of
 * {@link javafx.scene.media.MediaPlayer} on your system.
 */
public class FXAudioCue implements IAudioCue, AutoCloseable
{
    private CueNumber number;
    private String name;
    private String notes;
    private Duration fadeInDuration;
    private Duration fadeOutDuration;
    private double pitch;
    private double pan;

    private String source;
    private Media backendSource;
    private MediaPlayer backend;

    private Timeline fadeTimeline;
    private Duration preWaitDelay;
    private Duration postWaitDelay;

    public FXAudioCue(CueNumber number)
    {
        this.number = number;
        name = "Untitled Audio Cue";
        fadeInDuration = Duration.ZERO;
        fadeOutDuration = Duration.ZERO;
        preWaitDelay = Duration.ZERO;
        postWaitDelay = Duration.ZERO;
    }

    @Override
    public CueNumber getNumber()
    {
        return number;
    }

    @Override
    public void setNumber(CueNumber number)
    {
        this.number = number;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getNotes()
    {
        return notes;
    }

    @Override
    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    @Override
    public Duration getDuration()
    {
        return backendSource == null || backend == null ? Duration.ZERO : backend.getTotalDuration();
    }

    @Override
    public Duration getPreWaitDelay()
    {
        return preWaitDelay;
    }

    @Override
    public void setPreWaitDelay(Duration delay)
    {
        preWaitDelay = delay;
    }

    @Override
    public Duration getPostWaitDelay()
    {
        return postWaitDelay;
    }

    @Override
    public void setPostWaitDelay(Duration delay)
    {
        postWaitDelay = delay;
    }

    @Override
    public Duration getFadeInDuration()
    {
        return fadeInDuration;
    }

    @Override
    public void setFadeInDuration(Duration duration)
    {
        fadeInDuration = duration;
    }

    @Override
    public Duration getFadeOutDuration()
    {
        return fadeOutDuration;
    }

    @Override
    public void setFadeOutDuration(Duration duration)
    {
        fadeOutDuration = duration;
    }

    @Override
    public int getSupportedOperations()
    {
        return CueSupportFlags.FADE | CueSupportFlags.RESUME | CueSupportFlags.SEEK | CueSupportFlags.AUDIO_PITCH | CueSupportFlags.AUDIO_PAN;
    }

    @Override
    public List<String> getValidationErrors()
    {
        List<String> errors = new LinkedList<>();

        if(source == null || source.isEmpty()) errors.add("No audio file specified");
        if(backendSource == null || backend == null) errors.add("Unable to initialize audio backend");
        if(backendSource != null)
        {
            MediaException ex = backendSource.getError();
            if(ex != null)
            {
                errors.add(ex.getMessage());
            }
        }

        return errors;
    }

    @Override
    public void go()
    {
        if(backend != null) backend.play();
    }

    @Override
    public void pause()
    {
        if(backend != null) backend.pause();
    }

    @Override
    public void resume()
    {
        if(backend != null) backend.play();
    }

    @Override
    public void stop()
    {
        if(backend != null) backend.stop();
    }

    @Override
    public ReadOnlyObjectProperty<Duration> preWaitProgressProperty()
    {
        // TODO: implement pre-wait
        return new SimpleObjectProperty<>(Duration.ZERO);
    }

    @Override
    public Duration getPreWaitProgress()
    {
        return Duration.ZERO;
    }

    @Override
    public ReadOnlyObjectProperty<Duration> progressProperty()
    {
        return backend == null ? new SimpleObjectProperty<>(Duration.UNKNOWN) : backend.currentTimeProperty();
    }

    @Override
    public Duration getProgress()
    {
        return backend == null ? Duration.UNKNOWN : backend.getCurrentTime();
    }

    @Override
    public ReadOnlyObjectProperty<Duration> postWaitProgressProperty()
    {
        //TODO: Implement Post-Wait
        return new SimpleObjectProperty<>(Duration.ZERO);
    }

    @Override
    public Duration getPostWaitProgress()
    {
        return Duration.ZERO;
    }

    @Override
    public void fadeIn()
    {
        if(fadeTimeline != null)
        {
            fadeTimeline.stop();
        }

        backend.setVolume(0.0);

        // TODO: Fade to set volume
        fadeTimeline = new Timeline(
                new KeyFrame(
                        javafx.util.Duration.millis(fadeOutDuration.toMillis()),
                        new KeyValue(backend.volumeProperty(), 1.0)
                )
        );

        backend.play();
        fadeTimeline.play();
    }

    @Override
    public void fadeOut()
    {
        if(fadeTimeline != null)
        {
            fadeTimeline.stop();
        }

        fadeTimeline = new Timeline(
                new KeyFrame(
                        javafx.util.Duration.millis(fadeOutDuration.toMillis()),
                        new KeyValue(backend.volumeProperty(), 0.0)
                )
        );
        fadeTimeline.setOnFinished(e ->
        {
            backend.stop();

            // TODO: Restore volume to configured volume
            backend.setVolume(1.0);
        });

        fadeTimeline.play();
    }

    @Override
    public void seekRelative(Duration offset)
    {
        backend.seek(backend.getCurrentTime().add(offset));
    }

    @Override
    public void seekAbsolute(Duration duration)
    {
        backend.seek(javafx.util.Duration.millis(duration.toMillis()));
    }

    /** @return the path to the audio file this cue will play, null if no file is specified */
    public String getSource()
    {
        return source;
    }

    /** Set the audio file path */
    public void setSource(String source)
    {
        if(source == null || source.isEmpty()) throw new IllegalArgumentException("Source cannot be null or empty");

        this.source = source;

        if(backend != null) backend.stop();
        backend = null;

        backendSource = new Media(source);
        backend = new MediaPlayer(backendSource);
    }

    @Override
    public void close() throws Exception
    {
        if(backend != null) backend.stop();
        backend = null;
    }

    @Override
    public double getPitch()
    {
        return pitch;
    }

    @Override
    public void setPitch(double pitch)
    {
        this.pitch = pitch;
    }

    @Override
    public double getPan()
    {
        return pan;
    }

    @Override
    public void setPan(double pan)
    {
        this.pan = pan < -1.0 ? -1.0 : (pan > 1.0 ? 1.0 : pan);
    }
}
