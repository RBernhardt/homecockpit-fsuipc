package de.newsarea.homecockpit.fsuipc;

import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetCollectionEventListener;
import de.newsarea.homecockpit.fsuipc.event.OffsetEventListener;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

public interface FSUIPCInterface {
	
	void open() throws ConnectException;

	void monitor(OffsetIdent offsetIdent);

	void write(OffsetItem[] offsetItems);
	void write(OffsetItem offsetItem);
    void writeAndWaitForResetToZero(OffsetItem offsetItem) throws TimeoutException;

	OffsetItem read(OffsetIdent offsetIdent);

	void close();

	void addEventListener(OffsetEventListener offsetEventListener);

    void addEventListener(OffsetCollectionEventListener offsetCollectionEventListener);

}
