import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ExecutorListener implements PropertyChangeListener {
    private final JProgressBar pbar;
    private final HomeScreen homeScreen;

    public ExecutorListener(JProgressBar pbar, HomeScreen homeScreen) {
        this.pbar = pbar;
        this.homeScreen = homeScreen;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if ("progress".equals(propertyName)) {
            pbar.setValue((Integer) evt.getNewValue());
        }

        if ("state".equals(propertyName) && evt.getNewValue() == SwingWorker.StateValue.DONE) {
            //TODO: Add som dynamics, like button opacity, completed text, etc
            System.out.println("DONE");
            homeScreen.inDownloadProgress(false);
        }
    }
}
