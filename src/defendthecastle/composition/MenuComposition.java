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
import javax.swing.event.ListSelectionEvent;

import commoninheritance.CommonJPanel;
import commoninheritance.ImagePanel;
import defaultdata.DefaultUnit;
import defaultdata.EditImage;
import defendthecastle.MainFrame;
import defendthecastle.screendisplay.DisplayStatus;

//編成
public class MenuComposition extends CommonJPanel implements MouseListener{
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
	private final Font largeFont = new Font("ＭＳ ゴシック", Font.BOLD, 20);
	private final Font smallFont = new Font("ＭＳ ゴシック", Font.BOLD, 16);
	
	public MenuComposition(MainFrame MainFrame, ScheduledExecutorService scheduler) {
		this.mainFrame = MainFrame;
		saveData = createSaveData();
		displayListCreation = createDisplayListCreation();
		compositionImage = createCompositionImage();
		coreImagePanel = createImagePanel(scheduler, compositionImage.getNormalCoreList(), displayListCreation.initialCoreDisplayList(), saveData.getNowCoreNumberList(), true);
		weaponImagePanel = createImagePanel(scheduler, compositionImage.getNormalWeaponList(), displayListCreation.initialWeaponDisplayList(), saveData.getNowWeaponNumberList(), false);
		addMouseListener(this);
		setLabel(compositionNameLabel, "編成名", 10, 10, 130, 30, largeFont);
		setLabel(compositionLabel, "ユニット編成", 230, 10, 130, 30, largeFont);
		setLabel(typeLabel, typeName(), 570, 10, 130, 30, largeFont);
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
		movie(scheduler, brown());
	}
	
	ImagePanel createImagePanel(ScheduledExecutorService scheduler, List<BufferedImage> imageList, List<Integer> displayList, List<Integer> numberList, boolean exists) {
		return new ImagePanel(scheduler, imageList, displayList, numberList, exists, 3);
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
	
	String typeName() {
		return hasDisplayedPanel()? "コアリスト": "武器リスト";
	}
	
	boolean hasDisplayedPanel() {
		return itemScroll.getViewport().getView() == coreImagePanel;
	}
	
	private void addSwapButton(){
		setButton(swapButton, "編成入替", 10, 320, 101, 60, smallFont);
		swapButton.addActionListener(this::swapButtonAction);
	}
	
	void swapButtonAction(ActionEvent e) {
		saveData.swapComposition(compositionJList.getMaxSelectionIndex(), compositionJList.getMinSelectionIndex());
		modelUpdate();
	}
	
	private void addNameChangeButton() {
		setButton(nameChangeButton, "名称変更", 120, 320, 101, 60, smallFont);
		nameChangeButton.addActionListener(this::nameChangeButtonAction);
	}
	
	void nameChangeButtonAction(ActionEvent e) {
		saveData.changeCompositionName();
		modelUpdate();
	}
	
	private void addSaveButton() {
		setButton(saveButton, "セーブ", 10, 390, 101, 60, smallFont);
		saveButton.addActionListener(this::saveButtonAction);
	}
	
	void saveButtonAction(ActionEvent e) {
		saveData.saveProcessing();
	}
	
	private void addLoadButton() {
		setButton(loadButton, "ロード", 120, 390, 101, 60, smallFont);
		loadButton.addActionListener(this::loadButtonAction);
	}
	
	void loadButtonAction(ActionEvent e) {
		saveData.loadProcessing();
		modelUpdate();
	}
	
	private void addResetButton() {
		setButton(resetButton, "リセット", 10, 460, 101, 60, smallFont);
		resetButton.addActionListener(this::resetButtonAction);
	}
	
	void resetButtonAction(ActionEvent e) {
		saveData.resetComposition();
	}
	
	private void addReturnButton() {
		setButton(returnButton, "戻る", 120, 460, 101, 60, smallFont);
		returnButton.addActionListener(this::returnButtonAction);
	}
	
	void returnButtonAction(ActionEvent e) {
		if(saveData.returnProcessing()) {
			mainFrame.mainMenuDraw();
		}
	}
	
	private void addSwitchButton() {
		setButton(switchButton, "表示切替", 570, 460, 185, 60, smallFont);
		switchButton.addActionListener(this::switchButtonAction);
	}
	
	void switchButtonAction(ActionEvent e) {
		itemScroll.getViewport().setView(switchImagePanel());
		typeLabel.setText(typeName());
	}
	
	ImagePanel switchImagePanel() {
		return hasDisplayedPanel()? weaponImagePanel: coreImagePanel;
	}
	
	private void addSortButton() {
		setButton(sortButton, "ソート", 765, 460, 185, 60, smallFont);
		sortButton.addActionListener(this::sortButtonAction);
	}
	
	void sortButtonAction(ActionEvent e) {
		if(hasDisplayedPanel()) {
			coreImagePanel.setDisplayList(displayListCreation.getCoreDisplayList());
		}else {
			weaponImagePanel.setDisplayList(displayListCreation.getWeaponDisplayList());
		}
	}
	
	private void addCompositionScroll() {
		modelUpdate();
		compositionJList.addListSelectionListener(this::selectAction);
		compositionScroll.getViewport().setView(compositionJList);
		compositionJList.setFont(largeFont);
		setScroll(compositionScroll, 10, 40, 210, 270);
		CompletableFuture.runAsync(this::delaySelect);
	}
	
	void selectAction(ListSelectionEvent e) {
		int selectIndex = compositionJList.getSelectedIndex();
		if(selectIndex < 0) {
			return;
		}
		saveData.selectNumberUpdate(selectIndex);
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
			if(ValueRange.of(x, x + ImagePanel.UNIT_SIZE).isValidIntValue(e.getX())
					&& ValueRange.of(y, y + ImagePanel.UNIT_SIZE).isValidIntValue(e.getY())){
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
			if(hasDisplayedPanel()) {
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