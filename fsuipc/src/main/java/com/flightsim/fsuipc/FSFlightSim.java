/*
	Copyright 2003 Mark Burton
*/

package com.flightsim.fsuipc;


public class FSFlightSim extends FSUIPC
{
	public FSFlightSim()
	{
	super();
	}
	public String StartSituationName()
	{
	return getString(0x0024,255);
	}
}