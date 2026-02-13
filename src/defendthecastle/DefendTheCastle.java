package defendthecastle;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import savedata.FileCheck;

public class DefendTheCastle implements WindowListener{
	private ScheduledExecutorService scheduler;
	
	void main() {
		scheduler = createScheduler();
		var fileCheck = createFileCheck();
		fileCheck.createSQL();
		var mainFrame = createMainFrame();
		mainFrame.addWindowListener(this);
		mainFrame.mainMenuDraw();
	}
	
	FileCheck createFileCheck() {
		return new FileCheck();
	}
	
	MainFrame createMainFrame() {
		return new MainFrame(createScheduler());
	}

	ScheduledExecutorService createScheduler() {
		return Executors.newScheduledThreadPool(100);
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
	}
	@Override
	public void windowClosing(WindowEvent e) {
		scheduler.shutdownNow();
	}
	@Override
	public void windowClosed(WindowEvent e) {
	}
	@Override
	public void windowIconified(WindowEvent e) {
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
	}
	@Override
	public void windowActivated(WindowEvent e) {
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
	}
}