package com.flightsim.fsuipc;

/*
Copyright 2002 Mark Burton
*/

public class FSLights extends FSUIPC
	{
	public FSLights()
		{
		super();
		}

	public int NavLightState()
		{
		return getInt(0x0d0c) & 0x01;
		}
	
	public void SetNavLight(boolean aOn)
		{
		int current = NavLightState();
		byte[] data = new byte[2];
		if(aOn == true)
			{
			data[0] = (byte) (current| 0x01);
			data[1]=0;
			fsuipc_wrapper.WriteData(0x0d0c,2,data);
			}
		else
			{
			data[0] = (byte) (current & 0xfe);
			data[1]=0;
			fsuipc_wrapper.WriteData(0x0d0c,2,data);
			}
		}

	public int BeaconLightState()
		{
		return getInt(0x0d0c) & 0x02;
		}

	public void SetBeaconLight(boolean aOn)
		{
		int current = BeaconLightState();
		byte[] data = new byte[2];
		if(aOn == true)
			{
			data[0] = (byte) (current| 0x02);
			data[1]=0;
			fsuipc_wrapper.WriteData(0x0d0c,2,data);
			}
		else
			{
			data[0] = (byte) (current & 0xfd);
			data[1]=0;
			fsuipc_wrapper.WriteData(0x0d0c,2,data);
			}
		}

	public int LandingLightState()
		{
		return getInt(0x0d0c) & 0x04;
		}
	public void SetLandingLight(boolean aOn)
		{
		int current = LandingLightState();
		byte[] data = new byte[2];
		if(aOn == true)
			{
			data[0] = (byte) (current| 0x04);
			data[1]=0;
			fsuipc_wrapper.WriteData(0x0d0c,2,data);
			}
		else
			{
			data[0] = (byte) (current & 0xfb);
			data[1]=0;
			fsuipc_wrapper.WriteData(0x0d0c,2,data);
			}
		}
	
	public int TaxiLightState()
		{
		return getInt(0x0d0c) & 0x08;
		}
	
	public void SetTaxiLight(boolean aOn)
		{
		int current = TaxiLightState();
		byte[] data = new byte[2];
		if(aOn == true)
			{
			data[0] = (byte) (current| 0x08);
			data[1]=0;
			fsuipc_wrapper.WriteData(0x0d0c,2,data);
			}
		else
			{
			data[0] = (byte) (current & 0xf7);
			data[1]=0;
			fsuipc_wrapper.WriteData(0x0d0c,2,data);
			}
		}
	
	public int StrobeLightState()
		{
		return getInt(0x0d0c) & 0x10;
		}

	public void SetStrobeLight(boolean aOn)
		{
		int current = StrobeLightState();
		byte[] data = new byte[2];
		if(aOn == true)
			{
			data [0] = (byte) (current| 0x10);
			data [1]=0;
			fsuipc_wrapper.WriteData(0x0d0c,2,data );
			}
		else
			{
			data [0] = (byte) (current & 0xef);
			data [1]=0;
			fsuipc_wrapper.WriteData(0x0d0c,2,data );
			}
		}
	
	public int InstrumentLightState()
		{
		return getInt(0x0d0c) & 0x20;
		}

	public void SetInstrumentLight(boolean aOn)
		{
		int current = InstrumentLightState();
		byte[] data = new byte[2];
		if(aOn == true)
			{
			data[0] = (byte) (current| 0x20);
			data[1]=0;
			fsuipc_wrapper.WriteData(0x0d0c,2,data);
			}
		else
			{
			data[0] = (byte) (current & 0xdf);
			data[1]=0;
			fsuipc_wrapper.WriteData(0x0d0c,2,data);
			}
		}
	
	public int RecognitionLightState()
		{
		return getInt(0x0d0c) & 0x40;
		}
	
	public void SetRecognitionLight(boolean aOn)
		{
		int current = RecognitionLightState();
		byte[] data = new byte[2];
		if(aOn == true)
			{
			data[0] = (byte) (current| 0x40);
			data[1]=0;
			fsuipc_wrapper.WriteData(0x0d0c,2,data);
			}
		else
			{
			data[0] = (byte) (current & 0xbf);
			data[1]=0;
			fsuipc_wrapper.WriteData(0x0d0c,2,data);
			}
		}
	

	public int WingLightState()
		{
		return getInt(0x0d0c) & 0x80;
		}

	public void SetWingLight(boolean aOn)
		{
		int current = WingLightState();
		byte[] data = new byte[2];
		if(aOn == true)
			{
			data[0] = (byte) (current| 0x80);
			data[1]=0;
			fsuipc_wrapper.WriteData(0x0d0c,2,data);
			}
		else
			{
			data[0] = (byte) (current & 0x7f);
			data[1]=0;
			fsuipc_wrapper.WriteData(0x0d0c,2,data);
			}
		}
	
	public int LogoLightState()
		{
		return getInt(0x0d0c) & 0x100;
		}
	
	}
