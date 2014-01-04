/*
Copyright 2002 Mark Burton
*/

package com.flightsim.fsuipc;


public class FSADF extends FSNavRadio
{
	public FSADF()
	{
	super();
	iFreq = 0x34c;
	iID = 0x0303e;
	iName = 0x03044;
	}
	
	public String FreqAsString()
		{
		short freq = getShort(0x34c);
		int	dig1 = (freq>>8 & 0x0f);
		int dig2 = (freq>>4 & 0x0f);
		int dig3 = (freq& 0x0f);
		
		short freq2 = getShort(0x356);
		int dig4 = (freq2>>12 & 0x0f);
		int dig5 = (freq2& 0x0f);
		String ret = new String(new Integer(dig4).toString()+
								new Integer(dig1).toString()+
								new Integer(dig2).toString()+
								new Integer(dig3).toString()+ "."+
								new Integer(dig5).toString());
		return ret;
		}
}
