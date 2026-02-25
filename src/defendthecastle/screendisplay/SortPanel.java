package defendthecastle.screendisplay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import commonclass.CommonJPanel;
import defaultdata.AtackPattern;
import defaultdata.Distance;
import defaultdata.Element;
import defaultdata.Handle;
import defaultdata.StatusText;
import defaultdata.WeaponUnit;
import defaultdata.Atack;

//ソート画面
abstract class SortPanel extends CommonJPanel {
	private final SortDialog sortDialog;
	private final List<Integer> rarityList;
	private final List<List<Double>> weaponStatusList;
	private final List<List<Double>> unitStatusList;
	private final List<List<Integer>> cutList;
	private final List<Distance> distanceList;
	private final List<Handle> handleList;
	private final List<List<Element>> elementList;
	private final List<Integer> targetList;
	private final List<Integer> defaultDisplayList;
	private final JLabel sortLabel = new JLabel();
	private final JLabel filterLabel = new JLabel();
	private final JLabel[] commentLabel = IntStream.range(0, 9).mapToObj(_ -> new JLabel()).toArray(JLabel[]::new);
	private final JButton sortButton = new JButton();
	private final JButton resetButton = new JButton();
	private final JButton returnButton = new JButton();
	private JRadioButton[] mode;
	private JRadioButton[] raritySort;
	private JRadioButton[] weapon;
	private JRadioButton[] unit;
	private JRadioButton[] cut;
	private JRadioButton[] rarity;
	private JRadioButton[] distance;
	private JRadioButton[] handle;
	private JRadioButton[] element;
	private JRadioButton[] target;
	private final ButtonGroup sortGroup = new ButtonGroup();
	private final ButtonGroup itemGroup = new ButtonGroup();
	private final Font largeFont = new Font("ＭＳ ゴシック", Font.BOLD, 20);
	private final Font smallFont = new Font("ＭＳ ゴシック", Font.BOLD, 15);
	private boolean canSort;
	
	SortPanel(List<Integer> defaultList) {
		this.defaultDisplayList = defaultList;
		sortDialog = createSortDialog();
		rarityList = createRarityList();
		weaponStatusList =  speedInversion(createWeaponStatusList());
		unitStatusList = createUnitStatusList();
		cutList = createCutList();
		distanceList = createDistanceList();
		handleList = createHandleList();
		elementList = createElementList();
		targetList = createTargetList();
		setAllLabel();
		setButton(sortButton, "ソート", 220, 470, 120, 40, largeFont, this::sortButtonAction);
		setButton(resetButton, "リセット", 350, 470, 120, 40, largeFont, this::resetButtonAction);
		setButton(returnButton, "戻る", 480, 470, 120, 40, largeFont, this::returnButtonAction);
		setRadioButton();
		stillness(brown());
	}
	
	SortDialog createSortDialog() {
		return new SortDialog();
	}
	
	abstract List<Integer> createRarityList();
	
	abstract List<List<Double>> createWeaponStatusList();
	
	abstract List<List<Double>> createUnitStatusList();
	
	abstract List<List<Integer>> createCutList();
	
	abstract List<Distance> createDistanceList();
	
	abstract List<Handle> createHandleList();
	
	abstract List<List<Element>> createElementList();
	
	abstract List<Integer> createTargetList();
	
	//攻撃速度のみは昇降順が逆になっている
	List<List<Double>> speedInversion(List<List<Double>> statusList){
		return statusList.stream().map(i -> IntStream.range(0, i.size()).mapToObj(j -> (j == 2)? 1 / i.get(j): i.get(j)).toList()).toList();
	}
	
