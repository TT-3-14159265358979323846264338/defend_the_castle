package battle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;

//ユニット用JButtonの編集
class UnitButton extends JButton{
	protected UnitButton() {
		setPreferredSize(new Dimension(60, 30));
		setContentAreaFilled(false);
	}
	
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		if(getModel().isArmed()) {
			g2.setPaint(new GradientPaint(25.0f, 5.0f, Color.LIGHT_GRAY, 30.0f, 20.0f, Color.GRAY));
		}else {
			g2.setPaint(new GradientPaint(25.0f, 5.0f, Color.YELLOW, 30.0f, 20.0f, Color.ORANGE));
		}
		g2.fillOval(0, 0, getSize().width, getSize().height);
		super.paintComponent(g2);
	}
	
	protected void paintBorder(Graphics g) {
		g.drawOval(0, 0, getSize().width, getSize().height);
	}
}