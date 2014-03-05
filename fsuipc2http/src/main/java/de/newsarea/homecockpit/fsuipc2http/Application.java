package de.newsarea.homecockpit.fsuipc2http;

import de.newsarea.homecockpit.fsuipc.flightsim.FSUIPCFlightSimInterface;
import de.newsarea.homecockpit.fsuipc2http.cmd.CMDOptions;
import de.newsarea.homecockpit.fsuipc2http.cmd.CMDParser;
import de.newsarea.homecockpit.fsuipc2http.controller.FSUIPCController;
import de.newsarea.homecockpit.fsuipc2http.netty.OutputSocketServer;
import de.newsarea.homecockpit.fsuipc2http.watchdog.ConnectorWatchdog;
import de.newsarea.homecockpit.fsuipc2http.watchdog.FSUIPCWatchdogHandler;
import de.newsarea.homecockpit.fsuipc2http.watchdog.event.ConnectorStateChangedEventListener;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.SystemUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class Application {

    private static final String SPRING_XML_FILES = "spring/fsuipc2http-context.xml";

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private FSUIPCFlightSimInterface fsuipcFlightSimInterface;
    private HttpServer httpServer;
    private OutputSocketServer outputSocketServer;
    private ConnectorWatchdog connectorWatchdog;
    private ApplicationWindow applicationWindow;

    public static void main(String[] args) throws Exception {
        if(!SystemUtils.IS_OS_WINDOWS) {
            System.out.println("[ERROR] fsuipc2http will only run on a Microsoft Windows operating system.");
            return;
        }
        // ~
        CMDParser cmdParser = new CMDParser(8080, 8081);
        CMDOptions cmdOptions = null;
        try {
            cmdOptions = cmdParser.parse(args);
        } catch (ParseException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
            System.out.println("please use 'fsuipc2http -h' for help");
        }
        if(cmdOptions == null) { return; }
        // ~
        Application app = new Application();
        app.start(cmdOptions);
    }

    public void start(CMDOptions cmdOptions) throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    shutdown();
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
        });
        // ~
        applicationWindow = new ApplicationWindow();
        //
        connectorWatchdog = new ConnectorWatchdog();
        connectorWatchdog.addEventListerner(new ConnectorStateChangedEventListener() {
            @Override
            public void stateChanged(State state) {
                applicationWindow.setConnectionStatus(state.toString());
            }
        });
        // ~
        ApplicationContext appCtx = new ClassPathXmlApplicationContext(SPRING_XML_FILES);
        fsuipcFlightSimInterface = (FSUIPCFlightSimInterface) appCtx.getBean("fsuipcInterface");
        // ~
        try {
            fsuipcFlightSimInterface.open();
            // ~
            connectorWatchdog.monitorConnector("fsuipcFlightSimInterface", new FSUIPCWatchdogHandler(fsuipcFlightSimInterface));
            connectorWatchdog.start();
            // ~
            URI baseURI = getBaseURI(cmdOptions.getHttpPort());
            httpServer = GrizzlyHttpServerFactory.createHttpServer(baseURI, create(), false);
            httpServer.start();
            log.info("server running at " + baseURI);
            // ~
            outputSocketServer = new OutputSocketServer(fsuipcFlightSimInterface, cmdOptions.getSocketPort());
            outputSocketServer.open();
            // ~
            applicationWindow.showWindow();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            shutdown();
        }
    }

    private static URI getBaseURI(Integer httpPort) {
        return UriBuilder.fromUri("http://0.0.0.0/").port(httpPort).build();
    }

    public ResourceConfig create() {
        return new ResourceConfig().register(new FSUIPCController(fsuipcFlightSimInterface));
    }

    public void shutdown() {
        log.info("shutdown ...");
        if(connectorWatchdog != null) {
            connectorWatchdog.stop();
            log.info("# connectorWatchdog stopped");
        }
        if(fsuipcFlightSimInterface != null) {
            fsuipcFlightSimInterface.close();
            log.info("# fsuipcFlightSimInterface stopped");
        }
        if(outputSocketServer != null) {
            outputSocketServer.close();
            log.info("# outputSocketServer stopped");
        }
        if(httpServer != null) {
            httpServer.shutdown();
            log.info("# webserver stopped");
        }
    }

}
