# FSUIPC HTTP-Server

The FSUIPC HTTP-Server can be used to connect to fsuipc over the network.

## Requires

* FSUIPC ([http://www.schiratti.com/dowson.html](http://www.schiratti.com/dowson.html))

## Usage

    usage: fsuipc2http [OPTIONS]
     -h,--help                help message
     -p,--http-port <arg>     http-port for FSUIPC connections
     -s,--socket-port <arg>   socket-port for FSUIPC events

### GET offset value

    // 0x0274 | 2 | Frame rate is given by 32768/this value
    curl -X GET http://localhost:8080/offsets/0274/?size=2

    HTTP/1.1 200 OK
    Content-Type: text/plain
    Date: Sun, 09 Mar 2014 21:26:59 GMT
    Content-Length: 6

    0x0000

### CONNECT to event socket

    telnet localhost:8081

## Licensing

The project is released under version 2.0 of the Apache License. See LICENSE.txt for details.

