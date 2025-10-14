package battle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;

class AwakeLabel extends JLabel{
	protected AwakeLabel() {
		setPreferredSize(new Dimension(40, 20));
		setText("覚醒可能");
		setFont(new Font("ＭＳ ゴシック", Font.BOLD, 7));
		setHorizontalAlignment(JLabel.CENTER);
		setForeground(Color.RED);
	}
	
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setPaint(new GradientPaint(15.0f, 10.0f, Color.PINK, 25.0f, 30.0f, Color.MAGENTA));
		g2.fillOval(0, 0, getSize().width, getSize().height);
		super.paintComponent(g2);
	}
	
	protected void paintBorder(Graphics g) {
		g.drawOval(0, 0, getSize().width, getSize().height);
	}
}