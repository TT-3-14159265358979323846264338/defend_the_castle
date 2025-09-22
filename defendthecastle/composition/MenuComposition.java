package defendthecastle.composition;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;
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
		addReturnButton(MainFrame);
		addSwitchButton();
		addSortButton();
		addCompositionScroll();
		addItemScroll();
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setLabel(compositionNameLabel, "編成名", 10, 10, 130, 30);
		setLabel(compositionLabel, "ユニット編成", 230, 10, 130, 30);
		setLabel(typeLabel, (itemScroll.getViewport().getView() == CoreImagePanel)? "コアリスト": "武器リスト", 570, 10, 130, 30);
		setButton(newButton, "編成追加", 10, 250, 100, 60);
		setButton(removeButton, "編成削除", 120, 250, 100, 60);
		setButton(swapButton, "編成入替", 10, 320, 100, 60);
		setButton(nameChangeButton, "名称変更", 120, 320, 100, 60);
		setButton(saveButton, "セーブ", 10, 390, 100, 60);
		setButton(loadButton, "ロード", 120, 390, 100, 60);
		setButton(resetButton, "リセット", 10, 460, 100, 60);
		setButton(returnButton, "戻る", 120, 460, 100, 60);
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
		newButton.addActionListener(e->{
			SaveData.addNewComposition();
			modelUpdate();
		});
	}
	
	private void addRemoveButton() {
		add(removeButton);
		removeButton.addActionListener(e->{
			SaveData.removeComposition(compositionJList.getSelectedIndices());
			modelUpdate();
		});
	}
	
	private void addSwapButton(){
		add(swapButton);
		swapButton.addActionListener(e->{
			SaveData.swapComposition(compositionJList.getMaxSelectionIndex(), compositionJList.getMinSelectionIndex());
			modelUpdate();
		});
	}
	
	private void addNameChangeButton() {
		add(nameChangeButton);
		nameChangeButton.addActionListener(e->{
			String newName = SaveData.changeCompositionName();
			if(newName != null) {
				compositionListModel.set(SaveData.getSelectNumber(), newName);
			}
		});
	}
	
	private void addSaveButton() {
		add(saveButton);
		saveButton.addActionListener(e->{
			SaveData.saveProcessing();
		});
	}
	
	private void addLoadButton() {
		add(loadButton);
		loadButton.addActionListener(e->{
			SaveData.loadProcessing();
			modelUpdate();
		});
	}
	
	private void addResetButton() {
		add(resetButton);
		resetButton.addActionListener(e->{
			SaveData.resetComposition();
		});
	}
	
	private void addReturnButton(MainFrame MainFrame) {
		add(returnButton);
		returnButton.addActionListener(e->{
			if(SaveData.returnProcessing()) {
				MainFrame.mainMenuDraw();
			}
		});
	}
	
	private void addSwitchButton() {
		add(switchButton);
		switchButton.addActionListener(e->{
			itemScroll.getViewport().setView((itemScroll.getViewport().getView() == CoreImagePanel)? WeaponImagePanel: CoreImagePanel);
		});
	}
	
	private void addSortButton() {
		add(sortButton);
		sortButton.addActionListener(e->{
			if(itemScroll.getViewport().getView() == CoreImagePanel) {
				CoreImagePanel.updateList(DisplayListCreation.getCoreDisplayList());
			}else {
				WeaponImagePanel.updateList(DisplayListCreation.getWeaponDisplayList());
			}
		});
	}
	
	private void addCompositionScroll() {
		modelUpdate();
		compositionScroll.getViewport().setView(compositionJList);
    	add(compositionScroll);
    	new DelaySelect(compositionJList, SaveData.getSelectNumber()).start();
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
	
	private void modelUpdate() {
		compositionListModel.clear();
		SaveData.getCompositionNameList().stream().forEach(i -> compositionListModel.addElement(i));
		compositionJList.setSelectedIndex(SaveData.getSelectNumber());
	}
	
	private void drawComposition(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(230, 40, 330, 480);
		IntStream.range(0, SaveData.getActiveCompositionList().size()).forEach(i -> {
			try {
				g.drawImage(rightWeaponList.get(SaveData.getActiveUnit(i).get(0)), getPositionX(i), getPositionY(i), this);
			}catch(Exception ignore) {
				//右武器を装備していないので、無視する
			}
			g.drawImage(ceterCoreList.get(SaveData.getActiveUnit(i).get(1)), getPositionX(i), getPositionY(i), this);
			try {
				g.drawImage(leftWeaponList.get(SaveData.getActiveUnit(i).get(2)), getPositionX(i), getPositionY(i), this);
			}catch(Exception ignore) {
				//左武器を装備していないので、無視する
			}
		});
	}
	
	private int getPositionX(int i) {
		return 230 + i % 2 * 150;
	}
	
	private int getPositionY(int i) {
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
				try {
					if(itemScroll.getViewport().getView() == CoreImagePanel) {
						int selectCore = CoreImagePanel.getSelectNumber();
						if(0 < SaveData.getCoreNumberList().get(selectCore)) {
							SaveData.changeCore(i, selectCore);
							CoreImagePanel.resetSelectNumber();
							SaveData.countNumber();
						}
					}else {
						int selectWeapon = WeaponImagePanel.getSelectNumber();
						if(0 < SaveData.getWeaponNumberList().get(selectWeapon)) {
							SaveData.changeWeapon(i, selectWeapon);
							WeaponImagePanel.resetSelectNumber();
							SaveData.countNumber();
						}
					}
				}catch(Exception notSelect) {
					new DisplayStatus().unit(EditImage.compositeImage(getImageList(SaveData.getActiveUnit(i))), SaveData.getActiveUnit(i));
				}
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
	
	private List<BufferedImage> getImageList(List<Integer> unitData){
		List<BufferedImage> originalImage = new ArrayList<>();
		try {
			originalImage.add(rightWeaponList.get(unitData.get(0)));
		}catch(Exception e) {
			originalImage.add(null);
		}
		originalImage.add(ceterCoreList.get(unitData.get(1)));
		try {
			originalImage.add(leftWeaponList.get(unitData.get(2)));
		}catch(Exception e) {
			originalImage.add(null);
		}
		return originalImage;
	}
}