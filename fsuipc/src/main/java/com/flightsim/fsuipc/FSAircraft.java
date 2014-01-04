package com.flightsim.fsuipc;

public class FSAircraft extends FSUIPC
{
	public FSAircraft()
	{
	super();
	}

	public double Latitude()
	{
	double d = (double)getLong(0x0560);

	d = 90.0*d/(10001750.0 * 65536.0 * 65536.0);
	return d;
	}
	
	public double Longitude()
	{
	double d = (double)getLong(0x0568);

	d = 360.0*d/(65536.0 * 65536.0 * 65536.0 * 65536.0);
	return d;
	}

	public double VOR1LocLatitude()
	{
	int l = getInt(0x0874);
	double lat = (double)l;
	lat *= 90.0/10001750.0;
	return lat; 
	
	}

	public double VOR1LocLongitude()
	{
	int l = getInt(0x0878);
	double lat = (double)l;
	lat *= 360.0/(65536.0*65536.0);
	return lat; 
	
	}

	public int Heading()
	{
	return getInt(0x0580);
	/*
	fsuipc_wrapper.ReadData(0x0580,4,iData);
	System.out.println("0 " +Integer.toHexString(iData[0]));
	System.out.println("1 " +Integer.toHexString(iData[1]));
	System.out.println("2 " +Integer.toHexString(iData[2]));
	System.out.println("3 " +Integer.toHexString(iData[3]));

	return (iData[0]&0xff) | (iData[1]&0xff) << 8 | (iData[2]&0xff) << 16 | (iData[3]&0xff) << 24; 
	*/
	}
	
	public int Magnetic()
	{
	//fsuipc_wrapper.ReadData(0x02a0,4,iData);
	//return (iData[0]&0xff) | (iData[1]&0xff) << 8 | (iData[2]&0xff) << 16 | (iData[3]&0xff) << 24; 
	return getInt(0x02a0);
	}
	
	public double Pitch()
	{
	/*
	ByteBuffer buf = ByteBuffer.allocate(8);
	buf.order(ByteOrder.LITTLE_ENDIAN);
	fsuipc_wrapper.ReadData(0x2f70,8,iData);
	buf.put(iData);
	return  Double.longBitsToDouble(buf.getLong(0));
	*/
	return getDouble(0x2f70);
	}
	
	public double Bank()
	{
	/*

	ByteBuffer buf = ByteBuffer.allocate(8);
	buf.order(ByteOrder.LITTLE_ENDIAN);
	fsuipc_wrapper.ReadData(0x2f78,8,iData);
	buf.put(iData);
	return  Double.longBitsToDouble(buf.getLong(0));
	*/
	return getDouble(0x2f78);
	}
	
	public int IAS()
	{
	/*
	fsuipc_wrapper.ReadData(0x02bc,2,iData);
	return iData[0]  + iData[1] << 8 ;
	*/
	return getInt(0x02bc);
	}

	public int VerticalSpeed()
	{
	/*
	fsuipc_wrapper.ReadData(0x02c8,4,iData);
	return iData[0]  + iData[1] << 8 +iData[2] << 16  + iData[3] << 24;
	*/
	return getInt(0x02c8);
	}

	public int Altitude()
	{
	/*
	byte data[]= new byte[4];
    fsuipc_wrapper.ReadData(0x0574,4,data);
    int ret = (0xff & data[0]) + (data[1]<<8) + (data[2]<<16) + (data[3]<<24);
    //int ret = fsuipc_wrapper.ReadDWord(0x0574);
	return ret;
    */
	return getInt(0x0574);
	}

	public byte LocaliserError()
	{
	/*
	fsuipc_wrapper.ReadData(0x0c48,1,iData);
	return iData[0];
	*/		
	return getByte(0x0c48);
	}
	
	public double Localiser()
	{
	/*

	fsuipc_wrapper.ReadData(0x0870,2,iData);
	
	int l = iData[0]  + iData[1] << 8;
	*/
	short l = getShort(0x0870);

	double loc = (double)(l*360.0)/65536.0;
	return loc;
	}

	public short NumberOfEngines()
	{
	return getShort(0x0AEC);
	}
	public byte EngineType()
	{
	String msg = new String("Hello, world");
	fsuipc_wrapper.WriteData(0x3380,msg.length(),msg.getBytes());
	byte[] data = new byte[2];
	data[0]=10;
	data[1]=0;
	fsuipc_wrapper.WriteData(0x32fa,2,data);
	return getByte(0x0609);
	}
}