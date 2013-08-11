package de.newsarea.homecockpit.fsuipc.domain;

import org.testng.annotations.Test;

import java.math.BigInteger;

import static org.testng.AssertJUnit.*;
import static org.testng.internal.junit.ArrayAsserts.assertArrayEquals;

public class ByteArrayTest {

    @Test
    public void shouldCreateLittleEndian() throws Exception {
        assertEquals(ByteArray.create(new byte[]{ 8, 7, 6, 5, 4, 3, 2, 1}), ByteArray.create(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8 }, true));
    }

    @Test
    public void shouldCreate() throws Exception {
        assertEquals(ByteArray.create(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8}), ByteArray.create(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8 }));
    }

    @Test
    public void shouldCreateByString() throws Exception {
        assertEquals(ByteArray.create(new byte[]{1}), ByteArray.create("1", 1));
        assertEquals(ByteArray.create(new byte[]{(byte) 0xFF}), ByteArray.create("255", 1));
        assertEquals(ByteArray.create(new byte[] { (byte)0xFF, (byte)0xFF }), ByteArray.create("65535", 2));
        //
        assertEquals(4, ByteArray.create("5000", 4).getSize());
        assertEquals(5000, ByteArray.create("5000", 4).toInt());
        //
        assertEquals(4, ByteArray.create("5000000", 4).getSize());
        assertEquals(5000000, ByteArray.create("5000000", 4).toInt());
    }

    @Test
    public void shouldReturnBigInteger() throws Exception {
        assertEquals(new BigInteger("255"), ByteArray.create(new byte[]{ (byte)0xFF }).toBigInteger());
        assertEquals(new BigInteger("65535"), ByteArray.create(new byte[]{ (byte)0xFF, (byte)0xFF }).toBigInteger());
    }

    @Test
    public void shouldReturnSize() throws Exception {
        assertEquals(1, ByteArray.create(new byte[]{1}).getSize());
        assertEquals(2, ByteArray.create(new byte[]{1, 2}).getSize());
        assertEquals(3, ByteArray.create(new byte[]{1, 2, 3}).getSize());
    }

    @Test
    public void shouldReturnNumber() throws Exception {
        assertEquals(Byte.class, ByteArray.create(new byte[] { 10 }).toNumber(1).getClass());
        assertEquals(Short.class, ByteArray.create(new byte[] { 10, 10 }).toNumber(2).getClass());
        assertEquals(Integer.class, ByteArray.create(new byte[] { 10, 10, 10, 10 }).toNumber(4).getClass());
        assertEquals(Long.class, ByteArray.create(new byte[] { 10, 10, 10, 10, 10, 10, 10, 10 }).toNumber(8).getClass());
    }

    @Test
    public void shouldReturnByte() throws Exception {
        assertEquals(10, ByteArray.create(new byte[] { 10 }).toByte());
        assertEquals(0, ByteArray.create(new byte[] { }).toByte());
    }

    @Test
    public void shouldReturnShort() throws Exception {
        assertEquals(2570, ByteArray.create(new byte[] { 10, 10 }).toShort());
        assertEquals(10, ByteArray.create(new byte[] { 10 }).toShort());
    }

    @Test
    public void shouldReturnLong() throws Exception {
        assertEquals(723401728380766730L, ByteArray.create(new byte[] { 10, 10, 10, 10, 10, 10, 10, 10 }).toLong());
        assertEquals(168430090L, ByteArray.create(new byte[] { 10, 10, 10, 10 }).toLong());
    }

    @Test
    public void shouldReturnInt() throws Exception {
        assertEquals(168430090, ByteArray.create(new byte[] { 10, 10, 10, 10 }).toInt());
        assertEquals(2570, ByteArray.create(new byte[] { 10, 10 }).toInt());
    }

    @Test
    public void shouldReturnUInt() throws Exception {
        assertEquals(168430090, ByteArray.create(new byte[] { 10, 10, 10, 10 }).toUInt());
    }

    @Test
    public void shouldReturnToString() throws Exception {
        assertEquals("0x0A", ByteArray.create(new byte[] { 10 }).toString());
        assertEquals("0x0A0A", ByteArray.create(new byte[] { 10, 10 }).toString());
        assertEquals("0x0A0A0A", ByteArray.create(new byte[] { 10, 10, 10 }).toString());
        assertEquals("0x0A0A0A0A", ByteArray.create(new byte[] { 10, 10, 10, 10 }).toString());
    }

    @Test
    public void testToHexString() {
        assertEquals("0xFF", ByteArray.create(new byte[] { (byte)0xFF }).toHexString());
        assertEquals("0x00FF", ByteArray.create(new byte[] { (byte)0x00, (byte)0xFF }).toHexString());
        assertEquals("0x00FF00", ByteArray.create(new byte[] { (byte)0x00, (byte)0xFF, (byte)0x00 }).toHexString());
        assertEquals("0x00FF00FF00FF00FF", ByteArray.create(new byte[] { (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0xFF, }).toHexString());
        assertEquals("0xFFFFFFFFFFFFFFFF", ByteArray.create(new byte[] { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, }).toHexString());
        assertEquals("0x0000000000000000", ByteArray.create(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,}).toHexString());
    }

    @Test
    public void shouldReturnLittleEndian() throws Exception {
        assertArrayEquals(new byte[]{4, 3, 2, 1}, ByteArray.create(new byte[]{1, 2, 3, 4}).toLittleEndian());
    }

    @Test
    public void testIsHighBitArray() {
        assertTrue(ByteArray.create(new byte[] { 1 }).isHighBit(0));
        assertTrue(ByteArray.create(new byte[] { 2 }).isHighBit(1));
        assertFalse(ByteArray.create(new byte[] { 2 }).isHighBit(0));
        assertTrue(ByteArray.create(new byte[] { 1, 0 }).isHighBit(8));
        assertTrue(ByteArray.create(new byte[] { 2, 0 }).isHighBit(9));
    }
}
