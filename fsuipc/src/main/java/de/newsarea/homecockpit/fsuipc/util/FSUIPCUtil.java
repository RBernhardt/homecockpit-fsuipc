package de.newsarea.homecockpit.fsuipc.util;

import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class FSUIPCUtil {

    private FSUIPCUtil() { }

    /***
     * To convert to Degrees:
     * If your compiler supports long long (64-bit) integers then use such a variable to simply copy this 64-bit value into a double floating point variable and multiply by 90.0/(10001750.0 * 65536.0 * 65536.0).
     * Either way, a negative result is South, positive North.
     * Die Breite kann Werte von 0° (am Äquator) bis ±90° (an den Polen) annehmen.
     */
	public static long toFSUIPCLatitude(double latitude) {
        Assert.isTrue(latitude >= -90, "value is valid from 0 to ±90");
        Assert.isTrue(latitude <= 90, "value is valid from 0 to ±90");
        // ~
		return (long)(latitude / (90D / (10001750D * 65536D * 65536D)));
	}

    /***
     * To convert to Degrees:
     * If your compiler supports long long (64-bit) integers then use such a variable to simply copy this 64-bit value into a double floating point variable and multiply by 90.0/(10001750.0 * 65536.0 * 65536.0).
     * Either way, a negative result is South, positive North.
     */
	public static double toLatitude(long fsuipcLatitude) {
		return fsuipcLatitude * (90D / (10001750D * 65536D * 65536D));
	}
	
	/* */

    /***
     * To convert to Degrees:
     * If your compiler supports long long (64-bit) integers then use such a variable to simply copy this 64-bit value into a double floating point variable and multiply by 360.0/(65536.0 * 65536.0 * 65536.0 * 65536.0).
     * Either way, a negative result is West, positive East.
     */
	public static long toFSUIPCLongitude(double longitude) {
		return (long)(longitude / (360.0 / (65536.0 * 65536.0 * 65536.0 * 65536.0)));
	}

    /***
     * To convert to Degrees:
     * If your compiler supports long long (64-bit) integers then use such a variable to simply copy this 64-bit value into a double floating point variable and multiply by 360.0/(65536.0 * 65536.0 * 65536.0 * 65536.0).
     * Either way, a negative result is West, positive East.
     */
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
