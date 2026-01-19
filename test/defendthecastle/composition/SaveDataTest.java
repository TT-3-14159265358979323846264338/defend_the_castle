package defendthecastle.composition;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.MockedConstruction;

import savedata.SaveComposition;
import savedata.SaveHoldItem;

class SaveDataTest {
	private SaveData SaveData;
	private MockedConstruction<SaveHoldItem> mockSaveHoldItem;
	private MockedConstruction<SaveComposition> mockSaveComposition;

	@BeforeEach
	void setUp() throws Exception {
		mockSaveHoldItem = createMockSaveHoldItem();
		mockSaveComposition = createmMockSaveComposition();
		SaveData = spy(new SaveData());
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
		return Arrays.asList(8, 0);
	}
	
	List<Integer> defaultWeaponNumber(){
		return Arrays.asList(0, 1);
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
			return IntStream.range(0, 8).mapToObj(i -> Arrays.asList(number, number, number)).toList();
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
}