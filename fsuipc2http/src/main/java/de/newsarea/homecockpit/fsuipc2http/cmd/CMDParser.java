package de.newsarea.homecockpit.fsuipc2http.cmd;

import org.apache.commons.cli.*;

public class CMDParser {

    private static final String CMD_OPTS_HTTPPORT = "http-port";
    private static final String CMD_OPTS_SOCKETPORT = "socket-port";

    private int defaultHttpPort;
    private int defaultSocketPort;

    public CMDParser(int defaultHttpPort, int defaultSocketPort) {
        this.defaultHttpPort = defaultHttpPort;
        this.defaultSocketPort = defaultSocketPort;
    }

    public CMDOptions parse(String[] args) throws ParseException {
        Option httpPortOption = new Option("h", CMD_OPTS_HTTPPORT, true, "HTTP-Port for FSUIPC Connections");
        Option socketPortOption = new Option("s", CMD_OPTS_SOCKETPORT , true, "Socket-Port for FSUIPC Events");
        Options options = new Options().addOption(httpPortOption).addOption(socketPortOption);
        //
        CommandLineParser parser = new PosixParser();
        CommandLine cmdLine = parser.parse(options, args);
        // validation of inputs
        Integer httpPort = tryToParse(cmdLine, CMD_OPTS_HTTPPORT, defaultHttpPort);
        int socketPort = tryToParse(cmdLine, CMD_OPTS_SOCKETPORT, defaultSocketPort);;
        CMDOptions opts = new CMDOptions(httpPort, socketPort);
        return  opts;
    }

    private Integer tryToParse(CommandLine cmdLine, String name, Integer defaultValue) {
        if(cmdLine.hasOption(name)) {
            String httpPortString = cmdLine.getOptionValue(name);
            try {
                return Integer.parseInt(httpPortString);
            } catch(Exception ex) {
                throw new IllegalArgumentException("invalid " + name + " format - " + httpPortString);
            }
        }
        return defaultValue;
    }

}