	private void setAllLabel(){
		setLabel(sortLabel, "並び替え", 10, 10, 300, 40, largeFont);
		setLabel(filterLabel, "絞り込み", 10, 240, 300, 40, largeFont);
		setLabel(commentLabel[0], "レアリティ", smallFont);
		setLabel(commentLabel[1], "武器ステータス", smallFont);
		setLabel(commentLabel[2], "ユニットステータス", smallFont);
		setLabel(commentLabel[3], "属性耐性", smallFont);
		setLabel(commentLabel[4], "レアリティ", smallFont);
		if(Objects.nonNull(distanceList)) {
			setLabel(commentLabel[5], "距離タイプ", smallFont);
			setLabel(commentLabel[6], "装備タイプ", smallFont);
			setLabel(commentLabel[7], "属性", smallFont);
			setLabel(commentLabel[8], "ターゲット", smallFont);
		}
		IntStream.range(0, commentLabel.length / 2).forEach(i -> {
			commentLabel[i].setBounds(10, 90 + 30 * (i % 4), 300, 30);
			commentLabel[i + 4].setBounds(10, 280 + 30 * (i % 4), 300, 30);
		});
		commentLabel[8].setBounds(10, 430, 300, 30);
	}
	
	void sortButtonAction(ActionEvent e) {
		canSort = true;
		sortDialog.dispose();
	}
	
	void resetButtonAction(ActionEvent e) {
		mode[0].setSelected(true);
		raritySort[0].setSelected(true);
		initializeRadioSelect(rarity);
		if(Objects.nonNull(distanceList)) {
			initializeRadioSelect(distance);
			initializeRadioSelect(handle);
			initializeRadioSelect(element);
			initializeRadioSelect(target);
		}
	}
	
	void initializeRadioSelect(JRadioButton[] radio) {
		Stream.of(radio).forEach(i -> i.setSelected(false));
	}
	
	void returnButtonAction(ActionEvent e) {
		sortDialog.dispose();
	}
	
	private void setRadioButton() {
		initializeRadioButton();
		setRadioAction();
		setRadioName();
		setRadioBounds();
	}
	
	private void initializeRadioButton() {
		mode = setRadio(new Integer[2]);
		mode[0].setSelected(true);
		radioGrouping(sortGroup, mode);
		raritySort = setRadio(new Integer[1]);
		raritySort[0].setSelected(true);
		weapon = setRadio(Atack.values());
		unit = setRadio(WeaponUnit.values());
		cut = setRadio(Element.values());
		radioGrouping(itemGroup, raritySort);
		radioGrouping(itemGroup, weapon);
		radioGrouping(itemGroup, unit);
		radioGrouping(itemGroup, cut);
		rarity = setRadio(new Integer[Collections.max(rarityList)]);
		if(Objects.nonNull(distanceList)) {
			distance = setRadio(Distance.values());
			handle = setRadio(Handle.values());
			element = setRadio(Element.values());
			target = setRadio(new Integer[AtackPattern.PATTERN_SPECIES]);
		}
	}
	
	<T>JRadioButton[] setRadio(T[] data) {
		JRadioButton[] radio = Stream.of(data).map(_ -> new JRadioButton()).toArray(JRadioButton[]::new);
		Stream.of(radio).forEach(i -> {
			i.setFont(smallFont);
			i.setOpaque(false);
			add(i);
		});
		return radio;
	}
	
	void radioGrouping(ButtonGroup group, JRadioButton[] radio) {
		Stream.of(radio).forEach(i -> group.add(i));
	}
	
	private void setRadioAction() {
		radioMethod((radio, text) -> radio.setActionCommand(text), 
				"true", 
				"false", 
				"true", 
				(number) -> "" + (number + 1), 
				(number) -> "" + number);
	}
	
	private void setRadioName() {
		AtackPattern atackPattern = createAtackPattern();
		radioMethod((radio, text) -> radio.setText(text), 
				"降順", 
				"昇順", 
				"レアリティ", 
				(number) -> String.format("★%d", number + 1), 
				(number) -> atackPattern.getAtackPattern(number).getExplanation());
	}
	
	AtackPattern createAtackPattern() {
		return new AtackPattern();
	}
	
