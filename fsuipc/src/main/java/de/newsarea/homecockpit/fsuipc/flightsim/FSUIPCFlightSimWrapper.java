package de.newsarea.homecockpit.fsuipc.flightsim;

import com.flightsim.fsuipc.fsuipc_wrapper;
import de.newsarea.homecockpit.fsuipc.util.NativeLibraryLoader;

/**
 * wrapper for better unit testing
 */
class FSUIPCFlightSimWrapper {

	public void open() {
        NativeLibraryLoader.loadNativeLibrary();
		fsuipc_wrapper.Open(fsuipc_wrapper.SIM_ANY);
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
		fsuipc_wrapper.Close();
	}
	
	
}
