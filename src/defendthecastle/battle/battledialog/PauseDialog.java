package defendthecastle.battle.battledialog;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Objects;

import javax.swing.JDialog;

import defaultdata.Difficulty;
import defaultdata.Stage;
import defendthecastle.MainFrame;
import defendthecastle.battle.InternalData.BattleEnemy;
import defendthecastle.battle.InternalData.BattleFacility;
import defendthecastle.battle.InternalData.BattleUnit;
import defendthecastle.battle.InternalData.GameData;
import defendthecastle.battle.InternalData.GameTimer;

//一時停止中の画面
public class PauseDialog extends JDialog implements WindowListener{
	private GameTimer gameTimer;
	
	public PauseDialog(GameTimer gameTimer, MainFrame MainFrame, Stage stage, Difficulty difficulty) {
		setTitle("一時停止");
		setSize(545, 615);
		setDialog(gameTimer);
		setLocationRelativeTo(null);
		add(new ReturnPanel(this, MainFrame, stage, difficulty, gameTimer));
		setVisible(true);
	}
	
	public PauseDialog(Stage stage, BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData, Difficulty difficulty) {
		setTitle("戦績");
		setSize(645, 425);
		setDialog(null);
		setLocationRelativeTo(null);
		add(new ClearPanel(this, stage, UnitMainData, UnitLeftData, FacilityData, EnemyData, GameData, difficulty));
		setVisible(true);
	}
	
	public PauseDialog() {
		setTitle("敗北");
		setSize(575, 425);
		setDialog(null);
		setLocationRelativeTo(null);
		add(new GameOverPanel(this));
		setVisible(true);
	}
	
	void setDialog(GameTimer gameTimer) {
		this.gameTimer = gameTimer;
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
		if(Objects.isNull(gameTimer)) {
			return;
		}
		gameTimer.timerRestart();
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