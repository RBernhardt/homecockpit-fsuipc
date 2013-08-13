package de.newsarea.homecockpit.fsuipc.domain;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OffsetItem extends OffsetIdent {
	
	private final ByteArray value;

    /* @Deprecated
	public byte[] getRawValue() {
		return this.value.clone();
	}  */

    public ByteArray getValue() {
        return this.value;
    }

    public OffsetItem(int offset, int size, ByteArray value) {
        super(offset, size);
        this.value = value;
    }

	public OffsetItem(int offset, int size, byte[] value) {
		this(offset, size, ByteArray.create(value));
	}

    public static OffsetItem from(String value) {
        Pattern p = Pattern.compile("([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([-0-9]+)");
        Matcher m = p.matcher(value);
        if(m.find()) {
            int offset = Integer.parseInt(m.group(1));
            int size = Integer.parseInt(m.group(2));
            ByteArray byteArray = ByteArray.create(m.group(3), size);
            return new OffsetItem(offset, size, byteArray);
        }
        throw new IllegalArgumentException("invalid input - " + value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OffsetItem)) return false;
        if (!super.equals(o)) return false;
        OffsetItem that = (OffsetItem) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
	public String toString() {
        StringBuilder strBld = new StringBuilder();
		strBld.append(super.toString());
		strBld.append(" : ");
		strBld.append(getValue().toNumberString());
		strBld.append(" (");
		strBld.append(getValue().toHexString());
		strBld.append(")");
		return strBld.toString();
	}

}
