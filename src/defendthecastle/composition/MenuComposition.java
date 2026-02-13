package defendthecastle.composition;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.IntStream;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import commoninheritance.CommonJPanel;
import defaultdata.DefaultUnit;
import defaultdata.EditImage;
import defendthecastle.MainFrame;
import screendisplay.DisplayStatus;

//編成
public class MenuComposition extends CommonJPanel implements MouseListener{
	public static final int SIZE = 60;
	private final MainFrame mainFrame;
	private final SaveData saveData;
	private final DisplayListCreation displayListCreation;
	private final CompositionImage compositionImage;
	private final ImagePanel coreImagePanel;
	private final ImagePanel weaponImagePanel;
	private final JLabel compositionNameLabel = new JLabel();
	private final JLabel compositionLabel = new JLabel();
	private final JLabel typeLabel = new JLabel();
	private final JButton swapButton = new JButton();
	private final JButton nameChangeButton = new JButton();
	private final JButton saveButton = new JButton();
	private final JButton loadButton = new JButton();
	private final JButton resetButton = new JButton();
	private final JButton returnButton = new JButton();
	private final JButton switchButton = new JButton();
	private final JButton sortButton = new JButton();
	private final DefaultListModel<String> compositionListModel = new DefaultListModel<>();
	private final JList<String> compositionJList = new JList<>(compositionListModel);
	private final JScrollPane compositionScroll = new JScrollPane();
	private final JScrollPane itemScroll = new JScrollPane();
	private final Font largeFont;
	private final Font smallFont;
	
	public MenuComposition(MainFrame MainFrame, ScheduledExecutorService scheduler) {
		this.mainFrame = MainFrame;
		saveData = createSaveData();
		displayListCreation = createDisplayListCreation();
		compositionImage = createCompositionImage();
		coreImagePanel = createImagePanel(scheduler, compositionImage.getNormalCoreList(), saveData.getCoreNumberList(), saveData.getNowCoreNumberList(), true);
		weaponImagePanel = createImagePanel(scheduler, compositionImage.getNormalWeaponList(), saveData.getWeaponNumberList(), saveData.getNowWeaponNumberList(), false);
		largeFont = createFont(20);
		smallFont = createFont(16);
		addMouseListener(this);
		repaintTimer(scheduler, brown());
		setLabel(compositionNameLabel, "編成名", 10, 10, 130, 30);
		setLabel(compositionLabel, "ユニット編成", 230, 10, 130, 30);
		setLabel(typeLabel, typeName(), 570, 10, 130, 30);
		addSwapButton();
		addNameChangeButton();
		addSaveButton();
		addLoadButton();
		addResetButton();
		addReturnButton();
		addSwitchButton();
		addSortButton();
		addCompositionScroll();
		addItemScroll();
	}
	
	ImagePanel createImagePanel(ScheduledExecutorService scheduler, List<BufferedImage> imageList, List<Integer> displayList, List<Integer> numberList, boolean exists) {
		return new ImagePanel(scheduler, imageList, displayListCreation.getDisplayList(displayList), numberList, exists);
	}
	
	SaveData createSaveData() {
		return new SaveData();
	}
	
	DisplayListCreation createDisplayListCreation() {
		return new DisplayListCreation(saveData);
	}
	
	CompositionImage createCompositionImage(){
		return new CompositionImage();
	}
	
	Font createFont(int size) {
		return new Font("ＭＳ ゴシック", Font.BOLD, size);
	}
	
	String typeName() {
		return (itemScroll.getViewport().getView() == coreImagePanel)? "コアリスト": "武器リスト";
	}
	
	private void setLabel(JLabel label, String name, int x, int y, int width, int height) {
		label.setText(name);
		label.setBounds(x, y, width, height);
		label.setFont(largeFont);
		add(label);
	}
	
	private void addSwapButton(){
		setButton(swapButton, "編成入替", 10, 320, 101, 60);
		swapButton.addActionListener(this::swapButtonAction);
	}
	
	void swapButtonAction(ActionEvent e) {
		saveData.swapComposition(compositionJList.getMaxSelectionIndex(), compositionJList.getMinSelectionIndex());
		modelUpdate();
	}
	
	private void addNameChangeButton() {
		setButton(nameChangeButton, "名称変更", 120, 320, 101, 60);
		nameChangeButton.addActionListener(this::nameChangeButtonAction);
	}
	
	void nameChangeButtonAction(ActionEvent e) {
		saveData.changeCompositionName();
		modelUpdate();
	}
	
	private void addSaveButton() {
		setButton(saveButton, "セーブ", 10, 390, 101, 60);
		saveButton.addActionListener(this::saveButtonAction);
	}
	
	void saveButtonAction(ActionEvent e) {
		saveData.saveProcessing();
	}
	
	private void addLoadButton() {
		setButton(loadButton, "ロード", 120, 390, 101, 60);
		loadButton.addActionListener(this::loadButtonAction);
	}
	
	void loadButtonAction(ActionEvent e) {
		saveData.loadProcessing();
		modelUpdate();
	}
	
	private void addResetButton() {
		setButton(resetButton, "リセット", 10, 460, 101, 60);
		resetButton.addActionListener(this::resetButtonAction);
	}
	
	void resetButtonAction(ActionEvent e) {
		saveData.resetComposition();
	}
	
	private void addReturnButton() {
		setButton(returnButton, "戻る", 120, 460, 101, 60);
		returnButton.addActionListener(this::returnButtonAction);
	}
	
