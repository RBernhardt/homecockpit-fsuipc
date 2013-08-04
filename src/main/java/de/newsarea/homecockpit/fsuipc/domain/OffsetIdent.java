package de.newsarea.homecockpit.fsuipc.domain;

import de.newsarea.homecockpit.fsuipc.util.DataTypeUtil;


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
	
	public String toString() {
		StringBuffer strBld = new StringBuffer();
		strBld.append(DataTypeUtil.toHexString(this.offset));
		strBld.append(" (");
		strBld.append(this.getOffset());
		strBld.append(")");
		strBld.append(" : ");
		strBld.append(this.getSize());
		return strBld.toString();
	}

}
