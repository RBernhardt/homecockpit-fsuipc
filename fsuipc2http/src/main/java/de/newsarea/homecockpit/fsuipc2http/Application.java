package de.newsarea.homecockpit.fsuipc2http;

import de.newsarea.homecockpit.fsuipc.flightsim.FSUIPCFlightSimInterface;
import de.newsarea.homecockpit.fsuipc2http.controller.FSUIPCController;
import de.newsarea.homecockpit.fsuipc2http.netty.OutputSocketServer;
import de.newsarea.homecockpit.fsuipc2http.watchdog.ConnectorWatchdog;
import de.newsarea.homecockpit.fsuipc2http.watchdog.FSUIPCWatchdogHandler;
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

    public static void main(String[] args) throws Exception {
        Application app = new Application();
        app.start();
    }

    public void start() throws Exception {
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
        //
        connectorWatchdog = new ConnectorWatchdog();
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
            httpServer = GrizzlyHttpServerFactory.createHttpServer(getBaseURI(), create(), false);
            httpServer.start();
            // ~
            outputSocketServer = new OutputSocketServer(fsuipcFlightSimInterface, 8081);
            outputSocketServer.open();
            // ~
            System.in.read();
            log.info("server running at " + getBaseURI());
        } finally {
            shutdown();
        }
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://0.0.0.0/").port(8080).build();
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
