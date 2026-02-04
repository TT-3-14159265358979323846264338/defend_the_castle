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
import java.util.stream.IntStream;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import defaultdata.DefaultUnit;
import defaultdata.EditImage;
import defendthecastle.MainFrame;
import screendisplay.DisplayStatus;

//編成
public class MenuComposition extends JPanel implements MouseListener{
	public static final int SIZE = 60;
	private MainFrame MainFrame;
	private JLabel compositionNameLabel = new JLabel();
	private JLabel compositionLabel = new JLabel();
	private JLabel typeLabel = new JLabel();
	private JButton newButton = new JButton();
	private JButton removeButton = new JButton();
	private JButton swapButton = new JButton();
	private JButton nameChangeButton = new JButton();
	private JButton saveButton = new JButton();
	private JButton loadButton = new JButton();
	private JButton resetButton = new JButton();
	private JButton returnButton = new JButton();
	private JButton switchButton = new JButton();
	private JButton sortButton = new JButton();
	private DefaultListModel<String> compositionListModel = new DefaultListModel<>();
	private JList<String> compositionJList = new JList<>(compositionListModel);
	private JScrollPane compositionScroll = new JScrollPane();
	private JScrollPane itemScroll = new JScrollPane();
	private ImagePanel CoreImagePanel = new ImagePanel();
	private ImagePanel WeaponImagePanel = new ImagePanel();
	private SaveData SaveData = new SaveData();
	private DisplayListCreation DisplayListCreation = new DisplayListCreation(SaveData);
	private List<BufferedImage> rightWeaponList = IntStream.range(0, DefaultUnit.WEAPON_DATA_MAP.size()).mapToObj(i -> DefaultUnit.WEAPON_DATA_MAP.get(i).getRightActionImageName().isEmpty()? null: DefaultUnit.WEAPON_DATA_MAP.get(i).getRightActionImage(2).get(0)).toList();
	private List<BufferedImage> ceterCoreList = IntStream.range(0, DefaultUnit.CORE_DATA_MAP.size()).mapToObj(i -> DefaultUnit.CORE_DATA_MAP.get(i).getActionImage(2)).toList();
	private List<BufferedImage> leftWeaponList = IntStream.range(0, DefaultUnit.WEAPON_DATA_MAP.size()).mapToObj(i -> DefaultUnit.WEAPON_DATA_MAP.get(i).getLeftActionImage(2).get(0)).toList();
	
	public MenuComposition(MainFrame MainFrame) {
		this.MainFrame = MainFrame;
		addMouseListener(this);
		setBackground(new Color(240, 170, 80));
		add(compositionNameLabel);
		add(compositionLabel);
		add(typeLabel);
		addNewButton();
		addRemoveButton();
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
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setLabel(compositionNameLabel, "編成名", 10, 10, 130, 30);
		setLabel(compositionLabel, "ユニット編成", 230, 10, 130, 30);
		setLabel(typeLabel, (itemScroll.getViewport().getView() == CoreImagePanel)? "コアリスト": "武器リスト", 570, 10, 130, 30);
		setButton(newButton, "編成追加", 10, 250, 101, 60);
		setButton(removeButton, "編成削除", 120, 250, 101, 60);
		setButton(swapButton, "編成入替", 10, 320, 101, 60);
		setButton(nameChangeButton, "名称変更", 120, 320, 101, 60);
		setButton(saveButton, "セーブ", 10, 390, 101, 60);
		setButton(loadButton, "ロード", 120, 390, 101, 60);
		setButton(resetButton, "リセット", 10, 460, 101, 60);
		setButton(returnButton, "戻る", 120, 460, 101, 60);
		setButton(switchButton, "表示切替", 570, 460, 185, 60);
		setButton(sortButton, "ソート", 765, 460, 185, 60);
		compositionJList.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
		setScroll(compositionScroll, 10, 40, 210, 200);
		setScroll(itemScroll, 570, 40, 380, 410);
		drawComposition(g);
		SaveData.selectNumberUpdate(compositionJList.getSelectedIndex());
		SaveData.countNumber();
		requestFocus();
	}
	
	private void addNewButton(){
		add(newButton);
		newButton.addActionListener(this::newButtonAction);
	}
	
	void newButtonAction(ActionEvent e) {
		SaveData.addNewComposition();
		modelUpdate();
	}
	
	private void addRemoveButton() {
		add(removeButton);
		removeButton.addActionListener(this::removeButtonAction);
	}
	
	void removeButtonAction(ActionEvent e) {
		SaveData.removeComposition(compositionJList.getSelectedIndices());
		modelUpdate();
	}
	
