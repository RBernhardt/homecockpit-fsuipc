/*
Copyright 2002 Mark Burton
*/
package com.flightsim.fsuipc;


public class FSNav2 extends FSNavRadio
{
	public FSNav2()
	{
	super();
	iStandbyFreq = 0x3120;
	iFreq = 0x352;
	iID = 0x301f;
	iName = 0x3025;
	iSwap = 0x3123;
	iLocNeedle = 0x0c59;
	iGlideSlope = 0x0c49;
	}
}
