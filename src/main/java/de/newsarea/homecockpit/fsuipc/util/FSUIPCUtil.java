package de.newsarea.homecockpit.fsuipc.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;

public class FSUIPCUtil {
	
	private static String REGEX_ITEMS = "\\[\\s*(.*?)\\s*\\]";
			
	public static OffsetItem toOffsetItem(String value) {
		Pattern p = Pattern.compile("([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([-0-9]+)");
		Matcher m = p.matcher(value);
		while(m.find()) {
			int offset = Integer.parseInt(m.group(1));
			int size = Integer.parseInt(m.group(2));
			byte[] bValue = DataTypeUtil.toByteArray(m.group(3), size);
			//
			return new OffsetItem(offset, size, bValue);
		}
		//
		return null;
	}
	
	public static OffsetItem[] toOffsetItems(String value) {
		List<OffsetItem> items = new ArrayList<OffsetItem>();
		Pattern pOffsetObj = Pattern.compile(REGEX_ITEMS);
		Matcher mOffsetObj = pOffsetObj.matcher(value);
		while(mOffsetObj.find()) {
			items.add(toOffsetItem(mOffsetObj.group()));
		}	
		return items.toArray(new OffsetItem[] { });
	}
	
	public static OffsetIdent toOffsetIdent(String value) {
		Pattern p = Pattern.compile("([0-9]+)\\s*,\\s*([0-9]+)\\s*");
		Matcher m = p.matcher(value);
		while(m.find()) {
			int offset = Integer.parseInt(m.group(1));
			int size = Integer.parseInt(m.group(2));
			//
			return new OffsetIdent(offset, size);
		}
		//
		return null;
	}
	
	public static OffsetIdent[] toOffsetIdents(String value) {
		List<OffsetIdent> items = new ArrayList<OffsetIdent>();
		Pattern pOffsetObj = Pattern.compile(REGEX_ITEMS);
		Matcher mOffsetObj = pOffsetObj.matcher(value);
		while(mOffsetObj.find()) {
			items.add(toOffsetIdent(mOffsetObj.group()));
		}	
		return items.toArray(new OffsetIdent[] { });
	}
	
	public static String toString(OffsetItem[] offsetItems) {
		StringBuffer strBld = new StringBuffer();
		strBld.append("[");
		for(OffsetItem offsetItem : offsetItems) {
			strBld.append("[");
			strBld.append(offsetItem.getOffset());
			strBld.append(",");
			strBld.append(offsetItem.getSize());
			strBld.append(",");
			strBld.append(DataTypeUtil.toString(offsetItem.getValue()));
			strBld.append("]");
			strBld.append(",");
		}
		strBld.deleteCharAt(strBld.length() - 1);
		strBld.append("]");
		return strBld.toString();
	}
	
	public static String toString(OffsetItem offsetItem) {
		return toString(new OffsetItem[] { offsetItem });
	}
	
	public static String toString(OffsetIdent[] offsetIdents) {
		StringBuffer strBld = new StringBuffer();
		strBld.append("[");
		for(OffsetIdent offsetIdent : offsetIdents) {
			strBld.append("[");
			strBld.append(offsetIdent.getOffset());
			strBld.append(",");
			strBld.append(offsetIdent.getSize());
			strBld.append("]");
			strBld.append(",");
		}
		strBld.deleteCharAt(strBld.length() - 1);
		strBld.append("]");
		return strBld.toString();
	}
	
	/* */
	
	public static long toFSUIPCLatitude(double latitude) {
		return (long)(latitude / (90D / (10001750D * 65536D * 65536D)));
	}
	
	public static double toLatitude(long fsuipcLatitude) {
		// 42957189152768000
		// 2,0951091487837894969942760016997e-15
		return (double)(fsuipcLatitude * (90D / (10001750D * 65536D * 65536D)));
	}
	
	/* */
	
	public static long toFSUIPCLongitude(double longitude) {
		return (long)(longitude / (360.0 / (65536.0 * 65536.0 * 65536.0 * 65536.0)));
	}
	
	public static double toLongitude(long fsuipcLongitude) {
		return (double)(fsuipcLongitude * (360.0 / (65536.0 * 65536.0 * 65536.0 * 65536.0)));
	}
	
	/* */
	
	public static double toAlititude(long fsuipcAltitude) {		
		long mUnit = (long)(fsuipcAltitude >> 32);
        long mFraction = (long)(0x0000FFFF & fsuipcAltitude);
        double altInMeter = Double.valueOf(mUnit + "." + mFraction);
        return new BigDecimal(altInMeter / 1609.344 * 5280D).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}
	
	public static long toFSUIPCAlititude(double altitude) {
		double meter = altitude * 0.0254 * 12D;
        long mUnit = (long)meter;
        long mFraction = (long)((meter - mUnit) * 1000000000);        
        long fsupicAltitude = ((long)(((long)mUnit) << 32) | ((long)mFraction));
        return fsupicAltitude;
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
