package soundclip.core.cues;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.util.Duration;

/**
 * Provides Pre-Wait, Action, and PostWait progress
 */
public interface IProgressProvider
{
    ReadOnlyObjectProperty<Duration> preWaitProgressProperty();
    Duration getPreWaitProgress();

    ReadOnlyObjectProperty<Duration> progressProperty();
    Duration getProgress();

    ReadOnlyObjectProperty<Duration> postWaitProgressProperty();
    Duration getPostWaitProgress();

    boolean isPerformingAction();
}
