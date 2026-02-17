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
		setTitle("メインメニュー");
		setSize(585, 510);
		getContentPane().removeAll();
		add(createMenuMain());
		postProcessing();
		setVisible(true);
	}
	
	MenuMain createMenuMain() {
		return new MenuMain(this, scheduler);
	}
	
	void itemGetMenuDraw() {
		setTitle("ガチャ");
		setSize(585, 510);
		getContentPane().removeAll();
		add(createMenuItemGet());
		postProcessing();
	}
	
	MenuItemGet createMenuItemGet() {
		return new MenuItemGet(this, scheduler);
	}
	
	void itemDisposeMenuDraw() {
		setTitle("リサイクル");
		setSize(715, 640);
		getContentPane().removeAll();
		add(createMenuItemDispose());
		postProcessing();
	}
	
	MenuItemDispose createMenuItemDispose() {
		return new MenuItemDispose(this, scheduler);
	}
	
	void compositionDraw() {
		setTitle("ユニット編成");
		setSize(975, 570);
		getContentPane().removeAll();
		add(createMenuComposition());
		postProcessing();
	}
	
	MenuComposition createMenuComposition() {
		return new MenuComposition(this, scheduler);
	}
	
	public void selectStageDraw() {
		setTitle("ステージ選択");
		setSize(925, 570);
		getContentPane().removeAll();
		add(createMenuSelectStage());
		postProcessing();
	}
	
	MenuSelectStage createMenuSelectStage() {
		return new MenuSelectStage(this, scheduler);
	}
	
	public void battleDraw(StageData StageData, double difficultyCorrection) {
		setTitle(StageData.getName());
		setSize(1235, 600);
		getContentPane().removeAll();
		add(createBattle(StageData, difficultyCorrection));
		postProcessing();
	}
	
	Battle createBattle(StageData StageData, double difficultyCorrection) {
		return new Battle(this, scheduler, StageData, difficultyCorrection);
	}
	
	void postProcessing() {
		setLocationRelativeTo(null);
		revalidate();
		repaint();
	}
}