	private void addSwapButton(){
		add(swapButton);
		swapButton.addActionListener(this::swapButtonAction);
	}
	
	void swapButtonAction(ActionEvent e) {
		SaveData.swapComposition(compositionJList.getMaxSelectionIndex(), compositionJList.getMinSelectionIndex());
		modelUpdate();
	}
	
	private void addNameChangeButton() {
		add(nameChangeButton);
		nameChangeButton.addActionListener(this::nameChangeButtonAction);
	}
	
	void nameChangeButtonAction(ActionEvent e) {
		SaveData.changeCompositionName();
		modelUpdate();
	}
	
	private void addSaveButton() {
		add(saveButton);
		saveButton.addActionListener(this::saveButtonAction);
	}
	
	void saveButtonAction(ActionEvent e) {
		SaveData.saveProcessing();
	}
	
	private void addLoadButton() {
		add(loadButton);
		loadButton.addActionListener(this::loadButtonAction);
	}
	
	void loadButtonAction(ActionEvent e) {
		SaveData.loadProcessing();
		modelUpdate();
	}
	
	private void addResetButton() {
		add(resetButton);
		resetButton.addActionListener(this::resetButtonAction);
	}
	
	void resetButtonAction(ActionEvent e) {
		SaveData.resetComposition();
	}
	
	private void addReturnButton() {
		add(returnButton);
		returnButton.addActionListener(this::returnButtonAction);
	}
	
	void returnButtonAction(ActionEvent e) {
		if(SaveData.returnProcessing()) {
			MainFrame.mainMenuDraw();
		}
	}
	
	private void addSwitchButton() {
		add(switchButton);
		switchButton.addActionListener(this::switchButtonAction);
	}
	
	void switchButtonAction(ActionEvent e) {
		itemScroll.getViewport().setView((itemScroll.getViewport().getView() == CoreImagePanel)? WeaponImagePanel: CoreImagePanel);
	}
	
	private void addSortButton() {
		add(sortButton);
		sortButton.addActionListener(this::sortButtonAction);
	}
	
	void sortButtonAction(ActionEvent e) {
		if(itemScroll.getViewport().getView() == CoreImagePanel) {
			CoreImagePanel.setDisplayList(DisplayListCreation.getCoreDisplayList());
		}else {
			WeaponImagePanel.setDisplayList(DisplayListCreation.getWeaponDisplayList());
		}
	}
	
	private void addCompositionScroll() {
		modelUpdate();
		compositionScroll.getViewport().setView(compositionJList);
		add(compositionScroll);
		CompletableFuture.runAsync(this::delaySelect);
	}
	
	void delaySelect() {
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		compositionJList.ensureIndexIsVisible(SaveData.getSelectNumber());
	}
	
	private void addItemScroll() {
		CoreImagePanel.setImagePanel(IntStream.range(0, DefaultUnit.CORE_DATA_MAP.size()).mapToObj(i -> DefaultUnit.CORE_DATA_MAP.get(i).getImage(2)).toList(), DisplayListCreation.getDisplayList(SaveData.getCoreNumberList()), SaveData.getNowCoreNumberList(), true);
		WeaponImagePanel.setImagePanel(IntStream.range(0, DefaultUnit.WEAPON_DATA_MAP.size()).mapToObj(i -> DefaultUnit.WEAPON_DATA_MAP.get(i).getImage(2)).toList(), DisplayListCreation.getDisplayList(SaveData.getWeaponNumberList()), SaveData.getNowWeaponNumberList(), false);
		itemScroll.getViewport().setView(CoreImagePanel);
		add(itemScroll);
	}
	
	private void setLabel(JLabel label, String name, int x, int y, int width, int height) {
		label.setText(name);
		label.setBounds(x, y, width, height);
		label.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
	}
	
