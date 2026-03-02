package defendthecastle.battle.battledialog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;

import javax.swing.JButton;

import commonclass.CommonJPanel;

//ゲームオーバー時の処理
class GameOverPanel extends CommonJPanel{
	private final PauseDialog pauseDialog;
	private final JButton okButton = new JButton();
	private final Font buttonFont = new Font("ＭＳ ゴシック", Font.BOLD, 20);
	private final Font endFont = new Font("Aria", Font.BOLD|Font.ITALIC, 150);
	private final BasicStroke stroke = new BasicStroke(10);
	private final String game = "GAME";
	private final String over = "OVER";
	private final float x = 50;
	private final float gameY = 150;
	private final float overY = 300;
	
	GameOverPanel(PauseDialog pauseDialog) {
		this.pauseDialog = pauseDialog;
		setButton(okButton, "OK", 200, 320, 150, 60, buttonFont, this::okButtonAction);
		stillness(brown());
	}
	
	void okButtonAction(ActionEvent e) {
		pauseDialog.disposeDialog();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(endFont);
		g2d.setColor(Color.BLACK);
		g2d.setStroke(stroke);
		g2d.draw(comment(g2d, game, gameY));
		g2d.draw(comment(g2d, over, overY));
		g2d.setColor(Color.RED);
		g2d.drawString(game, x, gameY);
		g2d.drawString(over, x, overY);
	}
	
	Shape comment(Graphics2D g2d, String comment, float y) {
		return endFont.createGlyphVector(g2d.getFontRenderContext(), comment).getOutline(x, y);
	}
}
