package defendthecastle.composition;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.junit.jupiter.MockitoExtension;

import screendisplay.DisplaySort;

@ExtendWith(MockitoExtension.class)
class DisplayListCreationTest {
	@Mock
	private SaveData SaveData;
	
	@InjectMocks
	private DisplayListCreation DisplayListCreation;
	
	/**
	 * 2つのDisplaySortインスタンスでcoreとweapon各1回ずつ初期化したことを確認。
	 */
	@Test
	void testDisplayListCreation() {
		MockedConstruction<DisplaySort> mockDisplaySort = createMockConstructionDisplaySort();
		DisplayListCreation = new DisplayListCreation(SaveData);
		assertThat(canCallAtLeastOne(mockDisplaySort, "core"), is(true));
		assertThat(canCallAtLeastOne(mockDisplaySort, "weapon"), is(true));
		mockDisplaySort.close();
	}
	
	@SuppressWarnings("unchecked")
	MockedConstruction<DisplaySort> createMockConstructionDisplaySort(){
		return mockConstruction(DisplaySort.class,
				(mock, context) -> {
					doNothing().when(mock).core(Mockito.any(List.class));
					doNothing().when(mock).weapon(Mockito.any(List.class));
				});
	}
	
	@SuppressWarnings("unchecked")
	boolean canCallAtLeastOne(MockedConstruction<DisplaySort> mockDisplaySort, String name) {
		for(DisplaySort DisplaySort: mockDisplaySort.constructed()) {
			try{
				if(name.equals("core")) {
					verify(DisplaySort).core(Mockito.any(List.class));
				}else {
					verify(DisplaySort).weapon(Mockito.any(List.class));
				}
				return true;
			}catch (MockitoAssertionError e) {
				continue;
			}
		}
		return false;
	}
	
	/**
	 * アイテムの所有数リストから、所有数が0ではないインデックスを格納したリスト返却するか確認。
	 */
	@ParameterizedTest
	@MethodSource("extractNonZeroIndex")
	void testGetDisplayList(List<Integer> origin, List<Integer> expected) {
		List<Integer> result = DisplayListCreation.getDisplayList(origin);
		assertThat(result, is(expected));
	}
	
	static Stream<Arguments> extractNonZeroIndex(){
		return Stream.of(
				Arguments.of(Arrays.asList(0, 1, 5, 3), Arrays.asList(1, 2, 3)),
				Arguments.of(Arrays.asList(1, 6, 4, 9), Arrays.asList(0, 1, 2, 3)),
				Arguments.of(Arrays.asList(2, 0, 2, 0, 3, 0), Arrays.asList(0, 2, 4)));
	}
	
	/**
	 * コアの表示リストを返却するか確認。
	 */
	@ParameterizedTest
	@MethodSource("displayList")
	void testGetCoreDisplayList(List<Integer> origin) {
		DisplaySort mockDisplaySort = createMockDisplaySort(origin);
		DisplayListCreation.setCoreDisplaySort(mockDisplaySort);
		List<Integer> expected = DisplayListCreation.getCoreDisplayList();
		assertThat(expected, is(origin));
	}
	
	/**
	 * 武器の表示リストを返却するか確認。
	 */
	@ParameterizedTest
	@MethodSource("displayList")
	void testGetWeaponDisplayList(List<Integer> origin) {
		DisplaySort mockDisplaySort = createMockDisplaySort(origin);
		DisplayListCreation.setWeaponDisplaySort(mockDisplaySort);
		List<Integer> expected = DisplayListCreation.getWeaponDisplayList();
		assertThat(expected, is(origin));
	}
	
	static Stream<Arguments> displayList(){
		return Stream.of(
				Arguments.of(Arrays.asList(0, 1, 2, 3)),
				Arguments.of(Arrays.asList(0, 1, 5, 7, 9)));
	}
	
	DisplaySort createMockDisplaySort(List<Integer> origin) {
		DisplaySort mockDisplaySort = mock(DisplaySort.class);
		doReturn(origin).when(mockDisplaySort).getDisplayList();
		return mockDisplaySort;
	}
}
