package defendthecastle.composition;

import static custommatcher.CustomMatcher.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedConstruction.MockInitializer;
import org.mockito.Mockito;

import defaultdata.DefaultUnit;
import defendthecastle.MainFrame;

class MenuCompositionTest {
	private MainFrame MainFrame;
	private MenuComposition MenuComposition;

	@BeforeEach
	void setUp() throws Exception {
		MainFrame = mock(MainFrame.class);
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
	 * JLabel, JButtonにテキストが設定されており、テキストの全文が表示可能であるか確認。
	 * 選択した編成とその編成でのユニット数計算を呼び出していることを確認。
	 */
	@Test
	void testPaintComponent() {
		SaveData SaveData = mock(SaveData.class);
		MenuComposition.setSaveData(SaveData);
		MenuComposition.paintComponent(brankGraphics());
		Stream.of(labelArray()).forEach(this::assertText);
		Stream.of(buttonArray()).forEach(this::assertText);
		verify(SaveData).selectNumberUpdate(anyInt());
		verify(SaveData).countNumber();
	}
	
	Graphics brankGraphics() {
		return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).createGraphics();
	}
	
	void assertText(JComponent comp) {
		assertThat(comp, displayAllText());
	}
	
	/**
	 * 武器とコア合計3個を表示することを確認。
	 */
	@Test
	void testDrawCompositionOfDrawingAll() {
		setUnitListIndex(0);
		setImage();
		Graphics mockGraphics = mock(Graphics.class);
		MenuComposition.drawComposition(mockGraphics);
		verify(mockGraphics, times(3)).drawImage(Mockito.any(Image.class), anyInt(), anyInt(), Mockito.any(ImageObserver.class));
	}
	
	/**
	 * 武器画像がない時(画像リストのindexが-1)、コアのみを表示することを確認。
	 */
	@Test
	void testDrawCompositionOfDrawingCore() {
		setUnitListIndex(-1);
		setImage();
		Graphics mockGraphics = mock(Graphics.class);
		MenuComposition.drawComposition(mockGraphics);
		verify(mockGraphics, times(1)).drawImage(Mockito.any(Image.class), anyInt(), anyInt(), Mockito.any(ImageObserver.class));
	}
	
	void setUnitListIndex(int index) {
		SaveData mockSaveData = mock(SaveData.class);
		List<?> mockList = mock(List.class);
		MenuComposition.setSaveData(mockSaveData);
		doReturn(mockList).when(mockSaveData).getActiveCompositionList();
		doReturn(1).when(mockList).size();
		doReturn(mockList).when(mockSaveData).getActiveUnit(anyInt());
		doReturn(index).when(mockList).get(anyInt());
		doReturn(0).when(mockList).get(DefaultUnit.CORE);
	}
	
	void setImage() {
		List<BufferedImage> imageList = Arrays.asList(mock(BufferedImage.class));
		MenuComposition.setRightWeaponList(imageList);
		MenuComposition.setCeterCoreList(imageList);
		MenuComposition.setLeftWeaponList(imageList);
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
		MockedConstruction<ValueRange> mockValueRange = mockConstruction(ValueRange.class, defineMockInitializer(true));
		setUnitListIndex(0);
		doNothing().when(MenuComposition).unitOperation(anyInt());
		MenuComposition.mousePressed(mock(MouseEvent.class));
		verify(MenuComposition, times(1)).unitOperation(anyInt());
		mockValueRange.close();
	}
	
	/**
	 * クリック地点にユニットが存在していなければ、ユニット操作メソッドを行わないことを確認。
	 */
	@Test
	void testMousePressedNotOperationUnit() {
		MockedConstruction<ValueRange> mockValueRange = mockConstruction(ValueRange.class, defineMockInitializer(false));
		setUnitListIndex(0);
		doNothing().when(MenuComposition).unitOperation(anyInt());
		MenuComposition.mousePressed(mock(MouseEvent.class));
		verify(MenuComposition, never()).unitOperation(anyInt());
		mockValueRange.close();
	}
	
	MockInitializer<ValueRange> defineMockInitializer(boolean exist){
		return (mock, context) -> doReturn(exist).when(mock).isValidIntValue(anyLong());
	}
	
	/**
	 * 
	 */
	@Test
	void testUnitOperatio() {
		
	}
}