	private void setButton(JButton button, String name, int x, int y, int width, int height) {
		button.setText(name);
		button.setBounds(x, y, width, height);
		button.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16));
	}
	
	private void setScroll(JScrollPane scroll, int x, int y, int width, int height) {
		scroll.setBounds(x, y, width, height);
		scroll.setPreferredSize(scroll.getSize());
	}
	
	void modelUpdate() {
		compositionListModel.clear();
		SaveData.getCompositionNameList().stream().forEach(i -> compositionListModel.addElement(i));
		compositionJList.setSelectedIndex(SaveData.getSelectNumber());
	}
	
	void drawComposition(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(230, 40, 330, 480);
		IntStream.range(0, SaveData.getActiveCompositionList().size()).forEach(i -> {
			try {
				g.drawImage(rightWeaponList.get(SaveData.getUnitData(i).getUnit(DefaultUnit.RIGHT_WEAPON)), getPositionX(i), getPositionY(i), this);
			}catch(Exception ignore) {
				//右武器を装備していないので、無視する
			}
			g.drawImage(ceterCoreList.get(SaveData.getUnitData(i).getUnit(DefaultUnit.CORE)), getPositionX(i), getPositionY(i), this);
			try {
				g.drawImage(leftWeaponList.get(SaveData.getUnitData(i).getUnit(DefaultUnit.LEFT_WEAPON)), getPositionX(i), getPositionY(i), this);
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
		IntStream.range(0, SaveData.getActiveCompositionList().size()).forEach(i -> {
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
			if(itemScroll.getViewport().getView() == CoreImagePanel) {
				int selectCore = CoreImagePanel.getSelectNumber();
				if(0 < SaveData.getCoreNumberList().get(selectCore)) {
					SaveData.changeCore(number, selectCore);
					CoreImagePanel.resetSelectNumber();
				}
			}else {
				int selectWeapon = WeaponImagePanel.getSelectNumber();
				if(0 < SaveData.getWeaponNumberList().get(selectWeapon)) {
					SaveData.changeWeapon(number, selectWeapon);
					WeaponImagePanel.resetSelectNumber();
				}
			}
		}catch(Exception notSelect) {
			List<Integer> unitList = SaveData.getUnitData(number).getUnitDataList();
			new DisplayStatus().unit(EditImage.compositeImage(getImageList(unitList)), unitList);
		}
	}
	
	private List<BufferedImage> getImageList(List<Integer> unitData){
		List<BufferedImage> originalImage = new ArrayList<>();
		try {
			originalImage.add(rightWeaponList.get(unitData.get(DefaultUnit.RIGHT_WEAPON)));
		}catch(Exception e) {
			originalImage.add(null);
		}
		originalImage.add(ceterCoreList.get(unitData.get(DefaultUnit.CORE)));
		try {
			originalImage.add(leftWeaponList.get(unitData.get(DefaultUnit.LEFT_WEAPON)));
		}catch(Exception e) {
			originalImage.add(null);
		}
		return originalImage;
	}

	JLabel getCompositionNameLabel() {
		return compositionNameLabel;
	}

	JLabel getCompositionLabel() {
		return compositionLabel;
	}

	JLabel getTypeLabel() {
		return typeLabel;
	}

	JButton getNewButton() {
		return newButton;
	}

	JButton getRemoveButton() {
		return removeButton;
	}

	JButton getSwapButton() {
		return swapButton;
	}

	JButton getNameChangeButton() {
		return nameChangeButton;
	}

	JButton getSaveButton() {
		return saveButton;
	}

	JButton getLoadButton() {
		return loadButton;
	}

	JButton getResetButton() {
		return resetButton;
	}

	JButton getReturnButton() {
		return returnButton;
	}

	JButton getSwitchButton() {
		return switchButton;
	}

	JButton getSortButton() {
		return sortButton;
	}

	void setCompositionListModel(DefaultListModel<String> compositionListModel) {
		this.compositionListModel = compositionListModel;
	}

	void setCompositionJList(JList<String> compositionJList) {
		this.compositionJList = compositionJList;
	}

	JScrollPane getCompositionScroll() {
		return compositionScroll;
	}

	JScrollPane getItemScroll() {
		return itemScroll;
	}

	void setItemScroll(JScrollPane itemScroll) {
		this.itemScroll = itemScroll;
	}

	void setCoreImagePanel(ImagePanel coreImagePanel) {
		CoreImagePanel = coreImagePanel;
	}

	void setWeaponImagePanel(ImagePanel weaponImagePanel) {
		WeaponImagePanel = weaponImagePanel;
	}

	void setSaveData(SaveData saveData) {
		SaveData = saveData;
	}

	void setDisplayListCreation(DisplayListCreation displayListCreation) {
		DisplayListCreation = displayListCreation;
	}

	List<BufferedImage> getRightWeaponList() {
		return rightWeaponList;
	}

	void setRightWeaponList(List<BufferedImage> rightWeaponList) {
		this.rightWeaponList = rightWeaponList;
	}

	List<BufferedImage> getCeterCoreList() {
		return ceterCoreList;
	}

	void setCeterCoreList(List<BufferedImage> ceterCoreList) {
		this.ceterCoreList = ceterCoreList;
	}

	List<BufferedImage> getLeftWeaponList() {
		return leftWeaponList;
	}

	void setLeftWeaponList(List<BufferedImage> leftWeaponList) {
		this.leftWeaponList = leftWeaponList;
	}
}