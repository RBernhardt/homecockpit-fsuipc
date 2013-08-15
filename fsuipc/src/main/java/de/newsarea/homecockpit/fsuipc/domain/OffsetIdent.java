package de.newsarea.homecockpit.fsuipc.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OffsetIdent {

    public static final String REGEX_ITEM = "0x([A-F0-9]{1,4})\\s*:\\s*([0-9]+)";
	
	private final int offset;
	private final int size;

	public int getOffset() {
		return this.offset;
	}

	public int getSize() {
		return this.size;
	}
	
	public OffsetIdent(int offset, int size) {
		this.offset = offset;
		this.size = size;
	}

	public String getIdentifier() {
		return toString();
	}

    public static OffsetIdent fromString(String value) {
        Pattern p = Pattern.compile(REGEX_ITEM);
        Matcher m = p.matcher(value);
        if(m.find()) {
            int offset = Integer.parseInt(m.group(1), 16);
            int size = Integer.parseInt(m.group(2));
            //
            return new OffsetIdent(offset, size);
        }
        throw new IllegalArgumentException("invalid input - " + value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OffsetIdent)) return false;
        OffsetIdent that = (OffsetIdent) o;
        return offset == that.offset;
    }

    @Override
    public int hashCode() {
        int result = offset;
        result = 31 * result + size;
        return result;
    }

    @Override
	public String toString() {
		StringBuilder strBld = new StringBuilder();
		strBld.append(ByteArray.create(String.valueOf(offset), 2).toHexString());
		strBld.append(" : ");
		strBld.append(getSize());
		return strBld.toString();
	}

}
