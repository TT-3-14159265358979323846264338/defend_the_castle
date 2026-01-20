package defendthecastle.composition;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import javax.swing.JOptionPane;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import savedata.SaveComposition;
import savedata.SaveHoldItem;

class SaveDataTest {
	@Spy
	private SaveData SaveData;
	
	private MockedConstruction<SaveHoldItem> mockSaveHoldItem;
	private MockedConstruction<SaveComposition> mockSaveComposition;

	@BeforeEach
	void setUp() throws Exception {
		mockSaveHoldItem = createMockSaveHoldItem();
		mockSaveComposition = createmMockSaveComposition();
		MockitoAnnotations.openMocks(this);
	}
	
	MockedConstruction<SaveHoldItem> createMockSaveHoldItem(){
		return mockConstruction(SaveHoldItem.class,
									(mock, context) -> {
										doNothing().when(mock).load();
										doReturn(defaultCoreNumber()).when(mock).getCoreNumberList();
										doReturn(defaultWeaponNumber()).when(mock).getWeaponNumberList();
									}
								);
	}
	
	SaveHoldItem getMockSaveHoldItem() {
		return mockSaveHoldItem.constructed().get(0);
	}
	
	List<Integer> defaultCoreNumber(){
		return Arrays.asList(8, 8);
	}
	
	List<Integer> defaultWeaponNumber(){
		return Arrays.asList(16, 16);
	}
	
	MockedConstruction<SaveComposition> createmMockSaveComposition(){
		return mockConstruction(SaveComposition.class,
									(mock, context) -> {
										doNothing().when(mock).load();
										doReturn(defaultComposition()).when(mock).getAllCompositionList();
										doReturn(defaultCompositionName()).when(mock).getCompositionNameList();
										doReturn(defaultSelectNumber()).when(mock).getSelectNumber();
									}
								);
	}
	
	SaveComposition getMockSaveComposition() {
		return mockSaveComposition.constructed().get(0);
	}
	
	List<List<List<Integer>>> defaultComposition(){
		Function<Integer, List<List<Integer>>> defaultUnits = number -> {
			return IntStream.range(0, 8).mapToObj(i -> Arrays.asList(number - 1, number, number - 1)).toList();
		};
		return IntStream.range(0, 2).mapToObj(i -> defaultUnits.apply(i)).toList();
	}
	
	List<String> defaultCompositionName(){
		return Arrays.asList("test1", "test2");
	}
	
	int defaultSelectNumber() {
		return 0;
	}

	@AfterEach
	void tearDown() throws Exception {
		mockSaveHoldItem.close();
		mockSaveComposition.close();
	}

	/**
	 * セーブデータのインスタンスを作成したことを確認。
	 */
	@Test
	void testSaveData() {
		assertThat(SaveData.getSaveHoldItem(), notNullValue());
		assertThat(SaveData.getSaveComposition(), notNullValue());
	}
	
	/**
	 * セーブデータのロードした後に取り込みを行ったことを確認。
	 */
	@Test
	void testLoad() {
		InOrder InOrder = inOrder(SaveData, getMockSaveHoldItem(), getMockSaveComposition());
		SaveData.load();
		InOrder.verify(getMockSaveHoldItem()).load();
		InOrder.verify(getMockSaveComposition()).load();
		InOrder.verify(SaveData).input();
	}
	
	/**
	 * スタブのデータが取り込まれていることを確認。
	 */
	@Test
	void testInput() {
		SaveData.input();
		assertThat(SaveData.getCoreNumberList(), is(defaultCoreNumber()));
		assertThat(SaveData.getWeaponNumberList(), is(defaultWeaponNumber()));
		assertThat(SaveData.getAllCompositionList(), is(defaultComposition()));
		assertThat(SaveData.getCompositionNameList(), is(defaultCompositionName()));
		assertThat(SaveData.getSelectNumber(), is(defaultSelectNumber()));
	}
	
	/**
	 * 編成のセーブが呼び出されたことを確認。
	 */
	@Test
	void testSave() {
		doNothing().when(getMockSaveComposition()).save(anyList(), anyList(), anyInt());
		SaveData.save();
		verify(getMockSaveComposition()).save(anyList(), anyList(), anyInt());
	}
	
