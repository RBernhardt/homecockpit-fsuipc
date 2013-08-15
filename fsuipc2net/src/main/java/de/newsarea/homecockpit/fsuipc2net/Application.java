package de.newsarea.homecockpit.fsuipc2net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Application {

    private static final String SPRING_XML_FILES = "spring/fsuipc2net-context.xml";

	private static final Logger log = LoggerFactory.getLogger(Application.class);
	
	private FSUIPCServer fsuipcServer;
	private TrayIcon trayIcon;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Application app = new Application();
		app.start();
	}
	
	public void start() throws Exception {
		showTrayIcon();
		//
        ApplicationContext appCtx = new ClassPathXmlApplicationContext(SPRING_XML_FILES);
        fsuipcServer = (FSUIPCServer) appCtx.getBean("fsuipcServer");
		fsuipcServer.start();
	}
	
	public void stop() {
		SystemTray.getSystemTray().remove(trayIcon);
		fsuipcServer.stop();
	}
	
	private void showTrayIcon() {		
		if (!SystemTray.isSupported()) { return; }
		//
		Image iTrayIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/tray.gif"));
		trayIcon = new TrayIcon(iTrayIcon, "HomeCockpit FSUIPC2Net");
		//
		PopupMenu popupMenu = new PopupMenu();
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stop();				
			}
		});
		popupMenu.add(exitItem);
		//
		trayIcon.setPopupMenu(popupMenu);
		trayIcon.setImageAutoSize(true);
		trayIcon.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				//trayIcon.displayMessage("Tester!", "Some action performed", TrayIcon.MessageType.INFO);
			}
			
		});
		//
		try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException e) {
			log.error("TrayIcon could not be added.", e);
		}
	}

}
