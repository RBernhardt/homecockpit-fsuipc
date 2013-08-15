package de.newsarea.homecockpit.fsuipc.domain;


import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OffsetItem extends OffsetIdent {

    public static final String REGEX_ITEM = "0x([A-F0-9]{4})\\s*:\\s*([0-9]+)\\s*:\\s*0x((?:[A-F0-9][A-F0-9])+)";
	
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
        //
        if(size < value.getSize()) {
            throw new IllegalArgumentException("size (" + size + ") is smaler than value length (" + value.getSize() + ")");
        }
    }

	public OffsetItem(int offset, int size, byte[] value) {
		this(offset, size, ByteArray.create(value));
	}

    public static OffsetItem fromString(String value) {
        Pattern p = Pattern.compile(REGEX_ITEM);
        Matcher m = p.matcher(value);
        if(m.find()) {
            short offset = Short.parseShort(m.group(1), 16);
            int size = Integer.parseInt(m.group(2));
            // create byte array
            String byteArrayHex = m.group(3);
            ByteArray byteArray = ByteArray.create(new BigInteger(byteArrayHex, 16), byteArrayHex.length() / 2);
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
		strBld.append(getValue().toHexString());
		return strBld.toString();
	}

}
