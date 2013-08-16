package de.newsarea.homecockpit.fsuipc.event;

import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;

public interface OffsetEventListener {
	
	void valueChanged(OffsetItem offsetItem);

}
