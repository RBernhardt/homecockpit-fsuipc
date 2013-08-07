package de.newsarea.homecockpit.fsuipc.util;

import java.math.BigInteger;

public final class DataTypeUtil {

	private DataTypeUtil() { }
	
	public static long toLong(byte[] data) {
		if(data.length != 8) { throw new IllegalArgumentException("invalid byte array length - " + data.length); }
		return (Long)toNumber(data, 8);
    }
	
	public static long toUInt(byte[] data) {
		if(data.length != 4) { throw new IllegalArgumentException("invalid byte array length - " + data.length); }
		return (Long)toNumber(data, 8);
    }
	
	public static int toInt(byte[] data) {
		if(data.length != 4) { throw new IllegalArgumentException("invalid byte array length - " + data.length); }
		return (Integer)toNumber(data, 4);
    }
	
	public static short toShort(byte[] data) {
		if(data.length != 2) { throw new IllegalArgumentException("invalid byte array length - " + data.length); }
		return (Short)toNumber(data, 2);
    }
	
	public static byte toByte(byte[] data) {
		if(data.length != 1) { throw new IllegalArgumentException("invalid byte array length - " + data.length); }
		return (Byte)toNumber(data, 1);
    }
	
	public static Number toNumber(Object value) {
		if(value instanceof Number) {
			return (Number)value;
		} else if(value instanceof String) {
			try {
				return Byte.parseByte(value.toString());
			} catch(NumberFormatException nfe) { }
			try {
				return Short.parseShort(value.toString());
			} catch(NumberFormatException nfe) { }
			try {
				return Integer.parseInt(value.toString());
			} catch(NumberFormatException nfe) { }
			try {
				return Long.parseLong(value.toString());
			} catch(NumberFormatException nfe) { }
		}
		return null;
	}
	
	public static Number toNumber(byte[] data, int size) {
		switch(size) {		
			case 1:
				return (byte)toBigInteger(data).longValue();
			case 2:
				return (short)toBigInteger(data).longValue();
			case 4:
				return (int)toBigInteger(data).longValue();
			case 8:
				return toBigInteger(data).longValue();		
		}
		return null;
	}
	
	public static byte[] toByteArray(String numberString, int size) {
		BigInteger bigInt = new BigInteger(numberString);
		byte[] outputData = new byte[size];
		byte[] bigIntArray =  bigInt.toByteArray();
		for(int i=0; i < outputData.length; i++) {			
			int outputArrayIdx = outputData.length - i - 1;
			int bigIntArrayIdx = bigIntArray.length - i - 1;
			if(outputArrayIdx == -1 || bigIntArrayIdx == -1) { break; }
			outputData[outputArrayIdx] = bigIntArray[bigIntArrayIdx];
		}
		return outputData;
	}
	
	public static byte[] toByteArray(Object obj, int size) {	
		return toByteArray(obj.toString(), size);
	}	
	
	public static BigInteger toBigInteger(byte[] data) {
		byte[] ndata = new byte[data.length + 1];
		for(int i=0; i < data.length; i++) {
			ndata[i + 1] = data[i];
		}
		return new BigInteger(ndata);
	}
	
	public static String toString(byte[] data) {
		return toBigInteger(data).toString();
	}
	
	public static byte[] toLittleEndian(byte[] data) {
		byte[] out = new byte[data.length];
		for(int i=0; i < data.length; i++) {
			out[data.length - i - 1] = data[i];
		}
		return out;
	}
	
	public static String toHexString(byte[] data) {
	    StringBuffer buf = new StringBuffer();
	    char[] hexChars = { 
	        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
	        'A', 'B', 'C', 'D', 'E', 'F' };
	    int len = data.length;
	    int high = 0;
	    int low = 0;
	    for (int i = 0; i < len; i++) {
	        high = ((data[i] & 0xf0) >> 4);
	        low = (data[i] & 0x0f);
	        buf.append(hexChars[high]);
	        buf.append(hexChars[low]);
	    } 
	    return "0x" + buf.toString();
	}
	
	public static String toHexString(int data) {
		return toHexString(toByteArray(String.valueOf(data), 4));
	}
	
	public static boolean isEquals(byte[] d1, byte[] d2) {
		if(d1 == null || d2 == null) { return false; }
		if(d1.length != d2.length) { return false; }
		for(int i=0; i < d1.length; i++) {
			if(d1[i] != d2[i]) { return false; }
		}
		return true;
	}
	
	public static boolean isHighBit(byte[] data, int idx) {
		int bidx = (idx / 8);
		byte nidx = (byte)(idx % 8);		
		return isHighBit(data[data.length - 1 - bidx], nidx);
	}
	
	public static boolean isHighBit(byte data, byte idx) {
		return (data & (1 << idx)) > 0;
	}
	
}
