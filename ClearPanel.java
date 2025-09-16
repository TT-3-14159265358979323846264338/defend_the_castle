package battle;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JPanel;

//ゲームクリア時の処理
class ClearPanel extends JPanel{
	private JButton OKButton = new JButton();
	
	
	
	
	protected ClearPanel(PauseDialog PauseDialog, BattleUnit[] UnitMainData, BattleUnit[] UnitLeftData, BattleFacility[] FacilityData, BattleEnemy[] EnemyData, GameData GameData) {
		addOKButton(PauseDialog);
		
		
		
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setOKButton();
		clearComment(g);
		
		
		
		
		
		
		requestFocus();
	}
	
	private void addOKButton(PauseDialog PauseDialog) {
		add(OKButton);
		OKButton.addActionListener(e->{
			PauseDialog.disposeDialog();
		});
		OKButton.setText("OK");
	}
	
	private void setOKButton() {
		OKButton.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
		OKButton.setBounds(200, 320, 150, 60);
	}
	
	private void clearComment(Graphics g) {
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