	void radioMethod(BiConsumer<JRadioButton, String> mainMethod, 
			String modeText0, 
			String modeText1, 
			String raritySortText, 
			Function<Integer, String> rarityText, 
			Function<Integer, String> targetText) {
		mainMethod.accept(mode[0], modeText0);
		mainMethod.accept(mode[1], modeText1);
		mainMethod.accept(raritySort[0], raritySortText);
		arrayRadioText(mainMethod, weapon, Atack.values());
		arrayRadioText(mainMethod, unit, WeaponUnit.values());
		arrayRadioText(mainMethod, cut, Element.values());
		IntStream.range(0, rarity.length).forEach(i -> mainMethod.accept(rarity[i], rarityText.apply(i)));
		if(Objects.nonNull(distanceList)) {
			arrayRadioText(mainMethod, distance, Distance.values());
			arrayRadioText(mainMethod, handle, Handle.values());
			arrayRadioText(mainMethod, element, Element.values());
			IntStream.range(0, target.length).forEach(i -> mainMethod.accept(target[i], targetText.apply(i)));
		}
	}
	
	<T extends Enum<T> & StatusText>void arrayRadioText(BiConsumer<JRadioButton, String> consumer, JRadioButton[] radio, T[] data) {
		Stream.of(data).forEach(i -> consumer.accept(radio[i.getId()], i.getText()));
	}
	
	private void setRadioBounds() {
		int column = 6;
		int startX = 150;
		int startY = 50;
		int sizeX = 110;
		int sizeY = 30;
		int block1 = startY + sizeY + 10;
		int block2 = block1 + sizeY * 6 + 10;
		//ここから下でハードコードしている数字は各Radioを何行分表示しているかを表している。
		arrayRadioMethod(mode, (radio, i) -> radio.setBounds(startX + i * sizeX, startY, sizeX, sizeY));
		arrayRadioMethod(raritySort, (radio, _) -> radio.setBounds(startX, block1, sizeX, sizeY));
		arrayRadioMethod(weapon, (radio, i) -> radio.setBounds(startX + i * sizeX, block1 + sizeY, sizeX, sizeY));
		arrayRadioMethod(unit, (radio, i) -> radio.setBounds(startX + i * sizeX, block1 + sizeY * 2, sizeX, sizeY));
		arrayRadioMethod(cut, (radio, i) -> radio.setBounds(startX + i % column * sizeX, block1 + sizeY * (3 + i / column), sizeX, sizeY));
		arrayRadioMethod(rarity, (radio, i) -> radio.setBounds(startX + i * sizeX, block2, sizeX, sizeY));
		if(Objects.nonNull(distanceList)) {
			arrayRadioMethod(distance, (radio, i) -> radio.setBounds(startX + i * sizeX, block2 + sizeY, sizeX, sizeY));
			arrayRadioMethod(handle, (radio, i) -> radio.setBounds(startX + i * sizeX, block2 + sizeY * 2, sizeX, sizeY));
			arrayRadioMethod(element, (radio, i) -> radio.setBounds(startX + i % column * sizeX, block2 + sizeY * (3 + i / column), sizeX, sizeY));
			arrayRadioMethod(target, (radio, i) -> radio.setBounds(startX + i % column * sizeX, block2 + sizeY * 5, sizeX, sizeY));
		}
	}
	
	void arrayRadioMethod(JRadioButton[] radio, BiConsumer<JRadioButton, Integer> method) {
		IntStream.range(0, radio.length).forEach(i -> method.accept(radio[i], i));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(5, 90, 150, 150);
		g.fillRect(5, 280, 150, 180);
		g.setColor(Color.YELLOW);
		g.fillRect(150, 50, 220, 30);
		g.fillRect(150, 90, 655, 150);
		g.fillRect(150, 280, 655, 180);
		g.setColor(Color.BLACK);
		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(new BasicStroke(2));
		g.drawRect(150, 50, 220, 30);
		IntStream.range(0, 3).forEach(i -> g.drawRect(5, 90 + i * 30, 800, 30));
		g.drawRect(5, 180, 800, 60);
		IntStream.range(0, 3).forEach(i -> g.drawRect(5, 280 + i * 30, 800, 30));
		g.drawRect(5, 370, 800, 60);
		g.drawRect(5, 430, 800, 30);
		g.drawLine(150, 90, 150, 240);
		g.drawLine(150, 280, 150, 460);
	}
	
