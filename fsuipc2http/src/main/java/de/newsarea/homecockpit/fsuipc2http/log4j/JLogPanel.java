package de.newsarea.homecockpit.fsuipc2http.log4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class JLogPanel extends JScrollPane {

    private JTextArea textArea;

    public JLogPanel() {
        super(new JTextArea());
        fillContent();
    }

    private void fillContent() {
        JViewport viewport = getViewport();
        textArea = (JTextArea)viewport.getView();
        textArea.setEditable(false);
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        // ~
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    public void appendLog(String log) {
        textArea.append(log);
    }

}
