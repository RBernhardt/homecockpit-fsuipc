package de.newsarea.homecockpit.fsuipc.example;

import de.newsarea.homecockpit.fsuipc.FSUIPCInterface;
import de.newsarea.homecockpit.fsuipc.flightsim.FSUIPCFlightSimInterface;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.ConnectException;

public class Application {

    public static void main(String[] args) throws InterruptedException, ConnectException {
        FSUIPCInterface fsuipcInterface = FSUIPCFlightSimInterface.getInstance();
        // ~
        Application app = new Application();
        app.run(fsuipcInterface);
    }

    private void run(final FSUIPCInterface fsuipcInterface) throws ConnectException {
        fsuipcInterface.open();
        // ~
        FSUIPCSwingUI fsuipSwingUI = new FSUIPCSwingUI(fsuipcInterface);
        fsuipSwingUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                fsuipcInterface.close();
            }
        });
        fsuipSwingUI.initialize();
        // show frame
        fsuipSwingUI.setVisible(true);
    }

}
