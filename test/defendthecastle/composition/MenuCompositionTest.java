package defendthecastle.composition;

import static custommatcher.CustomMatcher.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import defaultdata.DefaultUnit;
import defendthecastle.MainFrame;
import screendisplay.DisplayStatus;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class MenuCompositionTest {
	@Mock
	private MainFrame MainFrame;
	
	private MenuComposition MenuComposition;

	@BeforeEach
	void setUp() throws Exception {
		MenuComposition = spy(new MenuComposition(MainFrame));
	}

	/**
	 * ユニット画像が全て取り込まれていることを確認。<br>
	 * コアと左武器はnull禁止で全て取り込まれている必要がある。
	 * 右武器はnullの可能性があるので、Listの要素数が左武器と一致することを確認。
	 */
	@Test
	void testVariables() {
		assertThat(MenuComposition.getRightWeaponList().size(), is(MenuComposition.getLeftWeaponList().size()));
		assertThat(MenuComposition.getCeterCoreList(), everyItem(notNullValue()));
		assertThat(MenuComposition.getLeftWeaponList(), everyItem(notNullValue()));
	}
	
	/**
	 * MouseListenerが設定されていることを確認。<br>
	 * 全てのComponentが追加されているか確認。<br>
	 * ButtonにActionListenerが、ScrollにViewが設定されていることを確認。
	 */
	@Test
	void testMenuComposition() {
		JLabel[] allLabel = labelArray();
		JButton[] allButton = buttonArray();
		JScrollPane[] allScroll = scrollArray();
		assertThat(MenuComposition.getMouseListeners(), notNullValue());
		assertThat(MenuComposition.getComponents(), allOf(hasAllItemInArray(allLabel),
															hasAllItemInArray(allButton),
															hasAllItemInArray(allScroll)));
		Stream.of(allButton).forEach(this::assertActionListeners);
		Stream.of(allScroll).forEach(this::assertView);
	}
	
	JLabel[] labelArray() {
		return new JLabel[] {MenuComposition.getCompositionNameLabel(),
				MenuComposition.getCompositionLabel(),
				MenuComposition.getTypeLabel()};
	}
	
	JButton[] buttonArray() {
		return new JButton[] {MenuComposition.getNewButton(),
				MenuComposition.getRemoveButton(),
				MenuComposition.getSwapButton(),
				MenuComposition.getNameChangeButton(),
				MenuComposition.getSaveButton(),
				MenuComposition.getLoadButton(),
				MenuComposition.getResetButton(),
				MenuComposition.getReturnButton(),
				MenuComposition.getSwitchButton(),
				MenuComposition.getSortButton()
		};
	}
	
	JScrollPane[] scrollArray() {
		return new JScrollPane[] {MenuComposition.getCompositionScroll(),
				MenuComposition.getItemScroll()};
	}
	
	void assertActionListeners(JButton button) {
		assertThat(button.getActionListeners(), not(emptyArray()));
	}
	
	void assertView(JScrollPane scroll) {
		assertThat(scroll.getViewport().getView(), notNullValue());
	}
	
	/**
	 * 一定時間処理を停止させた後、スクロール位置を表示していることを確認。
	 * Threadがモック不可のため、停止については検証していない。
	 */
	@Test
	void testDelaySelect() {
		JList<String> mockJList = createMockJList();
		MenuComposition.delaySelect();
		verify(mockJList).ensureIndexIsVisible(anyInt());
	}
	
	/**
	 * JLabel, JButtonにテキストが設定されており、テキストの全文が表示可能であるか確認。
	 * 選択した編成とその編成でのユニット数計算を呼び出していることを確認。
	 */
	@Test
	void testPaintComponent() {
		SaveData mockSaveData = createMockSaveData();
		MenuComposition.paintComponent(brankGraphics());
		Stream.of(labelArray()).forEach(this::assertText);
		Stream.of(buttonArray()).forEach(this::assertText);
		verify(mockSaveData).selectNumberUpdate(anyInt());
		verify(mockSaveData).countNumber();
	}
	
	SaveData createMockSaveData() {
		SaveData mockSaveData = mock(SaveData.class);
		doReturn(Arrays.asList("test")).when(mockSaveData).getCompositionNameList();
		doReturn(Arrays.asList(0, 1)).when(mockSaveData).getCoreNumberList();
		doReturn(Arrays.asList(0, 1)).when(mockSaveData).getWeaponNumberList();
		MenuComposition.setSaveData(mockSaveData);
		return mockSaveData;
	}
	
	BufferedImage brankBufferedImage() {
		return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	}
	
	Graphics brankGraphics() {
		return brankBufferedImage().createGraphics();
	}
	
	void assertText(JComponent comp) {
		assertThat(comp, displayAllText());
	}
	
	/**
	 * 武器とコア合計3個を表示することを確認。
	 */
	@Test
	void testDrawCompositionOfDrawingAll() {
		createMockUnitList(0);
		creatBranckImage();
		Graphics mockGraphics = createMockGraphics();
		MenuComposition.drawComposition(mockGraphics);
		verify(mockGraphics, times(3)).drawImage(Mockito.any(Image.class), anyInt(), anyInt(), Mockito.any(ImageObserver.class));
	}
	
	Graphics createMockGraphics() {
		return mock(Graphics.class);
	}
	
	/**
	 * 武器画像がない時(画像リストのindexが-1)、コアのみを表示することを確認。
	 */
	@Test
	void testDrawCompositionOfDrawingCore() {
		createMockUnitList(-1);
		creatBranckImage();
		Graphics mockGraphics = createMockGraphics();
		MenuComposition.drawComposition(mockGraphics);
		verify(mockGraphics, times(1)).drawImage(Mockito.any(Image.class), anyInt(), anyInt(), Mockito.any(ImageObserver.class));
	}
	
	void createMockUnitList(int index) {
		SaveData mockSaveData = createMockSaveData();
		List<?> mockList = mock(List.class);
		doReturn(mockList).when(mockSaveData).getActiveCompositionList();
		doReturn(1).when(mockList).size();
		doReturn(mockList).when(mockSaveData).getActiveUnit(anyInt());
		doReturn(index).when(mockList).get(anyInt());
		doReturn(0).when(mockList).get(DefaultUnit.CORE);
	}
	
	void creatBranckImage() {
		List<BufferedImage> imageList = Arrays.asList(brankBufferedImage());
		MenuComposition.setRightWeaponList(imageList);
		MenuComposition.setCeterCoreList(imageList);
		MenuComposition.setLeftWeaponList(imageList);
	}
	
	/**
	 * 新たな編成を追加した後、Scrollを更新したことを確認。
	 */
	@Test
	void testNewButtonAction() {
		SaveData mockcSaveData = createMockSaveData();
		InOrder InOrder = inOrder(mockcSaveData, MenuComposition); 
		MenuComposition.newButtonAction(createMockActionEvent());
		InOrder.verify(mockcSaveData).addNewComposition();
		InOrder.verify(MenuComposition).modelUpdate();
	}
	
	/**
	 * 編成削除をした後に、Scrollを更新したことを確認。
	 */
	@Test
	void testRemoveButtonAction() {
		SaveData mockcSaveData = createMockSaveData();
		InOrder InOrder = inOrder(mockcSaveData, MenuComposition); 
		MenuComposition.removeButtonAction(createMockActionEvent());
		InOrder.verify(mockcSaveData).removeComposition(Mockito.any(int[].class));
		InOrder.verify(MenuComposition).modelUpdate();
	}
	
	/**
	 * 編成を入れ替えた後に、Scrollを更新したことを確認。
	 */
	@Test
	void testSwapButtonAction() {
		SaveData mockcSaveData = createMockSaveData();
		InOrder InOrder = inOrder(mockcSaveData, MenuComposition); 
		MenuComposition.swapButtonAction(createMockActionEvent());
		InOrder.verify(mockcSaveData).swapComposition(anyInt(), anyInt());
		InOrder.verify(MenuComposition).modelUpdate();
	}
	
	/**
	 * 有効な名称が入力されれば、その後に、Scrollを更新したことを確認。
	 */
	@Test
	void testNameChangeButtonActionValidName() {
		SaveData mockcSaveData = createMockSaveData();
		doReturn("test").when(mockcSaveData).changeCompositionName();
		InOrder InOrder = inOrder(mockcSaveData, MenuComposition); 
		MenuComposition.nameChangeButtonAction(createMockActionEvent());
		InOrder.verify(mockcSaveData).changeCompositionName();
		InOrder.verify(MenuComposition).modelUpdate();
	}
	
	/**
	 * セーブ操作が実行されたことを確認。
	 */
	@Test
	void testSaveButtonAction() {
		SaveData mockcSaveData = createMockSaveData();
		MenuComposition.saveButtonAction(createMockActionEvent());
		verify(mockcSaveData).saveProcessing();
	}
	
	/**
	 * ロードした後に、Scrollを更新したことを確認。
	 */
	@Test
	void testLoadButtonAction() {
		SaveData mockcSaveData = createMockSaveData();
		InOrder InOrder = inOrder(mockcSaveData, MenuComposition); 
		MenuComposition.loadButtonAction(createMockActionEvent());
		InOrder.verify(mockcSaveData).loadProcessing();
		InOrder.verify(MenuComposition).modelUpdate();
	}
	
	/**
	 * 編成のリセット操作が実行されたことを確認。
	 */
	@Test
	void testResetButtonAction() {
		SaveData mockcSaveData = createMockSaveData();
		MenuComposition.resetButtonAction(createMockActionEvent());
		verify(mockcSaveData).resetComposition();
	}
	
	/**
	 * 画面を戻す時にはメインメニューへの切り替えが実行されたことを確認。
	 */
	@Test
	void testReturnButtonActionExecution() {
		SaveData mockcSaveData = createMockSaveData();
		doReturn(true).when(mockcSaveData).returnProcessing();
		MenuComposition.returnButtonAction(createMockActionEvent());
		verify(MainFrame).mainMenuDraw();
	}
	
	/**
	 * 画面を戻す時にはメインメニューへの切り替えが実行されたことを確認。
	 */
	@Test
	void testReturnButtonActionUnexecuted() {
		SaveData mockcSaveData = createMockSaveData();
		doReturn(false).when(mockcSaveData).returnProcessing();
		MenuComposition.returnButtonAction(createMockActionEvent());
		verify(MainFrame, never()).mainMenuDraw();
	}
	
	/**
	 * 表示するパネルが変更したことを確認。
	 */
	@Test
	void testSwitchButtonAction() {
		Component oldComponent = getViewComponent();
		MenuComposition.switchButtonAction(createMockActionEvent());
		assertThat(getViewComponent(), not(oldComponent));
	}
	
	Component getViewComponent() {
		return MenuComposition.getItemScroll().getViewport().getView();
	}
	
	/**
	 * コア表示時はコアの表示リストを変更をしたことを確認。
	 */
	@SuppressWarnings("unchecked")
	@Test
	void testSortButtonActionCore() {
		ImagePanel mockImagePanel = creteMockImagePanel();
		MenuComposition.setCoreImagePanel(mockImagePanel);
		createMockDisplayListCreation();
		MenuComposition.sortButtonAction(createMockActionEvent());
		verify(mockImagePanel).updateList(Mockito.any(List.class));
	}
	
	/**
	 * 武器表示時は武器の表示リストを変更をしたことを確認。
	 */
	@SuppressWarnings("unchecked")
	@Test
	void testSortButtonActionWeapon() {
		ImagePanel mockImagePanel = creteMockImagePanel();
		MenuComposition.setWeaponImagePanel(mockImagePanel);
		createMockDisplayListCreation();
		MenuComposition.sortButtonAction(createMockActionEvent());
		verify(mockImagePanel).updateList(Mockito.any(List.class));
	}
	
	ImagePanel creteMockImagePanel() {
		ImagePanel mockImagePanel = mock(ImagePanel.class);
		createMockView(mockImagePanel);
		return mockImagePanel;
	}
	
	void createMockView(ImagePanel mockImagePanel) {
		JScrollPane mockScroll = mock(JScrollPane.class);
		JViewport mockViewport = mock(JViewport.class);
		doReturn(mockViewport).when(mockScroll).getViewport();
		doReturn(mockImagePanel).when(mockViewport).getView();
		MenuComposition.setItemScroll(mockScroll);
	}
	
	void createMockDisplayListCreation() {
		DisplayListCreation mockDisplayListCreation = mock(DisplayListCreation.class);
		doReturn(Arrays.asList()).when(mockDisplayListCreation).getCoreDisplayList();
		doReturn(Arrays.asList()).when(mockDisplayListCreation).getWeaponDisplayList();
		MenuComposition.setDisplayListCreation(mockDisplayListCreation);
	}
	
	ActionEvent createMockActionEvent() {
		return mock(ActionEvent.class);
	}
	
	/**
	 * Scroll用のModelを初期化した後、再度要素の追加と選択を行うことを確認。
	 */
	@Test
	void testModelUpdate() {
		DefaultListModel<String> mockModel = createMockModel();
		JList<String> mockJList = createMockJList();
		InOrder InOrder = inOrder(mockModel, mockJList);
		createMockSaveData();
		MenuComposition.modelUpdate();
		InOrder.verify(mockModel).clear();
		InOrder.verify(mockModel).addElement(anyString());
		InOrder.verify(mockJList).setSelectedIndex(anyInt());
	}
	
	DefaultListModel<String> createMockModel(){
		@SuppressWarnings("unchecked")
		DefaultListModel<String> mockModel = mock(DefaultListModel.class);
		MenuComposition.setCompositionListModel(mockModel);
		return mockModel;
	}
	
	JList<String> createMockJList(){
		@SuppressWarnings("unchecked")
		JList<String> mockJList = mock(JList.class);
		MenuComposition.setCompositionJList(mockJList);
		return mockJList;
	}
	
	/**
	 * x座標は2の倍数ごとに値がループしていることを確認。
	 */
	@Test
	void testGetPositionX() {
		List<Integer> list = new ArrayList<>();
		IntStream.range(0, 10).forEach(i -> list.add(MenuComposition.getPositionX(i)));
		assertThat(list, periodicChange(2));
	}
	
	/**
	 * y座標は2の倍数ごとに同じ値が並んでいることを確認。
	 */
	@Test
	void testGetPositionY() {
		List<Integer> list = new ArrayList<>();
		IntStream.range(0, 10).forEach(i -> list.add(MenuComposition.getPositionY(i)));
		assertThat(list, repeatingPattern(2));
	}
	
	/**
	 * クリック地点にユニットが存在していれば、ユニット操作メソッドを行うことを確認。
	 */
	@Test
	void testMousePressedOperationUnit() {
		MockedConstruction<ValueRange> mockValueRange = createMockValueRange(true);
		createMockUnitList(0);
		doNothing().when(MenuComposition).unitOperation(anyInt());
		MenuComposition.mousePressed(createMockMouseEvent());
		verify(MenuComposition, times(1)).unitOperation(anyInt());
		mockValueRange.close();
	}
	
	/**
	 * クリック地点にユニットが存在していなければ、ユニット操作メソッドを行わないことを確認。
	 */
	@Test
	void testMousePressedNotOperationUnit() {
		MockedConstruction<ValueRange> mockValueRange = createMockValueRange(false);
		createMockUnitList(0);
		doNothing().when(MenuComposition).unitOperation(anyInt());
		MenuComposition.mousePressed(createMockMouseEvent());
		verify(MenuComposition, never()).unitOperation(anyInt());
		mockValueRange.close();
	}
	
	MockedConstruction<ValueRange> createMockValueRange(boolean exist){
		return mockConstruction(ValueRange.class,
				(mock, context) -> doReturn(exist).when(mock).isValidIntValue(anyLong()));
	}
	
	MouseEvent createMockMouseEvent() {
		return mock(MouseEvent.class);
	}
	
	/**
	 * コアが選択されていて所持数が1以上なら、コアの変更操作を行うことを確認。
	 */
	@Test
	void testUnitOperationHavingCoreSelected() {
		ImagePanel mockImagePanel = creteMockImagePanel();
		doReturn(1).when(mockImagePanel).getSelectNumber();
		MenuComposition.setCoreImagePanel(mockImagePanel);
		SaveData mockSaveData = createMockSaveData();
		MenuComposition.unitOperation(0);
		verify(mockSaveData).changeCore(anyInt(), anyInt());
	}
	
	/**
	 * コアが選択されていて所持数が0以下なら、コアの変更操作を行わないことを確認。
	 */
	@Test
	void testUnitOperationNotHavingCoreSelected() {
		ImagePanel mockImagePanel = creteMockImagePanel();
		doReturn(0).when(mockImagePanel).getSelectNumber();
		MenuComposition.setCoreImagePanel(mockImagePanel);
		SaveData mockSaveData = createMockSaveData();
		MenuComposition.unitOperation(0);
		verify(mockSaveData, never()).changeCore(anyInt(), anyInt());
	}
	
	/**
	 * コアが選択されていなければ、ステータス表示を行うことを確認。
	 * ステータス表示には、武器とコア合計3個を表示可能であることを確認。
	 */
	@SuppressWarnings("unchecked")
	@Test
	void testUnitOperationCoreNotSelected() {
		MockedConstruction<DisplayStatus> mockDisplayStatus = createMockDisplayStatus();
		ImagePanel mockImagePanel = creteMockImagePanel();
		doReturn(-1).when(mockImagePanel).getSelectNumber();
		MenuComposition.setCoreImagePanel(mockImagePanel);
		createMockSaveData();
		createMockUnitList(0);
		creatBranckImage();
		MenuComposition.unitOperation(0);
		verify(mockDisplayStatus.constructed().get(0)).unit(Mockito.any(BufferedImage.class), Mockito.any(List.class));
		mockDisplayStatus.close();
	}
	
	/**
	 * 武器が選択されていて所持数が1以上なら、コアの変更操作を行うことを確認。
	 */
	@Test
	void testUnitOperationHavingWeaponSelected() {
		ImagePanel mockImagePanel = creteMockImagePanel();
		doReturn(1).when(mockImagePanel).getSelectNumber();
		MenuComposition.setWeaponImagePanel(mockImagePanel);
		SaveData mockSaveData = createMockSaveData();
		MenuComposition.unitOperation(0);
		verify(mockSaveData).changeWeapon(anyInt(), anyInt());
	}
	
	/**
	 * 武器が選択されていて所持数が0以下なら、武器の変更操作を行わないことを確認。
	 */
	@Test
	void testUnitOperationNotHavingWeaponSelected() {
		ImagePanel mockImagePanel = creteMockImagePanel();
		doReturn(0).when(mockImagePanel).getSelectNumber();
		MenuComposition.setWeaponImagePanel(mockImagePanel);
		SaveData mockSaveData = createMockSaveData();
		MenuComposition.unitOperation(0);
		verify(mockSaveData, never()).changeWeapon(anyInt(), anyInt());
	}
	
	/**
	 * 武器が選択されていなければ、ステータス表示を行うことを確認。
	 * ステータス表示には、武器がない時(画像リストのindexが-1)、コアのみを表示可能であることを確認。
	 */
	@SuppressWarnings("unchecked")
	@Test
	void testUnitOperationWeaponNotSelected() {
		MockedConstruction<DisplayStatus> mockDisplayStatus = createMockDisplayStatus();
		ImagePanel mockImagePanel = creteMockImagePanel();
		doReturn(-1).when(mockImagePanel).getSelectNumber();
		MenuComposition.setWeaponImagePanel(mockImagePanel);
		createMockSaveData();
		createMockUnitList(-1);
		creatBranckImage();
		MenuComposition.unitOperation(0);
		verify(mockDisplayStatus.constructed().get(0)).unit(Mockito.any(BufferedImage.class), Mockito.any(List.class));
		mockDisplayStatus.close();
	}
	
	@SuppressWarnings("unchecked")
	MockedConstruction<DisplayStatus> createMockDisplayStatus(){
		return mockConstruction(DisplayStatus.class,
				(mock, context) -> doNothing().when(mock).unit(Mockito.any(BufferedImage.class), Mockito.any(List.class)));
	}
}