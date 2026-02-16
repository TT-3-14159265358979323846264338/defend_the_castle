package defendthecastle.battle.battledialog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import defaultdata.stage.StageData;
import defendthecastle.battle.BattleEnemy;
import defendthecastle.battle.BattleFacility;
import defendthecastle.battle.BattleUnit;
import defendthecastle.battle.GameData;

//ゲームクリア時の処理
class ClearPanel extends JPanel{
	private JButton OKButton = new JButton();
	private JScrollPane meritScroll = new JScrollPane();
	
	ClearPanel(PauseDialog PauseDialog, StageData StageData, BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData, double difficultyCorrection) {
		setBackground(new Color(240, 170, 80));
		addOKButton(PauseDialog);
		addMeritScroll(StageData, UnitMainData, UnitLeftData, FacilityData, EnemyData, GameData, difficultyCorrection);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setOKButton();
		setMeritScroll();
		clearComment(g);
		requestFocus();
	}
	
	void addOKButton(PauseDialog PauseDialog) {
		add(OKButton);
		OKButton.addActionListener(_ ->{
			PauseDialog.disposeDialog();
		});
		OKButton.setText("OK");
	}
	
	void setOKButton() {
		OKButton.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
		OKButton.setBounds(240, 320, 150, 60);
	}
	
	void addMeritScroll(StageData StageData, BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData, double difficultyCorrection) {
		meritScroll.getViewport().setView(new ClearMerit(StageData, UnitMainData, UnitLeftData, FacilityData, EnemyData, GameData, difficultyCorrection));
		add(meritScroll);
	}
	
	void setMeritScroll() {
		meritScroll.setBounds(50, 170, 530, 140);
		meritScroll.setPreferredSize(meritScroll.getSize());
	}
	
	void clearComment(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Font endFont = new Font("Aria", Font.BOLD|Font.ITALIC, 150);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(endFont);
		g2d.setColor(Color.RED);
		g2d.setStroke(new BasicStroke(10));
		g2d.draw(endFont.createGlyphVector(g2d.getFontRenderContext(),"CLEAR").getOutline(50, 150));
		g2d.setColor(Color.YELLOW);
		g2d.drawString("CLEAR", 50, 150);
	}
}
