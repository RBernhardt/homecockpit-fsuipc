package de.newsarea.homecockpit.fsuipc.util;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

public class DataTypeUtilTest {
		
	@Test
	public void testToByteArrayString() {
		assertArrayEquals(new byte[] { 1 }, DataTypeUtil.toByteArray("1", 1));
		assertArrayEquals(new byte[] { (byte)0xFF }, DataTypeUtil.toByteArray("255", 1));
		assertArrayEquals(new byte[] { (byte)0xFF, (byte)0xFF }, DataTypeUtil.toByteArray(0xFFFF, 2));
		//
		assertEquals(4, DataTypeUtil.toByteArray("5000", 4).length);
		assertEquals(5000, DataTypeUtil.toInt(DataTypeUtil.toByteArray("5000", 4)));
		//
		assertEquals(4, DataTypeUtil.toByteArray("5000000", 4).length);
		assertEquals(5000000, DataTypeUtil.toInt(DataTypeUtil.toByteArray("5000000", 4)));
	}
	
	@Test
	public void testToInt() {
		assertEquals(1, DataTypeUtil.toInt(new byte[] { 0, 0, 0, 1 }));
		assertEquals(255, DataTypeUtil.toInt(new byte[] { 0, 0, 0, (byte)255 }));
		assertEquals(-16776961, DataTypeUtil.toInt(new byte[] { (byte)255, 0, 0, (byte)255 }));	
	}
	
	@Test 
	public void testIsHighBit() {
		assertTrue(DataTypeUtil.isHighBit((byte)1, (byte)0));
		assertTrue(DataTypeUtil.isHighBit((byte)2, (byte)1));
		assertTrue(DataTypeUtil.isHighBit((byte)3, (byte)0));
		assertTrue(DataTypeUtil.isHighBit((byte)3, (byte)1));
		assertFalse(DataTypeUtil.isHighBit((byte)4, (byte)0));
	}
	
	@Test 
	public void testIsHighBitArray() {
		assertTrue(DataTypeUtil.isHighBit(new byte[] { 1 }, 0));
		assertTrue(DataTypeUtil.isHighBit(new byte[] { 2 }, 1));
		assertFalse(DataTypeUtil.isHighBit(new byte[] { 2 }, 0));
		/* */
		assertTrue(DataTypeUtil.isHighBit(new byte[] { 1, 0 }, 8));
		assertTrue(DataTypeUtil.isHighBit(new byte[] { 2, 0 }, 9));
		assertTrue(DataTypeUtil.isHighBit(DataTypeUtil.toByteArray(3, 2), 1));
	}
	
	@Test
	public void testToHexString() {
		assertEquals("0xFF", DataTypeUtil.toHexString(new byte[] { (byte)0xFF }));
		assertEquals("0x00FF", DataTypeUtil.toHexString(new byte[] { (byte)0x00, (byte)0xFF }));
		assertEquals("0x00FF00", DataTypeUtil.toHexString(new byte[] { (byte)0x00, (byte)0xFF, (byte)0x00 }));
		assertEquals("0x00FF00FF00FF00FF", DataTypeUtil.toHexString(new byte[] { (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0xFF, }));
		assertEquals("0xFFFFFFFFFFFFFFFF", DataTypeUtil.toHexString(new byte[] { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, }));
		assertEquals("0x0000000000000000", DataTypeUtil.toHexString(new byte[] { (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, }));
	}
	
	@Test
	public void testToNumber() {
		assertEquals(Byte.class, DataTypeUtil.toNumber(String.valueOf(Byte.MAX_VALUE)).getClass());
		assertEquals(Short.class, DataTypeUtil.toNumber(String.valueOf(Short.MAX_VALUE)).getClass());
		assertEquals(Integer.class, DataTypeUtil.toNumber(String.valueOf(Integer.MAX_VALUE)).getClass());
		assertEquals(Long.class, DataTypeUtil.toNumber(String.valueOf(Long.MAX_VALUE)).getClass());
	}
	
}
