package de.newsarea.homecockpit.fsuipc.example;

import de.newsarea.homecockpit.fsuipc.FSUIPCInterface;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetEventListener;
import de.newsarea.homecockpit.fsuipc.util.FSUIPCUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;

class FSUIPCSwingUI extends JFrame {

    private final FSUIPCInterface fsuipcInterface;
    private JLabel lLatLabel;
    private JLabel lLngLabel;
    private JLabel lAltLabel;

    public FSUIPCSwingUI(FSUIPCInterface fsuipcInterface) {
        this.fsuipcInterface = fsuipcInterface;
    }

    public void initialize() {
        setTitle("FSUIPC Example");
        setSize(300, 160);
        setLayout(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // add latitude
        add(createTitleLabel("Latitude:", 30));
        lLatLabel = createValueLabel("0.0", 30);
        add(lLatLabel);
        // add longitude
        add(createTitleLabel("Longitude:", 10));
        lLngLabel = createValueLabel("0.0", 10);
        add(lLngLabel);
        // add altitude
        add(createTitleLabel("Altitude:", 50));
        lAltLabel = createValueLabel("0", 50);
        add(lAltLabel);
        // add pause hint
        add(createTitleLabel("PRESS [P] TO PAUSE", 100));
        // ~
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == 'P') {
                    togglePause();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) { }
        });
        // ~
        initializeFSUIPCInterface();
    }

    private void initializeFSUIPCInterface() {
        fsuipcInterface.addEventListener(new OffsetEventListener() {
            @Override
            public void offsetValueChanged(OffsetItem offsetItem) {
                handleOffsetValueChanged(offsetItem);
            }

            @Override
            public void offsetValuesChanged(Collection<OffsetItem> offsetItemCollection) {
                // do nothing
            }
        });
        fsuipcInterface.monitor(new OffsetIdent(0x0560, 8)); // latitude
        fsuipcInterface.monitor(new OffsetIdent(0x0568, 8)); // longitude
        fsuipcInterface.monitor(new OffsetIdent(0x0570, 8)); // altitude
    }

    /***
     * Offset: 0x0262; Size: 2;
     */
    private void togglePause() {
        OffsetItem pauseOffsetItem = fsuipcInterface.read(new OffsetIdent(0x0262, 2));
        int pauseOffsetValue = pauseOffsetItem.getValue().toShort();
        switch(pauseOffsetValue) {
            case 0:
                fsuipcInterface.write(new OffsetItem(0x0262, 2, ByteArray.create("1", 2)));
                break;
            case 1:
                fsuipcInterface.write(new OffsetItem(0x0262, 2, ByteArray.create("0", 2)));
                break;
            default:
                throw new IllegalStateException("invalid value - " + pauseOffsetValue);
        }
    }

    private void handleOffsetValueChanged(OffsetItem offsetItem) {
        switch (offsetItem.getOffset()) {
            case 0x0560: // latitude
                lLatLabel.setText(String.valueOf(FSUIPCUtil.toLatitude(offsetItem.getValue().toLong())));
                break;
            case 0x0568: // longitude
                lLngLabel.setText(String.valueOf(FSUIPCUtil.toLongitude(offsetItem.getValue().toLong())));
                break;
            case 0x0570: // altitude
                lAltLabel.setText(String.valueOf(FSUIPCUtil.toAlititude(offsetItem.getValue().toLong())));
                break;
            default: // do nothing
                break;
        }
    }

    private JLabel createTitleLabel(String title, int y) {
        JLabel label = new JLabel(title);
        label.setLocation(10, y);
        label.setSize(250, 20);
        return label;
    }

    private JLabel createValueLabel(String defaultValue, int y) {
        JLabel label = new JLabel(defaultValue);
        label.setLocation(100, y);
        label.setSize(200, 20);
        return label;
    }

}
