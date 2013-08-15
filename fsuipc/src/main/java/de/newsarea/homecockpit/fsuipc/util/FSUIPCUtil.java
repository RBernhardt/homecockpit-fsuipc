package de.newsarea.homecockpit.fsuipc.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class FSUIPCUtil {

    private FSUIPCUtil() { }

	/* */
	
	public static long toFSUIPCLatitude(double latitude) {
		return (long)(latitude / (90D / (10001750D * 65536D * 65536D)));
	}
	
	public static double toLatitude(long fsuipcLatitude) {
		// 42957189152768000
		// 2,0951091487837894969942760016997e-15
		return fsuipcLatitude * (90D / (10001750D * 65536D * 65536D));
	}
	
	/* */
	
	public static long toFSUIPCLongitude(double longitude) {
		return (long)(longitude / (360.0 / (65536.0 * 65536.0 * 65536.0 * 65536.0)));
	}
	
	public static double toLongitude(long fsuipcLongitude) {
		return (fsuipcLongitude * (360.0 / (65536.0 * 65536.0 * 65536.0 * 65536.0)));
	}
	
	/* */
	
	public static double toAlititude(long fsuipcAltitude) {		
		long mUnit = fsuipcAltitude >> 32;
        long mFraction = 0x0000FFFF & fsuipcAltitude;
        double altInMeter = Double.valueOf(mUnit + "." + mFraction);
        return new BigDecimal(altInMeter / 1609.344 * 5280D).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}
	
	public static long toFSUIPCAlititude(double altitude) {
		double meter = altitude * 0.0254 * 12D;
        long mUnit = (long)meter;
        long mFraction = (long)((meter - mUnit) * 1000000000);        
        return mUnit << 32 | mFraction;
	}
	
	/* */
	
	public static double toHeading(int fsuipcHeading) {
		double hdg = fsuipcHeading * 8.3819E-008; //8.1716E-008D;
        if (hdg < 0) { hdg += 360; }
        return (new BigDecimal(hdg)).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}
	
	public static int toFSUIPCHeading(double heading) {
		return (int)Math.round(heading / 8.3819E-008D);
	}
	
	/* */
	
	public static double toDegree(int fsuipcDegree) {
		double hdg = fsuipcDegree * 8.3819E-008; //8.1716E-008D;
        return (new BigDecimal(hdg)).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
	
	public static long toFSUIPCDegree(double degree) {
		return (long)(degree / 8.3819E-008D);
	}
		
}
