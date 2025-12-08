package defendthecastle;

import javax.swing.JFrame;

import battle.Battle;
import defaultdata.stage.StageData;
import defendthecastle.composition.MenuComposition;
import defendthecastle.itemdispose.MenuItemDispose;
import defendthecastle.itemget.MenuItemGet;
import defendthecastle.selectstage.MenuSelectStage;

//メイン画面切り替え
public class MainFrame extends JFrame{
	MainFrame() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
	}
	
	public void mainMenuDraw() {
		getContentPane().removeAll();
		add(new MenuMain(this));
		setTitle("メインメニュー");
		setSize(585, 510);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	void itemGetMenuDraw() {
		getContentPane().removeAll();
		add(new MenuItemGet(this));
		setTitle("ガチャ");
		setSize(585, 510);
		setLocationRelativeTo(null);
	}
	
	void itemDisposeMenuDraw() {
		getContentPane().removeAll();
		add(new MenuItemDispose(this));
		setTitle("リサイクル");
		setSize(715, 640);
		setLocationRelativeTo(null);
	}
	
	void compositionDraw() {
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
	
	public void battleDraw(StageData StageData, double difficultyCorrection) {
		getContentPane().removeAll();
		add(new Battle(this, StageData, difficultyCorrection));
		setTitle(StageData.getName());
		setSize(1235, 600);
		setLocationRelativeTo(null);
	}
}