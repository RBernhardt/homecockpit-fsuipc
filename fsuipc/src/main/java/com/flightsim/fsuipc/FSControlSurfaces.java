package com.flightsim.fsuipc;

/*
Copyright 2002 Mark Burton
*/

public class FSControlSurfaces extends FSUIPC
	{
	public FSControlSurfaces()
	{
	super();
	}

	public void SetElevatorTrim(int aValue)
	{
	byte[] data = new byte[2];
	data[0] = (byte) (aValue & 0xff);
	data[1] = (byte) ((aValue >>8) & 0xff);
	fsuipc_wrapper.WriteData(0x0bc0,2,data);
	}

	public void SetElevator(int aValue)
	{
	byte[] data = new byte[2];
	data[0] = (byte) (aValue & 0xff);
	data[1] = (byte) ((aValue >>8) & 0xff);
	fsuipc_wrapper.WriteData(0x0bb2,2,data);
	}

	public void SetAileron(int aValue)
	{
	byte[] data = new byte[2];
	
	data[0] = (byte) (aValue & 0xff);
	data[1] = (byte) ((aValue >>8) & 0xff);
	fsuipc_wrapper.WriteData(0x0bb6,2,data);
	}

	}
