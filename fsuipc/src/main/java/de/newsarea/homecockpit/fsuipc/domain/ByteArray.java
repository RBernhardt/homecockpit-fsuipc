package de.newsarea.homecockpit.fsuipc.domain;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class ByteArray {

    private final byte[] data;

    private ByteArray(byte[] data) {
        this.data = data;
    }

    public static ByteArray create(byte[] data, boolean isLittleEndian) {
        if(isLittleEndian) {
            return new ByteArray(changeBitOrder(data));
        }
        return new ByteArray(data);
    }

    public static ByteArray create(byte[] data) {
        return create(data, false);
    }

    public static ByteArray create(BigInteger bigInt, int size) {
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

    public static ByteArray create(String numberString, int size) {
        BigInteger bigInt = new BigInteger(numberString);
        return create(bigInt, size);
    }

    public static ByteArray create(long value, int size) {
        return create(ByteBuffer.allocate(size).putLong(value).array());
    }

    public static ByteArray create(int value, int size) {
        return create(ByteBuffer.allocate(size).putInt(value).array());
    }

    public static ByteArray create(float value, int size) {
        return create(ByteBuffer.allocate(size).putFloat(value).array());
    }

    public static ByteArray create(double value, int size) {
        return create(ByteBuffer.allocate(size).putDouble(value).array());
    }

    public static ByteArray create(Object value, int size) {
        return create(value.toString(), size);
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
        return changeBitOrder(data);
    }

    public byte[] toByteArray() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
        byteBuffer.put(data);
        return byteBuffer.array();
    }

    private static byte[] changeBitOrder(byte[] data) {
        byte[] out = new byte[data.length];
        for(int i=0; i < data.length; i++) {
            out[data.length - i - 1] = data[i];
        }
        return out;
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
        if (!(o instanceof ByteArray)) return false;
        ByteArray byteArray = (ByteArray) o;
        return Arrays.equals(data, byteArray.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    public String toHexString() {
        StringBuilder strBld = new StringBuilder();
        char[] hexChars = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F' };
        int high;
        int low;
        for (byte cdata : data) {
            high = ((cdata & 0xf0) >> 4);
            low = (cdata & 0x0f);
            strBld.append(hexChars[high]);
            strBld.append(hexChars[low]);
        }
        return "0x" + strBld.toString();
    }

    public String toNumberString() {
        return toBigInteger().toString();
    }

    @Override
    public String toString() {
        return toHexString();
    }

}
