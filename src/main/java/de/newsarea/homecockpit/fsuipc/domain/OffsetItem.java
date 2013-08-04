package de.newsarea.homecockpit.fsuipc.domain;

import de.newsarea.homecockpit.fsuipc.util.DataTypeUtil;


public class OffsetItem extends OffsetIdent {
	
	private byte[] value;
	
	public byte[] getValue() {
		return this.value;
	}

	public OffsetItem(int offset, int size, byte[] value) {
		super(offset, size);
		this.value = value;
	}
	
	@Override
	public String toString() {
		StringBuffer strBld = new StringBuffer();
		strBld.append(super.toString());
		strBld.append(" : ");
		strBld.append(DataTypeUtil.toString(this.getValue()));
		strBld.append(" (");
		strBld.append(DataTypeUtil.toHexString(this.getValue()));
		strBld.append(")");
		return strBld.toString();
	}

}
