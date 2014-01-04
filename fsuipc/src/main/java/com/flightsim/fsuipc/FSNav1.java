
/*
Copyright 2002 Mark Burton
*/
package com.flightsim.fsuipc;

public class FSNav1 extends FSNavRadio
	{
	public FSNav1()
	{
	super();
	iStandbyFreq = 0x311e;
	iFreq = 0x350;
	iID = 0x3000;
	iName = 0x3006;
	iSwap = 0x3123;
	iLocNeedle = 0x0c48;
	iGlideSlope = 0x0c49;
	}
}
