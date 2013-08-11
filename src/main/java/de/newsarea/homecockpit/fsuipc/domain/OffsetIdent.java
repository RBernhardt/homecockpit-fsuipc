package de.newsarea.homecockpit.fsuipc.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OffsetIdent {
	
	private int offset;
	private int size;
	
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
		return this.getOffset() + " : " + this.getSize();
	}

    public static OffsetIdent from(String value) {
        Pattern p = Pattern.compile("([0-9]+)\\s*,\\s*([0-9]+)\\s*");
        Matcher m = p.matcher(value);
        while(m.find()) {
            int offset = Integer.parseInt(m.group(1));
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
        if (offset != that.offset) return false;
        if (size != that.size) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = offset;
        result = 31 * result + size;
        return result;
    }

    @Override
	public String toString() {
		StringBuffer strBld = new StringBuffer();
		strBld.append(ByteArray.create(String.valueOf(offset), 4).toHexString());
		strBld.append(" (");
		strBld.append(getOffset());
		strBld.append(")");
		strBld.append(" : ");
		strBld.append(getSize());
		return strBld.toString();
	}

}