	/**
	 * 全てのユニットに対して、元から保有している数から編成で使用した総数を引くことで、残りの未使用数を算出していることを確認。
	 * テストでは元保有数{@link #defaultCoreNumber}と{@link #defaultWeaponNumber}から{@link #defaultComposition}で使用した数を引いた値になっていることを確認している。
	 */
	@ParameterizedTest
	@ValueSource(ints = {0, 1})
	void testCountNumber(int selectNumber) {
		SaveData.selectNumberUpdate(selectNumber);
		SaveData.countNumber();
		assertThat(SaveData.getNowCoreNumberList(), is(Arrays.asList(selectNumber * 8, 8 - selectNumber * 8)));
		assertThat(SaveData.getNowWeaponNumberList(), is(Arrays.asList(16 - selectNumber * 16, 16)));
	}
	
	/**
	 * 新規編成作成メソッドが呼ばれ、編成変更がtrueになったことを確認。
	 */
	@Test
	void testAddNewComposition() {
		SaveData.setExistsChange(false);
		doNothing().when(getMockSaveComposition()).newComposition();
		SaveData.addNewComposition();
		verify(getMockSaveComposition()).newComposition();
		assertThat(SaveData.isExistsChange(), is(true));
	}
	
	/**
	 * 編成が1つしかなければ、メッセージを表示して編成変更は行わないことを確認。
	 * 確認ダイアログで処理の実行を行わなければ、編成変更は行わないことを確認。
	 * 確認ダイアログで処理を実行すれば、編成除去を編成インデックスの逆順に行うことを確認。
	 */
	@ParameterizedTest
	@CsvSource({"1, -1", "2, -1", "2, 0"})
	void testRemoveComposition(int size, int dialogCode) {
		MockedStatic<JOptionPane> mockJOptionPane = createMockJOptionPane(dialogCode);
		createMockAllCompositionList(size);
		SaveData.setExistsChange(false);
		ArgumentCaptor<Integer> capture = createRemoveCaptor();
		int[] removeTarget = {1, 2, 4};
		SaveData.removeComposition(removeTarget);
		if(1 < size) {
			if(dialogCode == 0) {
				assertRemove(capture, removeTarget);
			}else {
				assertNotRemove(capture);
			}
		}else {
			mockJOptionPane.verify(() -> JOptionPane.showMessageDialog(any(), any()));
			assertNotRemove(capture);
		}
		mockJOptionPane.close();
	}
	
	void assertRemove(ArgumentCaptor<Integer> capture, int[] removeTarget) {
		List<Integer> reverseTarget = Arrays.stream(removeTarget).boxed().sorted(Collections.reverseOrder()).toList();
		assertThat(capture.getAllValues(), is(reverseTarget));
		assertThat(SaveData.isExistsChange(), is(true));
	}
	
	void assertNotRemove(ArgumentCaptor<Integer> capture) {
		assertThat(capture.getAllValues(), empty());
		assertThat(SaveData.isExistsChange(), is(false));
	}
	
	MockedStatic<JOptionPane> createMockJOptionPane(int dialogCode){
		MockedStatic<JOptionPane> mockJOptionPane = mockStatic(JOptionPane.class);
		mockJOptionPane.when(() -> JOptionPane.showMessageDialog(any(), any())).thenAnswer(invocation -> null);
		mockJOptionPane.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt(), anyInt())).thenReturn(dialogCode);
		return mockJOptionPane;
	}
	
	void createMockAllCompositionList(int size) {
		@SuppressWarnings("unchecked")
		List<List<List<Integer>>> mockAllCompositionList = mock(List.class);
		doReturn(size).when(mockAllCompositionList).size();
		SaveData.setAllCompositionList(mockAllCompositionList);
	}
	
	ArgumentCaptor<Integer> createRemoveCaptor(){
		ArgumentCaptor<Integer> capture = ArgumentCaptor.forClass(Integer.class);
		doNothing().when(getMockSaveComposition()).removeComposition(capture.capture());
		return capture;
	}
}