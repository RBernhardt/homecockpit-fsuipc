package de.newsarea.homecockpit.fsuipc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class NativeLibraryLoader {

    private static final Logger log = LoggerFactory.getLogger(NativeLibraryLoader.class);

    private static final String FSUIPC_NATIVELIB = "/native/fsuipc_java.dll";

    private static boolean isLoaded = false;

    public static void loadNativeLibrary() {
        if(isLoaded) {
            return;
        }
        // ~
        try {
            // have to use a stream
            InputStream in = NativeLibraryLoader.class.getResourceAsStream(FSUIPC_NATIVELIB);
            if (in != null) {
                try {
                    // always write to different location
                    String tempName = FSUIPC_NATIVELIB.substring(FSUIPC_NATIVELIB.lastIndexOf('/') + 1);
                    File fileOut = File.createTempFile(tempName.substring(0, tempName.lastIndexOf('.')), tempName.substring(tempName.lastIndexOf('.'), tempName.length()));
                    fileOut.deleteOnExit();

                    OutputStream out = new FileOutputStream(fileOut);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0){
                        out.write(buf, 0, len);
                    }

                    out.close();
                    // ~
                    log.info("load library: {}", fileOut.toString());
                    System.load(fileOut.toString());
                    isLoaded = true;
                } finally {
                    in.close();
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        } catch (UnsatisfiedLinkError e) {
            log.info(e.getMessage(), e);
        }
    }

}
