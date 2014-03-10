package de.newsarea.homecockpit.fsuipc2http;

import de.newsarea.homecockpit.fsuipc2http.log4j.JPanelSyncAppender;
import de.newsarea.homecockpit.fsuipc2http.watchdog.event.ConnectorStateChangedEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.Inet4Address;
import java.net.URL;
import java.net.UnknownHostException;

public class ApplicationWindow extends JFrame {

    private static final Logger log = LoggerFactory.getLogger(ApplicationWindow.class);

    private JLabel txtStatus;

    public ApplicationWindow() {
        super("FSUIPC 2 HTTP");
        fillContent();
    }

    private void fillContent() {
        this.getContentPane().add(createMainPanel());
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(800, 600));
        panel.setLayout(new BorderLayout());
        panel.add(createInfoPanel(), BorderLayout.NORTH);
        panel.add(createLogPanel(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(5, 10, 5, 0));
        panel.add(createNetworkPanel());
        panel.add(createStatusPanel());
        return panel;
    }

    private JPanel createNetworkPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 1));
        JLabel lTitle = new JLabel("Server / IP: ");
        lTitle.setPreferredSize(new Dimension(90, 25));
        panel.add(lTitle);
        JLabel txtValue = new JLabel();
        txtValue.setText(determineHostAddress());
        panel.add(txtValue);
        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 1));
        JLabel lTitle = new JLabel("Connection: ");
        lTitle.setPreferredSize(new Dimension(90, 25));
        panel.add(lTitle);
        txtStatus = new JLabel();
        txtStatus.setText(ConnectorStateChangedEventListener.State.CLOSED.toString());
        panel.add(txtStatus);
        return panel;
    }

    private JScrollPane createLogPanel() {
        return JPanelSyncAppender.getJLogPanel();
    }

    public void setConnectionStatus(final String status) {
        if(txtStatus == null) { return; }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txtStatus.setText(status);
            }
        });
    }

    public void showWindow() throws AWTException {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

    private void showTrayIcon() throws AWTException {
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
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    toggleVisibility();
                }
            }
        });
        // ~
        final SystemTray tray = SystemTray.getSystemTray();
        tray.add(trayIcon);
    }

    private String determineHostAddress() {
        try {
            return Inet4Address.getLocalHost().toString();
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
        }
        return "NOT AVAIABLE";
    }

    private void toggleVisibility() {
        setState(isVisible() ? Frame.ICONIFIED : Frame.NORMAL);
        setVisible(!isVisible());
        toFront();
    }

}
