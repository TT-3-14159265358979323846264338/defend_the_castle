package defendthecastle;

import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JFrame;

import defaultdata.stage.StageData;
import defendthecastle.battle.Battle;
import defendthecastle.composition.MenuComposition;
import defendthecastle.itemdispose.MenuItemDispose;
import defendthecastle.itemget.MenuItemGet;
import defendthecastle.selectstage.MenuSelectStage;

//メイン画面切り替え
public class MainFrame extends JFrame{
	private ScheduledExecutorService scheduler;
	
	MainFrame(ScheduledExecutorService scheduler) {
		this.scheduler = scheduler;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
	}
	
	public void mainMenuDraw() {
		getContentPane().removeAll();
		add(createMenuMain());
		setTitle("メインメニュー");
		setSize(585, 510);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	MenuMain createMenuMain() {
		return new MenuMain(this, scheduler);
	}
	
	void itemGetMenuDraw() {
		getContentPane().removeAll();
		add(createMenuItemGet());
		setTitle("ガチャ");
		setSize(585, 510);
		setLocationRelativeTo(null);
	}
	
	MenuItemGet createMenuItemGet() {
		return new MenuItemGet(this, scheduler);
	}
	
	void itemDisposeMenuDraw() {
		getContentPane().removeAll();
		add(createMenuItemDispose());
		setTitle("リサイクル");
		setSize(715, 640);
		setLocationRelativeTo(null);
	}
	
	MenuItemDispose createMenuItemDispose() {
		return new MenuItemDispose(this, scheduler);
	}
	
	void compositionDraw() {
		getContentPane().removeAll();
		add(createMenuComposition());
		setTitle("ユニット編成");
		setSize(975, 570);
		setLocationRelativeTo(null);
	}
	
	MenuComposition createMenuComposition() {
		return new MenuComposition(this, scheduler);
	}
	
	public void selectStageDraw() {
		getContentPane().removeAll();
		add(createMenuSelectStage());
		setTitle("ステージ選択");
		setSize(925, 570);
		setLocationRelativeTo(null);
	}
	
	MenuSelectStage createMenuSelectStage() {
		return new MenuSelectStage(this, scheduler);
	}
	
	public void battleDraw(StageData StageData, double difficultyCorrection) {
		getContentPane().removeAll();
		add(createBattle(StageData, difficultyCorrection));
		setTitle(StageData.getName());
		setSize(1235, 600);
		setLocationRelativeTo(null);
	}
	
	Battle createBattle(StageData StageData, double difficultyCorrection) {
		return new Battle(this, scheduler, StageData, difficultyCorrection);
	}
}