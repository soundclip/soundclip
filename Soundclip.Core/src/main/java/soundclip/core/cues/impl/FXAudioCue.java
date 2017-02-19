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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.core.CueNumber;
import soundclip.core.CueSupportFlags;
import soundclip.core.cues.IAudioCue;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * An audio cue backed by the JavaFX MediaPlayer implementation
 *
 * Supports any audio formats supported by the implementation of
 * {@link javafx.scene.media.MediaPlayer} on your system.
 */
public class FXAudioCue extends CueBase implements IAudioCue, AutoCloseable
{
    private static final Logger Log = LogManager.getLogger(FXAudioCue.class);

    private double pitch;
    private double pan;

    private String source;
    private Media backendSource;
    private MediaPlayer backend;
    private SimpleObjectProperty<Duration> progressPropertyWrapper = new SimpleObjectProperty<>(Duration.UNKNOWN);

    private Timeline fadeTimeline;
    private String projectPath = null;

    public FXAudioCue(CueNumber number)
    {
        super();

        setNumber(number);
    }

    @Override
    public Duration getDuration()
    {
        return backendSource == null || backend == null ? Duration.ZERO : backend.getTotalDuration();
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
        if(fadeTimeline != null && fadeTimeline.getStatus() == Animation.Status.PAUSED) fadeTimeline.play();
    }

    @Override
    public void pause()
    {
        if(backend != null) backend.pause();
        if(fadeTimeline != null) fadeTimeline.pause();
    }

    @Override
    public void resume()
    {
        if(backend != null) backend.play();
    }

    @Override
    public void stop()
    {
        if(backend != null)
        {
            backend.seek(backend.getStartTime());
            backend.stop();
            backend.setVolume(1.0);
        }
        if(fadeTimeline != null)
        {
            fadeTimeline.stop();
        }
    }

    @Override
    public void load(JsonNode cue)
    {
        // Cue Number is set by the cue list deserializer
        deserializeCommonFields(cue);

        pan = cue.get("pan").asDouble();
        pitch = cue.get("pitch").asDouble();

        setSource(cue.get("source").asText());
    }

    @Override
    public void serialize(JsonGenerator w) throws IOException
    {
        w.writeStartObject();
        {
            // include the type so the cue list deserializer can load the right cue
            w.writeStringField("type", getClass().getCanonicalName());
            serializeCommonFields(w);

            w.writeNumberField("pan", pan);
            w.writeNumberField("pitch", pitch);

            w.writeStringField("source", source);
        }
        w.writeEndObject();
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
        return progressPropertyWrapper;
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
    public boolean isPerformingAction()
    {
        return backend != null && (backend.getStatus() == MediaPlayer.Status.PLAYING || backend.getStatus() == MediaPlayer.Status.PAUSED);
    }

    @Override
    public boolean isPaused()
    {
        return backend != null && (backend.getStatus() == MediaPlayer.Status.PAUSED);
    }

    @Override
    public void fadeIn(Duration duration)
    {
        if(fadeTimeline != null)
        {
            fadeTimeline.stop();
        }

        backend.setVolume(0.0);

        // TODO: Fade to set volume
        fadeTimeline = new Timeline(
                new KeyFrame(
                        duration,
                        new KeyValue(backend.volumeProperty(), 1.0)
                )
        );

        backend.play();
        fadeTimeline.play();
    }

    @Override
    public void fadeTo(double value, Duration duration)
    {
        if(fadeTimeline != null)
        {
            fadeTimeline.stop();
        }

        // TODO: Fade to set volume
        fadeTimeline = new Timeline(
                new KeyFrame(
                        duration,
                        new KeyValue(backend.volumeProperty(), value)
                )
        );

        if(!isPerformingAction()) backend.play();
        fadeTimeline.play();
    }

    @Override
    public void fadeOut(Duration duration)
    {
        if(fadeTimeline != null)
        {
            fadeTimeline.stop();
        }

        fadeTimeline = new Timeline(
                new KeyFrame(
                        duration,
                        new KeyValue(backend.volumeProperty(), 0.0)
                )
        );
        fadeTimeline.setOnFinished(e ->
        {
            backend.stop();
            backend.seek(backend.getStartTime());

            // TODO: Restore volume to configured volume
            backend.setVolume(1.0);
        });

        fadeTimeline.play();
    }

    @Override
    public boolean isFading()
    {
        return fadeTimeline != null && fadeTimeline.getStatus() == Animation.Status.RUNNING;
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
    @Override
    public String getSource()
    {
        return source;
    }

    /** Set the audio file path */
    @Override
    public void setSource(String source)
    {
        if(source == null || source.isEmpty()) throw new IllegalArgumentException("Source cannot be null or empty");
        if(projectPath == null) throw new IllegalStateException("The project path has not been propagated to this cue");

        if(!(new File(projectPath, source).exists()))
        {
            // probably not a relative path already, try to relativize it
            source = new File(projectPath).toURI().relativize(new File(source).toURI()).getPath();
        }

        if(!(new File(projectPath, source).exists())) throw new IllegalArgumentException("The file at " + source + " does not exist");

        this.source = source;

        try
        {
            close();
        }
        catch (Exception e)
        {
            Log.warn("Failed to clean up previous backend", e);
        }

        backendSource = new Media(new File(projectPath, source).toURI().toString());
        backend = new MediaPlayer(backendSource);

        backend.setOnReady(() -> {
            progressPropertyWrapper.bind(backend.currentTimeProperty());
        });
        backend.setOnEndOfMedia(() -> {
            backend.stop();
            backend.seek(backend.getStartTime());
        });
    }

    @Override
    public String[] getSupportedExtensions()
    {
        return new String[]{"wav", "mp3", "aif", "aiff", "mp4", "m4a", "m4v"};
    }

    @Override
    public void close() throws Exception
    {
        if(backend != null)
        {
            progressPropertyWrapper.unbind();
            backend.stop();
            backend.dispose();
        }
        backend = null;
        backendSource = null;
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

    @Override
    public void consumeProjectPath(String path)
    {
        projectPath = path;
    }
}