	void returnButtonAction(ActionEvent e) {
		if(saveData.returnProcessing()) {
			mainFrame.mainMenuDraw();
		}
	}
	
	private void addSwitchButton() {
		setButton(switchButton, "表示切替", 570, 460, 185, 60);
		switchButton.addActionListener(this::switchButtonAction);
	}
	
	void switchButtonAction(ActionEvent e) {
		itemScroll.getViewport().setView((itemScroll.getViewport().getView() == coreImagePanel)? weaponImagePanel: coreImagePanel);
		typeLabel.setText(typeName());
	}
	
	private void addSortButton() {
		setButton(sortButton, "ソート", 765, 460, 185, 60);
		sortButton.addActionListener(this::sortButtonAction);
	}
	
	void sortButtonAction(ActionEvent e) {
		if(itemScroll.getViewport().getView() == coreImagePanel) {
			coreImagePanel.setDisplayList(displayListCreation.getCoreDisplayList());
		}else {
			weaponImagePanel.setDisplayList(displayListCreation.getWeaponDisplayList());
		}
	}
	
	private void addCompositionScroll() {
		modelUpdate();
		compositionJList.setFont(largeFont);
		compositionJList.addListSelectionListener(_ -> {
			int selectIndex = compositionJList.getSelectedIndex();
			if(selectIndex < 0) {
				return;
			}
			saveData.selectNumberUpdate(selectIndex);
		});
		compositionScroll.getViewport().setView(compositionJList);
		setScroll(compositionScroll, 10, 40, 210, 270);
		CompletableFuture.runAsync(this::delaySelect);
	}
	
	void delaySelect() {
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		compositionJList.ensureIndexIsVisible(saveData.getSelectNumber());
	}
	
	private void addItemScroll() {
		itemScroll.getViewport().setView(coreImagePanel);
		setScroll(itemScroll, 570, 40, 380, 410);
	}
	
	private void setButton(JButton button, String name, int x, int y, int width, int height) {
		button.setText(name);
		button.setBounds(x, y, width, height);
		button.setFont(smallFont);
		add(button);
	}
	
	private void setScroll(JScrollPane scroll, int x, int y, int width, int height) {
		scroll.setBounds(x, y, width, height);
		scroll.setPreferredSize(scroll.getSize());
		add(scroll);
	}
	
	void modelUpdate() {
		compositionListModel.clear();
		saveData.getCompositionNameList().stream().forEach(i -> compositionListModel.addElement(i));
		compositionJList.setSelectedIndex(saveData.getSelectNumber());
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.fillRect(230, 40, 330, 480);
		IntStream.range(0, saveData.getActiveCompositionList().size()).forEach(i -> {
			try {
				g.drawImage(compositionImage.getRightWeapon(saveData.getUnitData(i).getUnit(DefaultUnit.RIGHT_WEAPON)), getPositionX(i), getPositionY(i), this);
			}catch(Exception ignore) {
				//右武器を装備していないので、無視する
			}
			g.drawImage(compositionImage.getCore(saveData.getUnitData(i).getUnit(DefaultUnit.CORE)), getPositionX(i), getPositionY(i), this);
			try {
				g.drawImage(compositionImage.getLeftWeapon(saveData.getUnitData(i).getUnit(DefaultUnit.LEFT_WEAPON)), getPositionX(i), getPositionY(i), this);
			}catch(Exception ignore) {
				//左武器を装備していないので、無視する
			}
		});
	}
	
	int getPositionX(int i) {
		return 230 + i % 2 * 150;
	}
	
	int getPositionY(int i) {
		return 40 + i / 2 * 100;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		IntStream.range(0, saveData.getActiveCompositionList().size()).forEach(i -> {
			int x = getPositionX(i) + 60;
			int y = getPositionY(i) + 60;
			if(ValueRange.of(x, x + SIZE).isValidIntValue(e.getX())
					&& ValueRange.of(y, y + SIZE).isValidIntValue(e.getY())){
				unitOperation(i);
			}
		});
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	void unitOperation(int number) {
		try {
			if(itemScroll.getViewport().getView() == coreImagePanel) {
				int selectCore = coreImagePanel.getSelectNumber();
				if(0 < saveData.getCoreNumberList().get(selectCore)) {
					saveData.changeCore(number, selectCore);
					coreImagePanel.resetSelectNumber();
				}
			}else {
				int selectWeapon = weaponImagePanel.getSelectNumber();
				if(0 < saveData.getWeaponNumberList().get(selectWeapon)) {
					saveData.changeWeapon(number, selectWeapon);
					weaponImagePanel.resetSelectNumber();
				}
			}
		}catch(Exception notSelect) {
			displayStatus(number);
		}
	}
	
	void displayStatus(int number) {
		List<Integer> unitList = saveData.getUnitData(number).getUnitDataList();
		new DisplayStatus().unit(EditImage.compositeImage(getImageList(unitList)), unitList);
	}
	
	private List<BufferedImage> getImageList(List<Integer> unitData){
		List<BufferedImage> originalImage = new ArrayList<>();
		try {
			originalImage.add(compositionImage.getRightWeapon(unitData.get(DefaultUnit.RIGHT_WEAPON)));
		}catch(Exception e) {
			originalImage.add(null);
		}
		originalImage.add(compositionImage.getCore(unitData.get(DefaultUnit.CORE)));
		try {
			originalImage.add(compositionImage.getLeftWeapon(unitData.get(DefaultUnit.LEFT_WEAPON)));
		}catch(Exception e) {
			originalImage.add(null);
		}
		return originalImage;
	}
}