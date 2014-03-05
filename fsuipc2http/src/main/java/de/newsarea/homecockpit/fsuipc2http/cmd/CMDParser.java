package de.newsarea.homecockpit.fsuipc2http.cmd;

import org.apache.commons.cli.*;

public class CMDParser {

    private static final String CMD_OPTS_HELP = "help";
    private static final String CMD_OPTS_HTTPPORT = "http-port";
    private static final String CMD_OPTS_SOCKETPORT = "socket-port";

    private int defaultHttpPort;
    private int defaultSocketPort;

    public CMDParser(int defaultHttpPort, int defaultSocketPort) {
        this.defaultHttpPort = defaultHttpPort;
        this.defaultSocketPort = defaultSocketPort;
    }

    public CMDOptions parse(String[] args) throws ParseException {
        Option helpOption = new Option("h", CMD_OPTS_HELP , false, "help message");
        Option httpPortOption = new Option("p", CMD_OPTS_HTTPPORT, true, "http-port for FSUIPC connections");
        Option socketPortOption = new Option("s", CMD_OPTS_SOCKETPORT , true, "socket-port for FSUIPC events");
        Options options = new Options()
            .addOption(helpOption)
            .addOption(httpPortOption)
            .addOption(socketPortOption);
        //
        CommandLineParser parser = new PosixParser();
        CommandLine cmdLine;
        try {
            cmdLine = parser.parse(options, args);
        } catch (ParseException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
            System.out.println("");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("fsuipc2http [OPTIONS]", options );
            return null;
        }
        // print help
        if(cmdLine.hasOption(CMD_OPTS_HELP)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("fsuipc2http [OPTIONS]", options );
            return null;
        }
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
