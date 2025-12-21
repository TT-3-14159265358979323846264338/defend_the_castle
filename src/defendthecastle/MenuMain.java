package defendthecastle;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JPanel;

import defaultdata.DefaultOther;
import defaultdata.DefaultUnit;
import defaultdata.EditImage;
import testdataedit.TestDataEdit;

//トップメニュー画面
public class MenuMain extends JPanel{
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
	private ScheduledFuture<?> mainFuture;
	private final static int NUMBER = 20;
	private MainFrame MainFrame;
	private FallMotion[] FallMotion = IntStream.range(0, NUMBER).mapToObj(i -> new FallMotion()).toArray(FallMotion[]::new);
	private FinalMotion[] FinalMotion = IntStream.range(0, NUMBER).mapToObj(i -> new FinalMotion(i)).toArray(FinalMotion[]::new);
	private JButton itemGetButton = new JButton();
	private JButton itemDisposeButton = new JButton();
	private JButton compositionButton = new JButton();
	private JButton selectStageButton = new JButton();
	private BufferedImage titleImage = new DefaultOther().getTitleImage(2);
	private List<BufferedImage> coreImage = IntStream.range(0, DefaultUnit.CORE_DATA_MAP.size()).mapToObj(i -> DefaultUnit.CORE_DATA_MAP.get(i).getImage(1)).toList();
	private List<Integer> randamList = IntStream.range(0, NUMBER).mapToObj(i -> new Random().nextInt(coreImage.size())).toList();
	private int count;
	
	//テスト用
	private JButton testButton = new JButton();
	
	MenuMain(MainFrame MainFrame) {
		this.MainFrame = MainFrame;
		setBackground(new Color(240, 170, 80));
		addItemGetButton();
		addItemDisposeButton();
		addCompositionButton();
		addBattleButton();
		addTestButton();
		effectTimer();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setButton(itemGetButton, "ガチャ", 10, 400, 130, 60);
		setButton(itemDisposeButton, "リサイクル", 150, 400, 130, 60);
		setButton(compositionButton, "ユニット編成", 290, 400, 130, 60);
		setButton(selectStageButton, "ステージ選択", 430, 400, 130, 60);
		setButton(testButton, "セーブデータ編集", 410, 0, 160, 40);
		drawImage(g);
		requestFocus();
	}
	
	private void addItemGetButton() {
		add(itemGetButton);
		itemGetButton.addActionListener(this::itemGetButtonAction);
	}
	
	void itemGetButtonAction(ActionEvent e) {
		MainFrame.itemGetMenuDraw();
	}
	
	private void addItemDisposeButton() {
		add(itemDisposeButton);
		itemDisposeButton.addActionListener(this::itemDisposeButtonAction);
	}
	
	void itemDisposeButtonAction(ActionEvent e) {
		MainFrame.itemDisposeMenuDraw();
	}
	
	private void addCompositionButton() {
		add(compositionButton);
		compositionButton.addActionListener(this::compositionButtonAction);
	}
	
	void compositionButtonAction(ActionEvent e) {
		MainFrame.compositionDraw();
	}
	
	private void addBattleButton() {
		add(selectStageButton);
		selectStageButton.addActionListener(this::battleButtonAction);
	}
	
	void battleButtonAction(ActionEvent e) {
		MainFrame.selectStageDraw();
	}
	
	private void setButton(JButton button, String name, int x, int y, int width, int height) {
		button.setText(name);
		button.setBounds(x, y, width, height);
		button.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 15));
	}
	
	void drawImage(Graphics g) {
		if(mainFuture.isCancelled()) {
			IntStream.range(0, NUMBER).forEach(i -> g.drawImage(coreImage.get(randamList.get(i)), FinalMotion[i].getX(), FinalMotion[i].getY(), this));
			g.drawImage(titleImage, 40, 100, this);
			return;
		}
		IntStream.range(0, NUMBER).filter(i -> FallMotion[i].canStart()).forEach(i -> g.drawImage(EditImage.rotateImage(coreImage.get(randamList.get(i)), FallMotion[i].getAngle()), FallMotion[i].getX(), FallMotion[i].getY(), this));
	}

	private void effectTimer() {
		mainFuture = scheduler.scheduleAtFixedRate(this::effectTimerProcess, 0, 300, TimeUnit.MILLISECONDS);
	}
	
	void effectTimerProcess() {
		try {
			FallMotion[count].fallTimerStart(scheduler);
		}catch(Exception ignore) {
		}
		count++;
		if(Stream.of(FallMotion).noneMatch(i -> i.canStart())) {
			Stream.of(FinalMotion).forEach(i -> i.finalTimerStart(scheduler));
			scheduler.scheduleAtFixedRate(this::schedulerEndProcess, 2, 1, TimeUnit.SECONDS);
			mainFuture.cancel(true);
		}
	}
	
	void schedulerEndProcess() {
		if(Stream.of(FinalMotion).allMatch(i -> i.canEnd())) {
			scheduler.shutdown();
		}
	}
	
	//テスト用
	private void addTestButton() {
		add(testButton);
		testButton.addActionListener(this::testButtonAction);
	}
	
	void testButtonAction(ActionEvent e) {
		new TestDataEdit();
	}

	ScheduledFuture<?> getMainFuture() {
		return mainFuture;
	}

	void setMainFuture(ScheduledFuture<?> mainFuture) {
		this.mainFuture = mainFuture;
	}

	MainFrame getMainFrame() {
		return MainFrame;
	}

	void setMainFrame(MainFrame mainFrame) {
		MainFrame = mainFrame;
	}

	JButton getItemGetButton() {
		return itemGetButton;
	}

	void setItemGetButton(JButton itemGetButton) {
		this.itemGetButton = itemGetButton;
	}

	JButton getItemDisposeButton() {
		return itemDisposeButton;
	}

	void setItemDisposeButton(JButton itemDisposeButton) {
		this.itemDisposeButton = itemDisposeButton;
	}

	JButton getCompositionButton() {
		return compositionButton;
	}

	void setCompositionButton(JButton compositionButton) {
		this.compositionButton = compositionButton;
	}

	JButton getSelectStageButton() {
		return selectStageButton;
	}

	void setSelectStageButton(JButton selectStageButton) {
		this.selectStageButton = selectStageButton;
	}

	JButton getTestButton() {
		return testButton;
	}

	void setTestButton(JButton testButton) {
		this.testButton = testButton;
	}

	ScheduledExecutorService getScheduler() {
		return scheduler;
	}

	void setScheduler(ScheduledExecutorService scheduler) {
		this.scheduler = scheduler;
	}
	
	FinalMotion[] getFinalMotion() {
		return FinalMotion;
	}

	void setFinalMotion(FinalMotion[] finalMotion) {
		FinalMotion = finalMotion;
	}

	FallMotion[] getFallMotion() {
		return FallMotion;
	}

	void setFallMotion(FallMotion[] fallMotion) {
		FallMotion = fallMotion;
	}

	int getCount() {
		return count;
	}

	void setCount(int count) {
		this.count = count;
	}

	BufferedImage getTitleImage() {
		return titleImage;
	}

	void setTitleImage(BufferedImage titleImage) {
		this.titleImage = titleImage;
	}

	List<BufferedImage> getCoreImage() {
		return coreImage;
	}

	void setCoreImage(List<BufferedImage> coreImage) {
		this.coreImage = coreImage;
	}

	List<Integer> getRandamList() {
		return randamList;
	}

	void setRandamList(List<Integer> randamList) {
		this.randamList = randamList;
	}
}