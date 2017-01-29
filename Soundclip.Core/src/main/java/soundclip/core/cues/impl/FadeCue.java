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
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soundclip.core.CueNumber;
import soundclip.core.CueSupportFlags;
import soundclip.core.IPostLoadHook;
import soundclip.core.Project;
import soundclip.core.cues.ICue;
import soundclip.core.cues.IFadeableCue;

import java.io.IOException;
import java.util.UUID;

/**
 * Fade a cue to the specified level and optionally stop it
 */
public class FadeCue extends CueBase implements IPostLoadHook
{
    private static final Logger Log = LogManager.getLogger(FadeCue.class);

    /** The type of fade to perform */
    public enum FadeType
    {
        /** Fade out */
        OUT,

        /** Fade to a specific volume */
        VALUE;

        public static FadeType fromOrdinal(int ord)
        {
            switch(ord)
            {
                case 0: return OUT;
                case 1: return VALUE;
                default: throw new IndexOutOfBoundsException();
            }
        }
    }

    private FadeType fadeType;
    private Duration fadeDuration;
    private double targetValue;
    private Timeline fadeTimeline;

    private UUID targetGUID;
    private IFadeableCue target;

    public FadeCue(CueNumber number)
    {
        super();

        setNumber(number);
        target = null;
        targetValue = 0.0d;
        fadeType = FadeType.OUT;
        fadeTimeline = new Timeline();
        fadeTimeline.setCycleCount(1);
        setFadeDuration(Duration.seconds(3));
    }

    private void onTimelineComplete(ActionEvent actionEvent)
    {
        Log.debug("Fade timeline complete");
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
        return fadeTimeline.currentTimeProperty();
    }

    @Override
    public Duration getProgress()
    {
        return fadeTimeline.getCurrentTime();
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
        return fadeTimeline.getStatus() == Animation.Status.RUNNING;
    }

    @Override
    public Duration getDuration()
    {
        return fadeDuration;
    }

    public void setFadeDuration(Duration d)
    {
        fadeDuration = d;
        fadeTimeline.getKeyFrames().clear();
        fadeTimeline.getKeyFrames().add(new KeyFrame(d, this::onTimelineComplete));
    }

    @Override
    public int getSupportedOperations()
    {
        return CueSupportFlags.RESUME;
    }

    @Override
    public void go()
    {
        if(target != null && target.isPerformingAction())
        {
            fadeTimeline.playFromStart();
            if(fadeType == FadeType.OUT)
            {
                target.fadeOut(fadeDuration);
            }
            else
            {
                target.fadeTo(targetValue, fadeDuration);
            }
        }

    }

    @Override
    public void pause()
    {
        fadeTimeline.pause();
    }

    @Override
    public void resume()
    {
        fadeTimeline.play();
    }

    @Override
    public void stop()
    {
        fadeTimeline.stop();
    }

    @Override
    public void load(JsonNode cue)
    {
        deserializeCommonFields(cue);
        setFadeType(FadeType.valueOf(cue.get("fadeType").asText().toUpperCase()));
        setFadeDuration(Duration.millis(cue.get("duration").asDouble()));
        setTargetValue(cue.get("value").asDouble());

        String guid = cue.get("target").asText("");
        targetGUID = guid.isEmpty() ? null : UUID.fromString(guid);
    }

    @Override
    public void onProjectLoaded(Project p)
    {
        if(targetGUID == null)
        {
            Log.warn("No target set for cue {}", this);
        }

        ICue c = p.resolveCue(targetGUID);
        if(!(c instanceof IFadeableCue))
        {
            throw new IllegalArgumentException("The target of this cue does not implement IFadeableCue");
        }

        target = ((IFadeableCue)c);
    }

    @Override
    public void serialize(JsonGenerator w) throws IOException
    {
        w.writeStartObject();
        {
            // include the type so the cue list deserializer can load the right cue
            w.writeStringField("type", getClass().getCanonicalName());
            serializeCommonFields(w);

            w.writeStringField("fadeType", fadeType.toString());
            w.writeNumberField("duration", fadeDuration.toMillis());
            w.writeNumberField("value", targetValue);
            w.writeStringField("target", target == null ? "" : target.getGUID().toString());
        }
        w.writeEndObject();
    }

    public FadeType getFadeType()
    {
        return fadeType;
    }

    public void setFadeType(FadeType fadeType)
    {
        this.fadeType = fadeType;
    }

    public double getTargetValue()
    {
        return targetValue;
    }

    public void setTargetValue(double targetValue)
    {
        if(fadeTimeline.getStatus() == Animation.Status.RUNNING)
        {
            Log.warn("Target Value changed while the timeline was running. The value will not be updated until the timeline is stopped");
        }
        this.targetValue = targetValue;
    }

    public IFadeableCue getTarget()
    {
        return target;
    }

    public void setTarget(IFadeableCue target)
    {
        if(fadeTimeline.getStatus() == Animation.Status.RUNNING)
        {
            throw new IllegalStateException("Can't change the target while the cue is running!");
        }

        this.target = target;
        this.targetGUID = target.getGUID();
    }
}
