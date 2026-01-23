package defendthecastle.composition;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JOptionPane;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import defaultdata.DefaultUnit;
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

	@AfterEach
	void tearDown() throws Exception {
		mockSaveHoldItem.close();
		mockSaveComposition.close();
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
			return IntStream.range(0, 8).mapToObj(i -> Arrays.asList(number - 1, number, number - 1)).collect(Collectors.toList());
		};
		return IntStream.range(0, 2).mapToObj(i -> defaultUnits.apply(i)).collect(Collectors.toList());
	}
	
	List<String> defaultCompositionName(){
		return new ArrayList<>(Arrays.asList("test1", "test2"));
	}
	
	int defaultSelectNumber() {
		return 0;
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
		List<Integer> coreExpected = Arrays.asList(selectNumber * 8, 8 - selectNumber * 8);
		List<Integer> weaponExpected = Arrays.asList(16 - selectNumber * 16, 16);
		SaveData.countNumber();
		assertThat(SaveData.getNowCoreNumberList(), is(coreExpected));
		assertThat(SaveData.getNowWeaponNumberList(), is(weaponExpected));
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
		assertChange(true);
	}
	
	void assertChange(boolean exists) {
		assertThat(SaveData.isExistsChange(), is(exists));
	}
	
	/**
	 * 編成が1つしかなければ、メッセージを表示して編成変更は行わないことを確認。
	 * 確認ダイアログで処理の実行を行わなければ、編成変更は行わないことを確認。
	 * 確認ダイアログで処理を実行すれば、編成除去を編成インデックスの逆順に行うことを確認。
	 */
	@ParameterizedTest
	@CsvSource({"1, -1", "2, -1", "2, 0"})
	void testRemoveComposition(int size, int dialogCode) {
		try(MockedStatic<JOptionPane> mockJOptionPane = createMockJOptionPaneOfConfirmDialog(dialogCode)){
			createMockAllCompositionList(size);
			SaveData.setExistsChange(false);
			ArgumentCaptor<Integer> capture = createRemoveCaptor();
			int[] removeTarget = {1, 2, 4};
			SaveData.removeComposition(removeTarget);
			if(1 < size) {
				if(canSelectDialog(dialogCode)) {
					assertRemove(capture, removeTarget);
				}else {
					assertNotRemove(capture);
				}
			}else {
				mockJOptionPane.verify(() -> JOptionPane.showMessageDialog(any(), any()));
				assertNotRemove(capture);
			}
		}
	}
	
	void assertRemove(ArgumentCaptor<Integer> capture, int[] removeTarget) {
		List<Integer> expected = Arrays.stream(removeTarget).boxed().sorted(Collections.reverseOrder()).toList();
		assertThat(capture.getAllValues(), is(expected));
		assertChange(true);
	}
	
	void assertNotRemove(ArgumentCaptor<Integer> capture) {
		assertThat(capture.getAllValues(), empty());
		assertChange(false);
	}
	
	MockedStatic<JOptionPane> createMockJOptionPaneOfConfirmDialog(int dialogCode){
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
	
	boolean canSelectDialog(int dialogCode) {
		return dialogCode == 0;
	}
	
	/**
	 * 1つしか選択されていない(max=min)時は、メッセージを表示して編成変更は行わないことを確認。
	 * 確認ダイアログで処理の実行を行わなければ、編成変更は行わないことを確認。
	 * 確認ダイアログで処理を実行すれば、編成と名称の入れ替えたことを確認。
	 */
	@ParameterizedTest
	@CsvSource({"0, 0, -1", "0, 1, 0", "0, 1, -1"})
	void testSwapComposition(int max, int min, int dialogCode) {
		try(MockedStatic<JOptionPane> mockJOptionPane = createMockJOptionPaneOfConfirmDialog(dialogCode)){
			List<List<List<Integer>>> defaultComposition = defaultComposition();
			List<String> defaultName = defaultCompositionName();
			SaveData.swapComposition(max, min);
			if(max == min) {
				mockJOptionPane.verify(() -> JOptionPane.showMessageDialog(any(), any()));
				assertChange(false);
			}else {
				if(canSelectDialog(dialogCode)) {
					assertSwap(max, min, defaultComposition, defaultName);
					assertChange(true);
				}else {
					assertChange(false);
				}
			}
		}
	}
	
	void assertSwap(int max, int min, List<List<List<Integer>>> defaultComposition, List<String> defaultName) {
		assertThat(SaveData.getAllCompositionList().get(max), is(defaultComposition.get(min)));
		assertThat(SaveData.getAllCompositionList().get(min), is(defaultComposition.get(max)));
		assertThat(SaveData.getCompositionNameList().get(max), is(defaultName.get(min)));
		assertThat(SaveData.getCompositionNameList().get(min), is(defaultName.get(max)));
	}
	
	/**
	 * 入力された編成名がnullやemptyなら、編成名の変更を行わないことを確認。
	 * 編成名が空白文字であれば、メッセージを表示して変更を行わないことを確認。
	 * 編成名が有効ならば編成名の変更を行うことを確認。
	 */
	@ParameterizedTest
	@ValueSource(strings = {"", " ", "　", "name"})
	@NullSource
	void testChangeCompositionName(String inputName) {
		try(MockedStatic<JOptionPane> mockJOptionPane = createMockJOptionPaneOfInputDialog(inputName)){
			String name = SaveData.changeCompositionName();
			if(inputName == null || inputName.isEmpty()) {
				assertThat(name, nullValue());
				assertChange(false);
			}else if(inputName.isBlank()) {
				mockJOptionPane.verify(() -> JOptionPane.showMessageDialog(any(), any()));
				assertThat(name, nullValue());
				assertChange(false);
			}else {
				assertThat(SaveData.getCompositionNameList().get(defaultSelectNumber()), is(inputName));
				assertChange(true);
			}
		}
	}
	
	MockedStatic<JOptionPane> createMockJOptionPaneOfInputDialog(String inputName){
		MockedStatic<JOptionPane> mockJOptionPane = mockStatic(JOptionPane.class);
		mockJOptionPane.when(() -> JOptionPane.showMessageDialog(any(), any())).thenAnswer(invocation -> null);
		mockJOptionPane.when(() -> JOptionPane.showInputDialog(any(), any(), anyString(), anyInt())).thenReturn(inputName);
		return mockJOptionPane;
	}
	
	/**
	 * 確認ダイアログで処理の実行を行わなければ、編成変更は行わないことを確認。
	 * 確認ダイアログで処理を実行すれば、セーブメソッドが呼ばれて、編成変更状況がfalseに切り替わることを確認。
	 */
	@ParameterizedTest
	@ValueSource(ints = {-1, 0})
	void testSaveProcessing(int dialogCode) {
		try(MockedStatic<JOptionPane> mockJOptionPane = createMockJOptionPaneOfConfirmDialog(dialogCode)){
			SaveData.setExistsChange(true);
			doNothing().when(getMockSaveComposition()).save(anyList(), anyList(), anyInt());
			SaveData.saveProcessing();
			if(dialogCode == 0) {
				verify(SaveData).save();
				assertChange(false);
			}else {
				assertChange(true);
			}
		}
	}
	
	/**
	 * 確認ダイアログで処理の実行を行わなければ、編成変更は行わないことを確認。
	 * 確認ダイアログで処理を実行すれば、ロードメソッドが呼ばれて、編成変更状況がfalseに切り替わることを確認。
	 */
	@ParameterizedTest
	@ValueSource(ints = {-1, 0})
	void testLoadProcessing(int dialogCode) {
		try(MockedStatic<JOptionPane> mockJOptionPane = createMockJOptionPaneOfConfirmDialog(dialogCode)){
			SaveData.setExistsChange(true);
			doNothing().when(getMockSaveComposition()).save(anyList(), anyList(), anyInt());
			SaveData.loadProcessing();
			if(dialogCode == 0) {
				verify(SaveData).load();
				assertChange(false);
			}else {
				assertChange(true);
			}
		}
	}
	
	/**
	 * 確認ダイアログで処理の実行を行わなければ、編成変更は行わないことを確認。
	 * 確認ダイアログで処理を実行すれば、現在の編成が初期値に戻ることを確認。
	 */
	@ParameterizedTest
	@ValueSource(ints = {-1, 0})
	void testResetProcessing(int dialogCode) {
		try(MockedStatic<JOptionPane> mockJOptionPane = createMockJOptionPaneOfConfirmDialog(dialogCode)){
			SaveData.setExistsChange(false);
			SaveData.selectNumberUpdate(1);
			SaveData.resetComposition();
			if(dialogCode == 0) {
				assertThat(SaveData.getAllCompositionList(), is(resetComposition()));
				assertChange(true);
			}else {
				assertChange(false);
			}
		}
	}
	
	List<List<List<Integer>>> resetComposition(){
		List<List<List<Integer>>> composition = defaultComposition();
		composition.set(1, composition.get(0));
		return composition;
	}
	
	/**
	 * 編成の変更が行われていなければ、trueを返却することを確認。
	 * 確認ダイアログで処理のキャンセルすれば、falseを返却することを確認。
	 * 確認ダイアログでセーブ実行しないならば、trueを返却することを確認。
	 * 確認ダイアログでセーブ実行すれば、セーブが呼ばれてtrueを返却することを確認。
	 */
	@ParameterizedTest
	@CsvSource({"false, -1", "true, -1", "true, 0", "true, 1"})
	void testReturnProcessing(boolean existsChange, int dialogCode) {
		try(MockedStatic<JOptionPane> mockJOptionPane = createMockJOptionPaneOfConfirmDialog(dialogCode)){
			SaveData.setExistsChange(existsChange);
			boolean canReturn = SaveData.returnProcessing();
			if(!existsChange) {
				assertThat(canReturn, is(true));
			}else if(dialogCode == 0){
				verify(SaveData).save();
				assertThat(canReturn, is(true));
			}else if(dialogCode == 1) {
				assertThat(canReturn, is(true));
			}else {
				assertThat(canReturn, is(false));
			}
		}
	}
	
	/**
	 * 指定したコアが変更されたことを確認。
	 */
	@Test
	void testChangeCore() {
		List<List<List<Integer>>> expected = coreChange();
		SaveData.changeCore(0, 5);
		assertThat(SaveData.getAllCompositionList(), is(expected));
		assertChange(true);
	}
	
	List<List<List<Integer>>> coreChange(){
		List<List<List<Integer>>> composition = defaultComposition();
		composition.get(0).get(0).set(1, 5);
		return composition;
	}
	
	/**
	 * 選択された武器が両手武器なら、右武器をなしにして左武器を変更する。
	 * 片手武器なら、現在のユニットの装備状況によって結果が異なる。<br>
	 * 左手に武器を未所持なら、変更メソッドを呼び出してダイアログで指定した部位を変更する。
	 * 左武器が片手武器なら、左手に武器を未所持と同様の対応をする。
	 * 左武器が両手武器なら、右武器を変更(dialogCode = 1)すると、左武器をなしして右武器を変更する。
	 * 左武器を変更(dialogCode = 0)すると、左武器のみ変更する。
	 * 変更をキャンセルすれば、変更は行われない。
	 */
	@ParameterizedTest
	@MethodSource("weaponChangeSource")
	void testChangeWeapon(int selectWeapon, int leftWeapon, int dialogCode) {
		try(MockedStatic<JOptionPane> mockJOptionPane = createMockJOptionPaneOfOptionDialog(dialogCode)){
			SaveData.setExistsChange(false);
			SaveData.setAllCompositionList(weaponChangeComposition());
			if(dialogCode != -1) {
				SaveData.getActiveCompositionList().get(0).set(DefaultUnit.LEFT_WEAPON, leftWeapon);
			}
			SaveData.changeWeapon(0, selectWeapon);
			assertOperation(selectWeapon, leftWeapon, dialogCode);
			assertChange(true);
		}
	}
	
	static Stream<Arguments> weaponChangeSource(){
		return Stream.of(
				Arguments.of(DefaultUnit.BOW, DefaultUnit.NO_WEAPON, -1),
				Arguments.of(DefaultUnit.SWORD, DefaultUnit.NO_WEAPON, 0),
				Arguments.of(DefaultUnit.SWORD, DefaultUnit.SWORD, 0),
				Arguments.of(DefaultUnit.SWORD, DefaultUnit.BOW, -1),
				Arguments.of(DefaultUnit.SWORD, DefaultUnit.BOW, 0),
				Arguments.of(DefaultUnit.SWORD, DefaultUnit.BOW, 1));
	}
	
	MockedStatic<JOptionPane> createMockJOptionPaneOfOptionDialog(int dialogCode){
		MockedStatic<JOptionPane> mockJOptionPane = mockStatic(JOptionPane.class);
		mockJOptionPane.when(() -> JOptionPane.showOptionDialog(any(), any(), anyString(), anyInt(), anyInt(), any(), any(), any())).thenReturn(dialogCode);
		return mockJOptionPane;
	}
	
	void assertOperation(int selectWeapon, int leftWeapon, int dialogCode) {
		if(DefaultUnit.WEAPON_DATA_MAP.get(selectWeapon).getHandle() == DefaultUnit.BOTH) {
			assertThat(SaveData.getAllCompositionList(), is(changeToBoth(selectWeapon)));
			return;
		}
		if(leftWeapon == DefaultUnit.NO_WEAPON){
			verify(SaveData).change(anyInt(), anyInt());
			return;
		}
		if(DefaultUnit.WEAPON_DATA_MAP.get(leftWeapon).getHandle() == DefaultUnit.ONE) {
			verify(SaveData).change(anyInt(), anyInt());
			return;
		}
		if(dialogCode == 1) {
			assertThat(SaveData.getAllCompositionList(), is(changeFromBothOnRight(selectWeapon)));
			return;
		}
		if(dialogCode == 0){
			assertThat(SaveData.getAllCompositionList(), is(changeFromBothOnLeft(selectWeapon)));
			return;
		}assertThat(SaveData.getAllCompositionList(), is(weaponChangeComposition()));
	}
	
	List<List<List<Integer>>> changeToBoth(int selectWeapon){
		List<List<List<Integer>>> expected = weaponChangeComposition();
		expected.get(0).get(0).set(DefaultUnit.RIGHT_WEAPON, DefaultUnit.NO_WEAPON);
		expected.get(0).get(0).set(DefaultUnit.LEFT_WEAPON, selectWeapon);
		return expected;
	}
	
	List<List<List<Integer>>> changeFromBothOnRight(int selectWeapon){
		List<List<List<Integer>>> expected = weaponChangeComposition();
		expected.get(0).get(0).set(DefaultUnit.RIGHT_WEAPON, selectWeapon);
		expected.get(0).get(0).set(DefaultUnit.LEFT_WEAPON, DefaultUnit.NO_WEAPON);
		return expected;
	}
	
	List<List<List<Integer>>> changeFromBothOnLeft(int selectWeapon){
		List<List<List<Integer>>> expected = weaponChangeComposition();
		expected.get(0).get(0).set(DefaultUnit.LEFT_WEAPON, selectWeapon);
		return expected;
	}
	
	List<List<List<Integer>>> weaponChangeComposition(){
		Function<Integer, List<List<Integer>>> defaultUnits = number -> {
			return IntStream.range(0, 8).mapToObj(i -> Arrays.asList(5, 0, 5)).collect(Collectors.toList());
		};
		return IntStream.range(0, 2).mapToObj(i -> defaultUnits.apply(i)).collect(Collectors.toList());
	}
	
	/**
	 * 右武器を変更(dialogCode = 1)すると、右武器のみ変更する。
	 * 左武器を変更(dialogCode = 0)すると、左武器のみ変更する。
	 * キャンセル(dialogCode = -1)すると、変更を行わない。
	 */
	@ParameterizedTest
	@ValueSource(ints = {-1, 0, 1})
	void testChange(int dialogCode) {
		try(MockedStatic<JOptionPane> mockJOptionPane = createMockJOptionPaneOfOptionDialog(dialogCode)){
			SaveData.setAllCompositionList(weaponChangeComposition());
			SaveData.change(0, 1);
			if(dialogCode == 1) {
				assertThat(SaveData.getAllCompositionList(), is(onlyOneSideChange(DefaultUnit.RIGHT_WEAPON)));
			}else if(dialogCode == 0) {
				assertThat(SaveData.getAllCompositionList(), is(onlyOneSideChange(DefaultUnit.LEFT_WEAPON)));
			}else {
				assertThat(SaveData.getAllCompositionList(), is(weaponChangeComposition()));
			}
		}
	}
	
	List<List<List<Integer>>> onlyOneSideChange(int position){
		List<List<List<Integer>>> expected = weaponChangeComposition();
		expected.get(0).get(0).set(position, 1);
		return expected;
	}
	
	/**
	 * 選択中の編成情報を返却することを確認。
	 */
	@Test
	void testGetActiveCompositionList() {
		List<List<Integer>> expected = defaultComposition().get(1);
		SaveData.selectNumberUpdate(1);
		assertThat(SaveData.getActiveCompositionList(), is(expected));
	}
	
	/**
	 * 選択中の編成のユニット情報を返却することを確認。
	 */
	@Test
	void testGetActiveUnit() {
		List<Integer> expected = defaultComposition().get(1).get(0);
		SaveData.selectNumberUpdate(1);
		assertThat(SaveData.getActiveUnit(0), is(expected));
	}
}