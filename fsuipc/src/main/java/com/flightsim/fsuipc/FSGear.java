package com.flightsim.fsuipc;

/*
Copyright 2002 Mark Burton
*/

public class FSGear extends FSUIPC
	{
	public FSGear()
	{
	super();
	}
	
	public int NoseGearState()
		{
		return getShort(0x0bec);
		}
	public int LeftGearState()
		{
		return getShort(0x0bf0);
		}
	
	public int RightGearState()
		{
		return getShort(0x0bf4);
		}
	
	public void GearUp()
		{
		byte[] data = new byte[2];
		data[0] = 0;
		data[1] = 0;
		fsuipc_wrapper.WriteData(0x0BE8,2,data);
		}
	public void GearDown()
		{
		byte[] data = new byte[2];
		data[0] = 0x00;
		data[1] = 0x40;
		fsuipc_wrapper.WriteData(0x0BE8,2,data);
		}
	}