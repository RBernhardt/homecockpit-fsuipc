package de.newsarea.homecockpit.fsuipc.event;

import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;

import java.util.Collection;

public interface OffsetEventListener {
	
	void offsetValueChanged(OffsetItem offsetItem);
    void offsetValuesChanged(Collection<OffsetItem> offsetItemCollection);

}
