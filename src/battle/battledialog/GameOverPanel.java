package battle.battledialog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JPanel;

//ゲームオーバー時の処理
class GameOverPanel extends JPanel{
	private JButton OKButton = new JButton();
	
	GameOverPanel(PauseDialog PauseDialog) {
		setBackground(new Color(240, 170, 80));
		addOKButton(PauseDialog);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setOKButton();
		gameOverComment(g);
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
		OKButton.setBounds(200, 320, 150, 60);
	}
	
	void gameOverComment(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Font endFont = new Font("Aria", Font.BOLD|Font.ITALIC, 150);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(endFont);
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(10));
		g2d.draw(endFont.createGlyphVector(g2d.getFontRenderContext(),"GAME").getOutline(50, 150));
		g2d.draw(endFont.createGlyphVector(g2d.getFontRenderContext(),"OVER").getOutline(50, 300));
		g2d.setColor(Color.RED);
		g2d.drawString("GAME", 50, 150);
		g2d.drawString("OVER", 50, 300);
	}
}
