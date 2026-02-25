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
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.IntStream;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;

import commonclass.CommonJPanel;
import commonclass.EditImage;
import commonclass.ImagePanel;
import defendthecastle.MainFrame;
import defendthecastle.screendisplay.DisplayStatus;
import savedata.OneUnitData;

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
		setButton(swapButton, "編成入替", 10, 320, 101, 60, smallFont, this::swapButtonAction);
		setButton(nameChangeButton, "名称変更", 120, 320, 101, 60, smallFont, this::nameChangeButtonAction);
		setButton(saveButton, "セーブ", 10, 390, 101, 60, smallFont, this::saveButtonAction);
		setButton(loadButton, "ロード", 120, 390, 101, 60, smallFont, this::loadButtonAction);
		setButton(resetButton, "リセット", 10, 460, 101, 60, smallFont, this::resetButtonAction);
		setButton(returnButton, "戻る", 120, 460, 101, 60, smallFont, this::returnButtonAction);
		setButton(switchButton, "表示切替", 570, 460, 185, 60, smallFont, this::switchButtonAction);
		setButton(sortButton, "ソート", 765, 460, 185, 60, smallFont, this::sortButtonAction);
		setCompositionScroll();
		setScroll(itemScroll, 570, 40, 380, 410, coreImagePanel);
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
	
	void swapButtonAction(ActionEvent e) {
		saveData.swapComposition(compositionJList.getMaxSelectionIndex(), compositionJList.getMinSelectionIndex());
		modelUpdate();
	}
	
	void nameChangeButtonAction(ActionEvent e) {
		saveData.changeCompositionName();
		modelUpdate();
	}
	
	void saveButtonAction(ActionEvent e) {
		saveData.saveProcessing();
	}
	
	void loadButtonAction(ActionEvent e) {
		saveData.loadProcessing();
		modelUpdate();
	}
	
	void resetButtonAction(ActionEvent e) {
		saveData.resetComposition();
	}
	
	void returnButtonAction(ActionEvent e) {
		if(saveData.returnProcessing()) {
			mainFrame.mainMenuDraw();
		}
	}
	
	void switchButtonAction(ActionEvent e) {
		itemScroll.getViewport().setView(switchImagePanel());
		typeLabel.setText(typeName());
	}
	
	ImagePanel switchImagePanel() {
		return hasDisplayedPanel()? weaponImagePanel: coreImagePanel;
	}
	
	void sortButtonAction(ActionEvent e) {
		if(hasDisplayedPanel()) {
			coreImagePanel.setDisplayList(displayListCreation.getCoreDisplayList());
		}else {
			weaponImagePanel.setDisplayList(displayListCreation.getWeaponDisplayList());
		}
	}
	
	private void setCompositionScroll() {
		modelUpdate();
		compositionJList.addListSelectionListener(this::selectAction);
		compositionJList.setFont(largeFont);
		setScroll(compositionScroll, 10, 40, 210, 270, compositionJList);
		compositionJList.ensureIndexIsVisible(saveData.getSelectNumber());
	}
	
	void selectAction(ListSelectionEvent e) {
		int selectIndex = compositionJList.getSelectedIndex();
		if(selectIndex < 0) {
			return;
		}
		saveData.selectNumberUpdate(selectIndex);
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
				g.drawImage(compositionImage.getRightWeapon(saveData.getUnitData(i).getUnit(OneUnitData.RIGHT_WEAPON)), getPositionX(i), getPositionY(i), this);
			}catch(Exception ignore) {
				//右武器を装備していないので、無視する
			}
			g.drawImage(compositionImage.getCore(saveData.getUnitData(i).getUnit(OneUnitData.CORE)), getPositionX(i), getPositionY(i), this);
			try {
				g.drawImage(compositionImage.getLeftWeapon(saveData.getUnitData(i).getUnit(OneUnitData.LEFT_WEAPON)), getPositionX(i), getPositionY(i), this);
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
			originalImage.add(compositionImage.getRightWeapon(unitData.get(OneUnitData.RIGHT_WEAPON)));
		}catch(Exception e) {
			originalImage.add(null);
		}
		originalImage.add(compositionImage.getCore(unitData.get(OneUnitData.CORE)));
		try {
			originalImage.add(compositionImage.getLeftWeapon(unitData.get(OneUnitData.LEFT_WEAPON)));
		}catch(Exception e) {
			originalImage.add(null);
		}
		return originalImage;
	}
}