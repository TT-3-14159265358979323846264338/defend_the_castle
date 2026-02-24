package defendthecastle;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.IntStream;

import javax.swing.JButton;

import commonclass.CommonJPanel;
import commonclass.EditImage;
import testdataedit.TestDataEdit;

//トップメニュー画面
public class MenuMain extends CommonJPanel{
	private final int NUMBER = 20;
	private final MainFrame mainFrame;
	private final FallMotion[] fallMotion;
	private final FinalMotion[] finalMotion;
	private final MainImage mainImage;
	private final MainTimer mainTimer;
	private final Font font = new Font("ＭＳ ゴシック", Font.BOLD, 15);
	private final JButton itemGetButton = new JButton();
	private final JButton itemDisposeButton = new JButton();
	private final JButton compositionButton = new JButton();
	private final JButton selectStageButton = new JButton();
	
	//テスト用
	private JButton testButton = new JButton();
	
	MenuMain(MainFrame MainFrame, ScheduledExecutorService scheduler) {
		this.mainFrame = MainFrame;
		fallMotion = createFallMotion();
		finalMotion = createFinalMotion();
		mainImage = createmainImage();
		mainTimer = createMainTimer(scheduler);
		setButton(itemGetButton, "ガチャ", 10, 400, 130, 60, font, this::itemGetButtonAction);
		setButton(itemDisposeButton, "リサイクル", 150, 400, 130, 60, font, this::itemDisposeButtonAction);
		setButton(compositionButton, "ユニット編成", 290, 400, 130, 60, font, this::compositionButtonAction);
		setButton(selectStageButton, "ステージ選択", 430, 400, 130, 60, font, this::battleButtonAction);
		setButton(testButton, "セーブデータ編集", 410, 0, 160, 40, font, this::testButtonAction);
		movie(scheduler, brown());
	}
	
	FallMotion[] createFallMotion(){
		return IntStream.range(0, NUMBER).mapToObj(_ -> new FallMotion()).toArray(FallMotion[]::new);
	}
	
	FinalMotion[] createFinalMotion() {
		return IntStream.range(0, NUMBER).mapToObj(i -> new FinalMotion(i)).toArray(FinalMotion[]::new);
	}
	
	MainImage createmainImage() {
		return new MainImage(NUMBER);
	}
	
	MainTimer createMainTimer(ScheduledExecutorService scheduler){
		return new MainTimer(scheduler, fallMotion, finalMotion);
	}
	
	void itemGetButtonAction(ActionEvent e) {
		mainFrame.itemGetMenuDraw();
	}
	
	void itemDisposeButtonAction(ActionEvent e) {
		mainFrame.itemDisposeMenuDraw();
	}
	
	void compositionButtonAction(ActionEvent e) {
		mainFrame.compositionDraw();
	}
	
	void battleButtonAction(ActionEvent e) {
		mainFrame.selectStageDraw();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(mainTimer.isEnd()) {
			IntStream.range(0, NUMBER).forEach(i -> g.drawImage(mainImage.getCoreImage(i), finalMotion[i].getX(), finalMotion[i].getY(), this));
			g.drawImage(mainImage.getTitleImage(), 40, 100, this);
			return;
		}
		IntStream.range(0, NUMBER).filter(i -> fallMotion[i].canStart()).forEach(i -> g.drawImage(EditImage.rotateImage(mainImage.getCoreImage(i), fallMotion[i].getAngle()), fallMotion[i].getX(), fallMotion[i].getY(), this));
	}
	
	//テスト用
	void testButtonAction(ActionEvent e) {
		new TestDataEdit();
	}
}