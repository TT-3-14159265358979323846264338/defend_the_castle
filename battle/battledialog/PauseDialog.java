package battle;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
import java.util.Objects;

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
	
	protected PauseDialog(Battle Battle, MainFrame MainFrame, StageData StageData, List<Boolean> clearMerit, double difficultyCorrection) {
		setDialog(Battle);
		setTitle("降参/再戦");
		setSize(415, 140);
		setLocationRelativeTo(null);
		add(new ReturnPanel(this, Battle, MainFrame, StageData, clearMerit, difficultyCorrection));
		setVisible(true);
	}
	
	protected PauseDialog(StageData StageData, BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData, double difficultyCorrection) {
		setDialog(null);
		setTitle("戦績");
		setSize(645, 425);
		setLocationRelativeTo(null);
		add(new ClearPanel(this, StageData, UnitMainData, UnitLeftData, FacilityData, EnemyData, GameData, difficultyCorrection));
		setVisible(true);
	}
	
	protected PauseDialog() {
		setDialog(null);
		setTitle("敗北");
		setSize(575, 425);
		setLocationRelativeTo(null);
		add(new GameOverPanel(this));
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
		if(Objects.isNull(Battle)) {
			return;
		}
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
