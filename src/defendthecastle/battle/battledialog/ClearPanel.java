package defendthecastle.battle.battledialog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JScrollPane;

import commonclass.CommonJPanel;
import defaultdata.Difficulty;
import defaultdata.Stage;
import defendthecastle.battle.InternalData.BattleEnemy;
import defendthecastle.battle.InternalData.BattleFacility;
import defendthecastle.battle.InternalData.BattleUnit;
import defendthecastle.battle.InternalData.GameData;

//ゲームクリア時の処理
class ClearPanel extends CommonJPanel{
	private final PauseDialog pauseDialog;
	private final JButton okButton = new JButton();
	private final JScrollPane meritScroll = new JScrollPane();
	private final Font buttonFont = new Font("ＭＳ ゴシック", Font.BOLD, 20);
	private final Font endFont = new Font("Arial", Font.BOLD|Font.ITALIC, 150);
	private final BasicStroke stroke = new BasicStroke(10);
	private final String clear = "CLEAR";
	private final float x = 50;
	private final float y = 150;
	
	ClearPanel(PauseDialog pauseDialog, Stage stage, BattleUnit[] unitMainData, BattleUnit[] unitLeftData, BattleFacility[] facilityData, BattleEnemy[] enemyData, GameData gameData, Difficulty difficulty) {
		this.pauseDialog = pauseDialog;
		setButton(okButton, "OK", 240, 320, 150, 60, buttonFont, this::okButtonAction);
		setScroll(meritScroll, 50, 170, 530, 140, createClearMerit(stage, unitMainData, unitLeftData, facilityData, enemyData, gameData, difficulty));
		stillness(brown());
	}
	
	void okButtonAction(ActionEvent e) {
		pauseDialog.disposeDialog();
	}
	
	GameClearPanel createClearMerit(Stage stage, BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData, Difficulty difficulty) {
		return new GameClearPanel(stage, UnitMainData, UnitLeftData, FacilityData, EnemyData, GameData, difficulty);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(endFont);
		g2d.setColor(Color.RED);
		g2d.setStroke(stroke);
		g2d.draw(endFont.createGlyphVector(g2d.getFontRenderContext(), clear).getOutline(x, y));
		g2d.setColor(Color.YELLOW);
		g2d.drawString(clear, x, y);
	}
}
