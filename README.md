# Homecockpit-FSUIPC

Java library for FSUIPC which is based on the java implementation of the [FSUIPC SDK](http://www.schiratti.com/dowson.html).

## Requires

* FSUIPC ([http://www.schiratti.com/dowson.html](http://www.schiratti.com/dowson.html))

## Usage

### Write / Read Value

    FSUIPCInterface fsuipcInterface = FSUIPCFlightSimInterface.getInstance();
    fsuipcInterface.open();
    // ..
    // write flaps offset value
    fsuipcInterface.write(new OffsetItem(0x0BDC, 4, ByteArray.create("0", 4)));
    // ..
    // read flaps offset value
    byte[] value = fsuipcInterface.read(new OffsetIdent(0x0BDC, 4));
    // ..
    fsuipcInterface.close();

### Monitor Offset

    fsuipcInterface.addEventListener(new OffsetEventListener() {
        @Override
        public void offsetValueChanged(OffsetItem offsetItem) {
            /* handling value changed */
        }
    });
    fsuipcInterface.monitor(new OffsetIdent(0x0570, 8)); // altitude

## Licensing

The project is released under version 2.0 of the Apache License. See LICENSE.txt for details.

