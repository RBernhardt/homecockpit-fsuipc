package de.newsarea.homecockpit.fsuipc.example;

import de.newsarea.homecockpit.fsuipc.FSUIPCInterface;
import de.newsarea.homecockpit.fsuipc.flightsim.FSUIPCFlightSimInterface;

import java.net.ConnectException;

public class CommandLineRunner {

    public static void main(String[] args) throws InterruptedException, ConnectException {
        CommandLineRunner commandLineRunner = new CommandLineRunner();
        commandLineRunner.run();
    }

    void run() throws InterruptedException, ConnectException {
        FSUIPCInterface fsuipcInterface = FSUIPCFlightSimInterface.getInstance();
        fsuipcInterface.open();
        FSUIPCSwingUI fsuipSwingUI = new FSUIPCSwingUI(fsuipcInterface);
        fsuipSwingUI.initialize();
    }

}
