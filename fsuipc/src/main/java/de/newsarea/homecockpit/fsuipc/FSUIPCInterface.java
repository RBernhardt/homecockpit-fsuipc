package de.newsarea.homecockpit.fsuipc;

import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetCollectionEventListener;
import de.newsarea.homecockpit.fsuipc.event.OffsetEventListener;

import java.net.ConnectException;

public interface FSUIPCInterface {
	
	void open() throws ConnectException;

	void monitor(OffsetIdent offsetIdent);

	void write(OffsetItem[] offsetItems);
	void write(OffsetItem offsetItem);

    void toggleBit(int offset, int size, byte byteIdx);

	OffsetItem read(OffsetIdent offsetIdent);

	void close();

	void addEventListener(OffsetEventListener offsetEventListener);

    void addEventListener(OffsetCollectionEventListener offsetCollectionEventListener);

}
