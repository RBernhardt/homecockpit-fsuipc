package de.newsarea.homecockpit.fsuipc2http.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

public class JPanelSyncAppender extends AppenderSkeleton {

    public static JLogPanel jLogPanel;

    public static JLogPanel getJLogPanel() {
        if(jLogPanel == null) {
            jLogPanel = new JLogPanel();
        }
        return jLogPanel;
    }

    protected void append(LoggingEvent event) {
        if (performChecks()) {
            String logOutput = this.layout.format(event);
            getJLogPanel().appendLog(logOutput);
            if (layout.ignoresThrowable()) {
                String[] lines = event.getThrowableStrRep();
                if (lines != null) {
                    int len = lines.length;
                    for (int i = 0; i < len; i++) {
                        getJLogPanel().appendLog(lines[i]);
                        getJLogPanel().appendLog(Layout.LINE_SEP);
                    }
                }
            }
        }
    }

    public void close() {
        jLogPanel = null;
    }

    public boolean requiresLayout() {
        return true;
    }

    private boolean performChecks() {
        return !closed && layout != null;
    }

}
