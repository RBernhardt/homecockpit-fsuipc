package de.newsarea.homecockpit.fsuipc;

import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetCollectionEventListener;
import de.newsarea.homecockpit.fsuipc.event.OffsetEventListener;

import java.io.IOException;
import java.net.ConnectException;

public interface FSUIPCInterface {
	
	void open() throws ConnectException;

    boolean isConnectionEstablished();

	void monitor(OffsetIdent offsetIdent);

	void write(OffsetItem offsetItem) throws IOException;
    void write(OffsetItem offsetItem, int timeOfBlocking) throws IOException;

	OffsetItem read(OffsetIdent offsetIdent) throws IOException;

	void close();

	void addEventListener(OffsetEventListener offsetEventListener);

    void addEventListener(OffsetCollectionEventListener offsetCollectionEventListener);

}
