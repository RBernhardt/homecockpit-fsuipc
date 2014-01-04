/*
Copyright 2003 Mark Burton
*/ 
package com.flightsim.fsuipc;

/**
Wrapper class for fsuipc_java.dll
@author Mark Burton
*/
public class fsuipc_wrapper {
	
	public static final int SIM_ANY=0;
	public static final int SIM_FS98=1;
	public static final int SIM_FS2K=2;
	public static final int SIM_CFS2=3;
	public static final int SIM_CFS1=4;
	public static final int SIM_FS2K2=6;

	/** 
	Connect to FS.
	@param 	aFlightSim Version of flightsim to try and connect to.
	@return 0 if connection failed
	*/
	public static synchronized native int Open(int aFlightSim);
	/** Close the connection */
	public static synchronized native void Close();
	/**	Read bytes from FS */
    public static synchronized native void ReadData(int aOffset,int aCount,byte[] aData);	
	/**	Write byte to FS */
	public static synchronized native void WriteData(int aOffset,int aCount,byte[] aData);	
	/** Process the commands */
	public static synchronized native void Process();

}