	/**
	 * ソート画面を開き、その入力結果からアイテムの表示順を作成する。
	 * @return アイテムの表示順に並んだアイテムのインデックスリストを返却する。
	 */
	public List<Integer> getDisplayList(){
		canSort = false;
		sortDialog.setSortDialog(this);
		if(!canSort) {
			return defaultDisplayList;
		}
		return getFilterList(getSortList(defaultDisplayList));
	}
	
	Stream<Integer> getSortList(List<Integer> displayList){
		String radioCommand = itemGroup.getSelection().getActionCommand();
		if(Boolean.valueOf(radioCommand)) {
			return displayList.stream().sorted(Comparator.comparing(i -> rarityList.get(i)));
		}
		List<String> mapList = getMapList(Atack.values());
		if(hasContained(mapList, radioCommand)) {
			return sortElement(displayList, radioCommand, weaponStatusList, mapList);
		}
		mapList = getMapList(WeaponUnit.values());
		if(hasContained(mapList, radioCommand)) {
			return sortElement(displayList, radioCommand, unitStatusList, mapList);
		}
		mapList = getMapList(Element.values());
		if(hasContained(mapList, radioCommand)) {
			return sortElement(displayList, radioCommand, cutList, mapList);
		}
		return displayList.stream();
	}
	
	<T extends Enum<T> & StatusText>List<String> getMapList(T[] data){
		return Stream.of(data).map(StatusText::getText).toList();
	}
	
	boolean hasContained(List<String> mapList, String radioCommand) {
		return mapList.contains(radioCommand);
	}
	
	<T extends Comparable<? super T>>Stream<Integer> sortElement(List<Integer> displayList, String radioCommand, List<List<T>> statusList, List<String> mapList){
		return displayList.stream().sorted(Comparator.comparing(i -> statusList.get(i).get(mapList.indexOf(radioCommand)), getOrder()));
	}
	
	<T extends Comparable<? super T>>Comparator<T> getOrder() {
		return Boolean.valueOf(sortGroup.getSelection().getActionCommand())? Comparator.reverseOrder(): Comparator.naturalOrder();
	}
	
	List<Integer> getFilterList(Stream<Integer> displayList){
		if(Objects.isNull(distanceList)) {
			return filterElement(displayList, rarity, matchIntegerCommand(rarityList)).collect(Collectors.toList());
		}
		displayList = filterElement(displayList, rarity, matchIntegerCommand(rarityList));
		displayList = filterElement(displayList, distance, matchStringCommand(Distance.values(), distanceList));
		displayList = filterElement(displayList, handle, matchStringCommand(Handle.values(), handleList));
		displayList = filterElement(displayList, element, anyMatchCommand(Element.values(), elementList));
		return filterElement(displayList, target, matchIntegerCommand(targetList)).collect(Collectors.toList());
	}
	
	Stream<Integer> filterElement(Stream<Integer> displayList, JRadioButton[] radio, BiPredicate<String, Integer> matchMethod){
		if(selectCheck(radio)) {
			return displayList;
		}
		return displayList.filter(displayIndex -> activeRadioCommand(radio).anyMatch(command -> matchMethod.test(command, displayIndex)));
	}
	
	boolean selectCheck(JRadioButton[] radio) {
		return !Stream.of(radio).map(AbstractButton::isSelected).anyMatch(i -> i);
	}
	
	Stream<String> activeRadioCommand(JRadioButton[] radio){
		return Stream.of(radio).filter(AbstractButton::isSelected).map(AbstractButton::getActionCommand);
	}
	
	BiPredicate<String, Integer> matchIntegerCommand(List<Integer> dataList){
		return (command, displayIndex) -> dataList.get(displayIndex) == Integer.parseInt(command);
	}
	
	<T extends Enum<T> & StatusText>BiPredicate<String, Integer> matchStringCommand(T[] dafualtData, List<T> dataList){
		return (command, displayIndex) -> dafualtData[dataList.get(displayIndex).getId()].getText().equals(command);
	}
	
	<T extends Enum<T> & StatusText>BiPredicate<String, Integer> anyMatchCommand(T[] dafualtData, List<List<T>> dataList){
		return (command, displayIndex) -> dataList.get(displayIndex).stream().map(i -> i.getId()).anyMatch(i -> dafualtData[i].getText().equals(command));
	}
}