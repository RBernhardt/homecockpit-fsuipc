package de.newsarea.homecockpit.fsuipc2http;

import de.newsarea.homecockpit.fsuipc2http.watchdog.event.ConnectorStateChangedEventListener;

import javax.swing.*;
import java.awt.*;

public class ApplicationWindow extends JFrame {

    private JLabel lStatus;

    public ApplicationWindow() {
        super("FSUIPC 2 HTTP");
        fillContent();
    }

    private void fillContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(new JLabel("Connection: "));
        lStatus = new JLabel(ConnectorStateChangedEventListener.State.CLOSED.toString());
        panel.add(lStatus);
        this.getContentPane().add(panel);
    }

    public void setConnectionStatus(String status) {
        if(lStatus == null) { return; }
        lStatus.setText(status);
    }

    public void showWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setMinimumSize(new Dimension(200, 50));
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }

}
