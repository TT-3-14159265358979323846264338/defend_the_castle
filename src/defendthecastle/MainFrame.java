package defendthecastle;

import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JFrame;

import defaultdata.Stage;
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
		return new MenuSelectStage(this);
	}
	
	public void battleDraw(Stage stage, double difficultyCorrection) {
		setTitle(stage.getLabel().getName());
		setSize(1235, 600);
		getContentPane().removeAll();
		add(createBattle(stage, difficultyCorrection));
		postProcessing();
	}
	
	Battle createBattle(Stage stage, double difficultyCorrection) {
		return new Battle(this, scheduler, stage, difficultyCorrection);
	}
	
	void postProcessing() {
		setLocationRelativeTo(null);
		revalidate();
		repaint();
	}
}