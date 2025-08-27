package battle;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.JDialog;

import defaultdata.stage.StageData;
import defendthecastle.MainFrame;

//一時停止中の画面
class PauseDialog extends JDialog implements WindowListener{
	private Battle Battle;
	
	protected PauseDialog(Battle Battle, StageData StageData, List<Boolean> clearMerit) {
		setDialog(Battle);
		setTitle("戦功");
		setSize(435, 255);
		setLocationRelativeTo(null);
		add(new MeritPanel(this, StageData, clearMerit));
		setVisible(true);
	}
	
	protected PauseDialog(Battle Battle) {
		setDialog(Battle);
		setTitle("一時停止");
		setSize(285, 140);
		setLocationRelativeTo(null);
		add(new PausePanel(this));
		setVisible(true);
	}
	
	protected PauseDialog(Battle Battle, MainFrame MainFrame, StageData StageData, List<Boolean> clearMerit, int difficultyCode) {
		setDialog(Battle);
		setTitle("降参/再戦");
		setSize(415, 140);
		setLocationRelativeTo(null);
		add(new ReturnPanel(this, Battle, MainFrame, StageData, clearMerit, difficultyCode));
		setVisible(true);
	}
	
	private void setDialog(Battle Battle) {
		this.Battle = Battle;
		addWindowListener(this);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
	}
	
	protected void disposeDialog() {
		dispose();
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}
	@Override
	public void windowClosing(WindowEvent e) {
	}
	@Override
	public void windowClosed(WindowEvent e) {
		Battle.timerRestart();
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