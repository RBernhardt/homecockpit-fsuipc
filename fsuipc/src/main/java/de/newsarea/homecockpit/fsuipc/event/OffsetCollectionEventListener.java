package de.newsarea.homecockpit.fsuipc.event;

import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;

import java.util.Collection;

public interface OffsetCollectionEventListener {

    void valuesChanged(Collection<OffsetItem> offsetItemCollection);

}
