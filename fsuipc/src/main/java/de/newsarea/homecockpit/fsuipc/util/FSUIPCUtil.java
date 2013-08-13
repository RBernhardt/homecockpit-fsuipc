package de.newsarea.homecockpit.fsuipc.util;

import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FSUIPCUtil {
	
	private static final String REGEX_ITEMS = "\\[\\s*(.*?)\\s*\\]";

    private FSUIPCUtil() { }

    /**
     * use OffsetItem.from(String value)
     */
    @Deprecated
	public static OffsetItem toOffsetItem(String value) {
		return OffsetItem.from(value);
	}

	public static OffsetItem[] toOffsetItems(String value) {
		List<OffsetItem> items = new ArrayList<>();
		Pattern pOffsetObj = Pattern.compile(REGEX_ITEMS);
		Matcher mOffsetObj = pOffsetObj.matcher(value);
		while(mOffsetObj.find()) {
			items.add(OffsetItem.from(mOffsetObj.group()));
		}	
		return items.toArray(new OffsetItem[] { });
	}

    /**
     * use OffsetIdent.from(String value)
     */
    @Deprecated
	public static OffsetIdent toOffsetIdent(String value) {
		return OffsetIdent.from(value);
	}
	
	public static OffsetIdent[] toOffsetIdents(String value) {
		List<OffsetIdent> items = new ArrayList<>();
		Pattern pOffsetObj = Pattern.compile(REGEX_ITEMS);
		Matcher mOffsetObj = pOffsetObj.matcher(value);
		while(mOffsetObj.find()) {
			items.add(OffsetIdent.from(mOffsetObj.group()));
		}	
		return items.toArray(new OffsetIdent[] { });
	}
	
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
