package de.newsarea.homecockpit.fsuipc2net.net.domain;

import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetMessageItem {

    public static final String REGEX_ITEM = "0x([A-F0-9]{1,4}):([0-9]+)(?::0x((?:[A-F0-9][A-F0-9])+))?";

    private final OffsetIdent offsetIdent;
    private final ByteArray byteArray;

    public OffsetIdent getOffsetIdent() {
        return offsetIdent;
    }

    public ByteArray getByteArray() {
        return byteArray;
    }

    public NetMessageItem(OffsetIdent offsetIdent, ByteArray byteArray) {
        this.offsetIdent = offsetIdent;
        this.byteArray = byteArray;
    }

    public static NetMessageItem fromString(String value) {
        Pattern p = Pattern.compile(REGEX_ITEM);
        Matcher m = p.matcher(value);
        if(m.find()) {
            int offset = Integer.parseInt(m.group(1), 16);
            int size = Integer.parseInt(m.group(2));
            // ~
            ByteArray byteArray = null;
            String byteArrayHex = m.group(3);
            if(byteArrayHex != null) {
                byteArray = ByteArray.create(new BigInteger(byteArrayHex, 16), byteArrayHex.length() / 2);
            }
            return new NetMessageItem(new OffsetIdent(offset, size), byteArray);
        }
        throw new IllegalArgumentException("invalid input - " + value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NetMessageItem)) return false;
        NetMessageItem item = (NetMessageItem) o;
        if (!byteArray.equals(item.byteArray)) return false;
        return offsetIdent.equals(item.offsetIdent);
    }

    @Override
    public int hashCode() {
        int result = offsetIdent.hashCode();
        result = 31 * result + byteArray.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder strBld = new StringBuilder();
        strBld.append(ByteArray.create(String.valueOf(getOffsetIdent().getOffset()), 2).toHexString());
        strBld.append(":");
        strBld.append(getOffsetIdent().getSize());
        if(getByteArray() != null) {
            strBld.append(":");
            strBld.append(getByteArray().toHexString());
        }
        return strBld.toString();
    }
}
