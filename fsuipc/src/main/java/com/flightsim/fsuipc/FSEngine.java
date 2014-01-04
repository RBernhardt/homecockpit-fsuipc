package com.flightsim.fsuipc;

/*
Copyright 2002 Mark Burton
*/

public abstract class FSEngine extends FSUIPC
	{
	protected int iMixAddress=0;
	protected int iStartAddress=0;
	protected int iCombustionAddress=0;
	protected int iValueOffset;
	protected byte[] iData;
	
	public FSEngine()
		{
		super();
		iData = new byte[152];
		}
	public void ReadData()
		{
		fsuipc_wrapper.ReadData(iValueOffset,152,iData);
		}
	public int ThrottleLever()
		{
		return (0xff & iData[0]) + (iData[1]<<8);
		}
	public void SetMixture(int aData)
		{
		System.out.println("SetMixture "+new Integer(aData).toString());
		iData[0] = (byte) (aData & 0xff);
		iData[1] = (byte) ((aData>>8) & 0xff);
		fsuipc_wrapper.WriteData(iMixAddress,2,iData);
		}
	public void SetStarter(int aData)
		{
		System.out.println("SetStarter "+new Integer(aData).toString());
		iData[0] = (byte) (aData & 0xff);
		iData[1] = (byte) ((aData>>8) & 0xff);
		fsuipc_wrapper.WriteData(iStartAddress,2,iData);
		}
	public int Combustion()
		{
		return getShort(iCombustionAddress);
		}
	}