package de.newsarea.homecockpit.fsuipc.flightsim;

import com.flightsim.fsuipc.fsuipc_wrapper;
import de.newsarea.homecockpit.fsuipc.util.NativeLibraryLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;

/**
 * wrapper for better unit testing
 */
class FSUIPCFlightSimWrapper {

    private static final Logger log = LoggerFactory.getLogger(FSUIPCFlightSimWrapper.class);

	public void open() throws ConnectException {
        try {
            NativeLibraryLoader.loadNativeLibrary();
            fsuipc_wrapper.Open(fsuipc_wrapper.SIM_ANY);
        } catch(UnsatisfiedLinkError uex) {
            throw new ConnectException(uex.getMessage());
        } catch (Exception ex) {
            throw new ConnectException(ex.getMessage());
        }
    }
	
	public void write(int offset, int size, byte[] data) {
		fsuipc_wrapper.WriteData(offset, size, data);
	}
	
	public byte[] read(int offset, int size) {
		byte[] data = new byte[size];
		fsuipc_wrapper.ReadData(offset, size, data);
		return data;
	}
	
	public void close() {
        try {
		    fsuipc_wrapper.Close();
        } catch(UnsatisfiedLinkError uex) { }
	}
	
	
}
