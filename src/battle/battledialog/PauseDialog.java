package battle.battledialog;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Objects;

import javax.swing.JDialog;

import battle.Battle;
import battle.BattleEnemy;
import battle.BattleFacility;
import battle.BattleUnit;
import battle.GameData;
import defaultdata.stage.StageData;
import defendthecastle.MainFrame;

//一時停止中の画面
public class PauseDialog extends JDialog implements WindowListener{
	private Battle Battle;
	
	public PauseDialog(Battle Battle, MainFrame MainFrame, StageData StageData, double difficultyCorrection) {
		setDialog(Battle);
		setTitle("一時停止");
		setSize(545, 615);
		setLocationRelativeTo(null);
		add(new ReturnPanel(this, Battle, MainFrame, StageData, difficultyCorrection));
		setVisible(true);
	}
	
	public PauseDialog(StageData StageData, BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData, double difficultyCorrection) {
		setDialog(null);
		setTitle("戦績");
		setSize(645, 425);
		setLocationRelativeTo(null);
		add(new ClearPanel(this, StageData, UnitMainData, UnitLeftData, FacilityData, EnemyData, GameData, difficultyCorrection));
		setVisible(true);
	}
	
	public PauseDialog() {
		setDialog(null);
		setTitle("敗北");
		setSize(575, 425);
		setLocationRelativeTo(null);
		add(new GameOverPanel(this));
		setVisible(true);
	}
	
	void setDialog(Battle Battle) {
		this.Battle = Battle;
		addWindowListener(this);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
	}
	
	void disposeDialog() {
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