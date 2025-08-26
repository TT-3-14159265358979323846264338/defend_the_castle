package defendthecastle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import battle.Battle;
import defaultdata.stage.StageData;
import defendthecastle.composition.MenuComposition;
import defendthecastle.itemdispose.MenuItemDispose;
import defendthecastle.itemget.MenuItemGet;
import defendthecastle.selectstage.MenuSelectStage;

//メイン画面切り替え
public class MainFrame extends JFrame{
	protected MainFrame() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		mainMenuDraw();
	}
	
	public void mainMenuDraw() {
		getContentPane().removeAll();
		add(new MenuMain(this));
		setTitle("メインメニュー");
		setSize(585, 510);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	protected void itemGetMenuDraw() {
		getContentPane().removeAll();
		add(new MenuItemGet(this));
		setTitle("ガチャ");
		setSize(585, 510);
		setLocationRelativeTo(null);
	}
	
	protected void itemDisposeMenuDraw() {
		getContentPane().removeAll();
		add(new MenuItemDispose(this));
		setTitle("リサイクル");
		setSize(715, 640);
		setLocationRelativeTo(null);
	}
	
	protected void compositionDraw() {
		getContentPane().removeAll();
		add(new MenuComposition(this));
		setTitle("ユニット編成");
		setSize(975, 570);
		setLocationRelativeTo(null);
	}
	
	public void selectStageDraw() {
		getContentPane().removeAll();
		add(new MenuSelectStage(this));
		setTitle("ステージ選択");
		setSize(925, 570);
		setLocationRelativeTo(null);
	}
	
	public void battleDraw(StageData StageData, List<Boolean> clearMerit, int difficultyCode) {
		getContentPane().removeAll();
		add(new Battle(this, StageData, clearMerit, difficultyCode));
		setTitle(StageData.getName());
		setSize(1235, 600);
		setLocationRelativeTo(null);
	}
}

//ロード画面
class LoadFrame extends JFrame{
	private Dimension size;
	
	protected LoadFrame(Dimension size) {
		this.size = size;
	}
	
	protected void frame() {
		setTitle("ロード");
		setSize(size);
		add(new LoadPanel(size));
		setLocationRelativeTo(null);
	}
}

class LoadPanel extends JPanel implements ActionListener{
	private JLabel load = new JLabel();
	private Dimension size;
	private Timer timer = new Timer(100, this);
	private int alpha = 255;
	private boolean canDecrease;
	
	protected LoadPanel(Dimension size) {
		setBackground(Color.BLACK);
		this.size = size;
		load.setText("ロード中");
		add(load);
		timer.start();
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		load.setBounds((int) (size.getWidth() - 200), (int) (size.getHeight() - 100), 400, 40);
		load.setForeground(new Color(255, 0, 0, alpha));
		load.setFont(new Font("Arail", Font.BOLD, 30));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(alpha <= 100 || 255 <= alpha) {
			canDecrease = canDecrease? false: true;
		}
		if(canDecrease) {
			alpha -= 10;
		}else {
			alpha += 10;
		}
	}
}