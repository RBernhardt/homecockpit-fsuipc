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
    curl -i -X GET http://simulator:8080/offsets/0274/?size=2

    HTTP/1.1 200 OK
    Content-Type: text/plain
    Date: Mon, 10 Mar 2014 18:55:59 GMT
    Content-Length: 6

    0x0447

### PUT offset value

    // 0x0262 | 2 | Pause control (write 1 to pause, 0 to un-pause).
    curl -i -X PUT -H"Content-Type: text/plain" -d"0x0001" http://simulator:8080/offsets/0262

    HTTP/1.1 200 OK
    Content-Type: text/plain
    Date: Mon, 10 Mar 2014 19:48:36 GMT
    Content-Length: 6

    0x0001

    ... or ...

    // 0x0262 | 2 | Pause control (write 1 to pause, 0 to un-pause).
    curl -i -X PUT -H"Content-Type: application/x-www-form-urlencoded" -d"data=0x0000" http://simulator:8080/offsets/0262

    HTTP/1.1 200 OK
    Content-Type: text/plain
    Date: Mon, 10 Mar 2014 19:48:02 GMT
    Content-Length: 6

    0x0000

    ... or with blocking offset after execution ...

    // 0x0262 | 2 | Pause control (write 1 to pause, 0 to un-pause).
    curl -i -X PUT -H"Content-Type: application/x-www-form-urlencoded" -d"data=0x0001&timeOfBlocking=1000" http://simulator:8080/offsets/0262

    HTTP/1.1 200 OK
    Content-Type: text/plain
    Date: Mon, 10 Mar 2014 19:49:10 GMT
    Content-Length: 6

    0x0001


### MONITOR offset value

    // 0x0274 | 2 | Frame rate is given by 32768/this value
    curl -i -X POST -d"offset=0274&size=2" http://simulator:8080/monitor

    HTTP/1.1 200 OK
    Date: Mon, 10 Mar 2014 18:58:23 GMT
    Content-Length: 0

### CONNECT to event socket (monitored offsets)

    telnet simulator 8081

    Connected to simulator
    Escape character is '^]'.
    [{"offset":"0x0274","size":2,"data":"0x03F5"}]
    [{"offset":"0x0274","size":2,"data":"0x044D"}]
    ^C
    **exit**
    Connection closed by foreign host.

## Licensing

The project is released under version 2.0 of the Apache License. See LICENSE.txt for details.

