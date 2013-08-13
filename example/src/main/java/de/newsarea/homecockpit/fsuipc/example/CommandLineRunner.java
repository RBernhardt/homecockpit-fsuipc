package de.newsarea.homecockpit.fsuipc.example;

import de.newsarea.homecockpit.fsuipc.FSUIPCInterface;
import de.newsarea.homecockpit.fsuipc.flightsim.FSUIPCFlightSimInterface;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.ConnectException;

public class CommandLineRunner {

    public static void main(String[] args) throws InterruptedException, ConnectException {
        CommandLineRunner commandLineRunner = new CommandLineRunner();
        commandLineRunner.run();
    }

    void run() throws ConnectException {
        final FSUIPCInterface fsuipcInterface = FSUIPCFlightSimInterface.getInstance();
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
