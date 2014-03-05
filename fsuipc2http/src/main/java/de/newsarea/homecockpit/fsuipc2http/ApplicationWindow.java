package de.newsarea.homecockpit.fsuipc2http;

import de.newsarea.homecockpit.fsuipc2http.watchdog.event.ConnectorStateChangedEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class ApplicationWindow extends JFrame {

    private static final Logger log = LoggerFactory.getLogger(ApplicationWindow.class);

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

    public void showWindow() throws AWTException {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setMinimumSize(new Dimension(200, 50));
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(false);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowIconified(WindowEvent e) {
                setVisible(false);
            }
        });
        showTrayIcon();
    }

    public void showTrayIcon() throws AWTException {
        URL imageURL = ApplicationWindow.class.getResource("/icon/tray.png");
        Image image = Toolkit.getDefaultToolkit().getImage(imageURL);
        final TrayIcon trayIcon = new TrayIcon(image);
        // ~
        final PopupMenu popup = new PopupMenu();
        MenuItem showOrHideItem = new MenuItem("Show / Hide");
        showOrHideItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleVisibility();
            }
        });
        popup.add(showOrHideItem);
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
                System.exit(0);
            }
        });
        popup.add(exitItem);
        trayIcon.setPopupMenu(popup);
        // ~
        final SystemTray tray = SystemTray.getSystemTray();
        tray.add(trayIcon);
    }

    private void toggleVisibility() {
        setState(isVisible() ? Frame.ICONIFIED : Frame.NORMAL);
        setVisible(!isVisible());
    }

}
