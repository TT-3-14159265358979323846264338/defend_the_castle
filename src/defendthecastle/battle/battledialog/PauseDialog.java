package defendthecastle.battle.battledialog;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Objects;

import javax.swing.JDialog;

import defaultdata.Stage;
import defendthecastle.MainFrame;
import defendthecastle.battle.Battle;
import defendthecastle.battle.BattleEnemy;
import defendthecastle.battle.BattleFacility;
import defendthecastle.battle.BattleUnit;
import defendthecastle.battle.GameData;

//一時停止中の画面
public class PauseDialog extends JDialog implements WindowListener{
	private Battle Battle;
	
	public PauseDialog(Battle Battle, MainFrame MainFrame, Stage stage, double difficultyCorrection) {
		setDialog(Battle);
		setTitle("一時停止");
		setSize(545, 615);
		setLocationRelativeTo(null);
		add(new ReturnPanel(this, Battle, MainFrame, stage, difficultyCorrection));
		setVisible(true);
	}
	
	public PauseDialog(Stage stage, BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData, double difficultyCorrection) {
		setDialog(null);
		setTitle("戦績");
		setSize(645, 425);
		setLocationRelativeTo(null);
		add(new ClearPanel(this, stage, UnitMainData, UnitLeftData, FacilityData, EnemyData, GameData, difficultyCorrection));
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