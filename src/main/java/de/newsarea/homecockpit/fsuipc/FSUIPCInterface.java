package de.newsarea.homecockpit.fsuipc;

import java.net.ConnectException;

import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetEventListener;

public interface FSUIPCInterface {
	
	public void open() throws ConnectException;
	/* */
	public void monitor(OffsetIdent[] offsetIdents);
	public void monitor(OffsetIdent offsetIdent);
	/* */
	public void write(OffsetItem[] offsetItems);
	public void write(OffsetItem offsetItem);
	/* */
	public byte[] read(OffsetIdent offsetIdent);
	/* */
	public void close();	
	/* */
	public void addEventListener(OffsetEventListener valueEventListener);

}
