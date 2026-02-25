package defendthecastle.battle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;

class AwakeLabel extends JLabel{
	private final Font font = new Font("ＭＳ ゴシック", Font.BOLD, 7);
	private final Dimension dimension = new Dimension(40, 20);
	private final GradientPaint paint = new GradientPaint(15.0f, 10.0f, Color.PINK, 25.0f, 30.0f, Color.MAGENTA);
	
	AwakeLabel() {
		setText("覚醒可能");
		setFont(font);
		setPreferredSize(dimension);
		setHorizontalAlignment(JLabel.CENTER);
		setForeground(Color.RED);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setPaint(paint);
		g2.fillOval(0, 0, getSize().width, getSize().height);
		super.paintComponent(g2);
	}
	
	@Override
	protected void paintBorder(Graphics g) {
		g.drawOval(0, 0, getSize().width, getSize().height);
	}
}