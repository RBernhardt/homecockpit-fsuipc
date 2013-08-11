package de.newsarea.homecockpit.fsuipc.domain;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteArray {

    private byte[] data;

    ByteArray(byte[] data) {
        this.data = data;
    }

    public static ByteArray create(byte[] data, boolean littleEndian) {
        if(littleEndian) {
            return new ByteArray(toLittleEndian(data));
        }
        return new ByteArray(data);
    }

    public static ByteArray create(byte[] data) {
        return create(data, false);
    }

    public static ByteArray create(String numberString, int size) {
        BigInteger bigInt = new BigInteger(numberString);
        byte[] outputData = new byte[size];
        byte[] bigIntArray =  bigInt.toByteArray();
        for(int i=0; i < outputData.length; i++) {
            int outputArrayIdx = outputData.length - i - 1;
            int bigIntArrayIdx = bigIntArray.length - i - 1;
            if(outputArrayIdx == -1 || bigIntArrayIdx == -1) { break; }
            outputData[outputArrayIdx] = bigIntArray[bigIntArrayIdx];
        }
        return create(outputData);
    }

    public int getSize() {
        return data.length;
    }

    public byte get(int i) {
        return data[i];
    }

    public BigInteger toBigInteger() {
        ByteBuffer bigIntBuffer = ByteBuffer.allocate(getSize() + 1);
        bigIntBuffer.position(1);
        bigIntBuffer.put(data);
        return new BigInteger(bigIntBuffer.array());
    }

    public long toLong() {
        return (Long)toNumber(8);
    }

    public long toUInt() {
        return (Long)toNumber(8);
    }

    public int toInt() {
        return (Integer)toNumber(4);
    }

    public short toShort() {
        return (Short)toNumber(2);
    }

    public byte toByte() {
        return (Byte)toNumber(1);
    }

    public Number toNumber(int size) {
        switch(size) {
            case 1:
                if(data.length > 1) { throw new IllegalArgumentException("invalid byte array length - " + data.length); }
                return (byte)toBigInteger().longValue();
            case 2:
                if(data.length > 2) { throw new IllegalArgumentException("invalid byte array length - " + data.length); }
                return (short)toBigInteger().longValue();
            case 4:
                if(data.length > 4) { throw new IllegalArgumentException("invalid byte array length - " + data.length); }
                return (int)toBigInteger().longValue();
            case 8:
                if(data.length > 8) { throw new IllegalArgumentException("invalid byte array length - " + data.length); }
                return toBigInteger().longValue();
            default:
                throw new IllegalArgumentException("unsupported size - " + size);
        }
    }

    public byte[] toLittleEndian() {
        return toLittleEndian(data);
    }

    public boolean isHighBit(int idx) {
        int byteIdx = (idx / 8);
        byte mByteIdx = (byte)(idx % 8);
        return isHighBit(data[data.length - 1 - byteIdx], mByteIdx);
    }

    private boolean isHighBit(byte data, byte idx) {
        return (data & (1 << idx)) > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteArray byteArray = (ByteArray) o;
        if (!Arrays.equals(data, byteArray.data)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return data != null ? Arrays.hashCode(data) : 0;
    }

    public String toHexString() {
        StringBuffer buf = new StringBuffer();
        char[] hexChars = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F' };
        int len = data.length;
        int high;
        int low;
        for (int i = 0; i < len; i++) {
            high = ((data[i] & 0xf0) >> 4);
            low = (data[i] & 0x0f);
            buf.append(hexChars[high]);
            buf.append(hexChars[low]);
        }
        return "0x" + buf.toString();
    }

    @Override
    public String toString() {
        return toHexString();
    }

    public String toNumberString() {
        return toBigInteger().toString();
    }

    /* */

    public static byte[] toLittleEndian(byte[] data) {
        byte[] out = new byte[data.length];
        for(int i=0; i < data.length; i++) {
            out[data.length - i - 1] = data[i];
        }
        return out;
    }

}
