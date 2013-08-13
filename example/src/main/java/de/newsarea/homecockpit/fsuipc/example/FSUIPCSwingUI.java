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

public class FSUIPCSwingUI {

    private FSUIPCInterface fsuipcInterface;
    private JLabel lLngLabel;
    private JLabel lLatLabel;
    private JLabel lAltLabel;

    public FSUIPCSwingUI(FSUIPCInterface fsuipcInterface) {
        this.fsuipcInterface = fsuipcInterface;
    }

    public void initialize() {
        JFrame frame = new JFrame();
        frame.setSize(250, 150);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // add latitude
        frame.add(createTitleLabel("Longitude:", 10));
        lLngLabel = createValueLabel("0.0", 10);
        frame.add(lLngLabel);
        // add longitude
        frame.add(createTitleLabel("Latitude:", 30));
        lLatLabel = createValueLabel("0.0", 30);
        frame.add(lLatLabel);
        // add altitude
        frame.add(createTitleLabel("Altitude:", 50));
        lAltLabel = createValueLabel("0", 50);
        frame.add(lAltLabel);
        // add pause hint
        frame.add(createTitleLabel("PRESS [P] TO PAUSE", 100));
        // ~
        frame.addKeyListener(new KeyListener() {
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
        frame.setVisible(true);
        // ~
        fsuipcInterface.addEventListener(new OffsetEventListener() {
            @Override
            public void offsetValueChanged(OffsetItem offsetItem) {
                handleOffsetValueChanged(offsetItem);
            }
        });
    }

    /***
     * Offset: 0x0262; Size: 2;
     */
    private void togglePause() {
        OffsetItem pauseOffsetItem = fsuipcInterface.read(new OffsetIdent(0x0262, 2));
        int pauseOffsetValue = pauseOffsetItem.getValue().toByte();
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
            case 0x0568: // longitude
                lLngLabel.setText(String.valueOf(FSUIPCUtil.toLongitude(offsetItem.getValue().toLong())));
                break;
            case 0x0560: // latitude
                lLatLabel.setText(String.valueOf(FSUIPCUtil.toLatitude(offsetItem.getValue().toLong())));
                break;
            case 0x0570: // altitude
                lAltLabel.setText(String.valueOf(FSUIPCUtil.toAlititude(offsetItem.getValue().toLong())));
                break;
            default:
                // do nothing
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
        label.setSize(100, 20);
        return label;
    }

}
