package defendthecastle.itemdispose;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import screendisplay.DisplaySort;

@ExtendWith(MockitoExtension.class)
class CreateDisplayListTest {
	@Mock
	private OperateData OperateData;
	
	private List<Integer> mockCoreNumberList;
	private List<Integer> mockWeaponNumberList;
	private ArgumentCaptor<List<Integer>> coreCaptor;
	private ArgumentCaptor<List<Integer>> weaponCaptor;
	private MockedConstruction<DisplaySort> mockDisplaySort;
	private CreateDisplayList CreateDisplayList;
	
	@BeforeEach
	void setUp() throws Exception {
		createTestList();
		createTestData();
		CreateDisplayList = new CreateDisplayList(OperateData);
	}
	
	@AfterEach
	void tearDown() throws Exception {
		mockDisplaySort.close();
	}
	
	void createTestList() {
		mockCoreNumberList = defaultNumberList();
		doReturn(mockCoreNumberList).when(OperateData).getCoreNumberList();
		mockWeaponNumberList = defaultNumberList();
		doReturn(mockWeaponNumberList).when(OperateData).getWeaponNumberList();
	}

	List<Integer> defaultNumberList(){
		return new ArrayList<>(Arrays.asList(8, 0, 1));
	}
	
	void createTestData() {
		coreCaptor = createCaptor();
		weaponCaptor = createCaptor();
		mockDisplaySort = createMockDisplaySort();
	}
	
	@SuppressWarnings("unchecked")
	ArgumentCaptor<List<Integer>> createCaptor(){
		return ArgumentCaptor.forClass(List.class);
	}
	
	MockedConstruction<DisplaySort> createMockDisplaySort(){
		return mockConstruction(DisplaySort.class,
				(mock, context) -> {
					doNothing().when(mock).core(coreCaptor.capture());
					doNothing().when(mock).weapon(weaponCaptor.capture());
				});
	}

	/**
	 * {@link OperateData#OperateData OperateData}が更新されたことを確認。<br>
	 * {@link DisplaySort#DisplaySort DisplaySort}で{@link DisplaySort#core core}と{@link DisplaySort#weapon weapon}が呼ばれ、これらの引数が適切であるか確認。<br>
	 * coreの引数: コア所有数が1以上の値が格納されたindexをリスト化したもの。ただし、初期コア(index = 0)は除く。<br>
	 * weaponの引数: 武器所有数が1以上の値が格納されたindexをリスト化したもの。
	 */
	@Test
	void testCreateDisplayList() {
		assertThat(CreateDisplayList.getOperateData(), is(OperateData));
		assertThat(coreCaptor.getValue(), is(Arrays.asList(2)));
		assertThat(weaponCaptor.getValue(), is(Arrays.asList(0, 2)));
	}
	
	/**
	 * coreにおいて{@link DisplaySort#getDisplayList}で返却されるListを返却したことを確認。
	 */
	@Test
	void testGetCoreDisplayList() {
		List<Integer> expected = createMockList();
		doReturn(expected).when(mockDisplaySort.constructed().get(0)).getDisplayList();
		List<Integer> result = CreateDisplayList.getCoreDisplayList();
		assertThat(result, is(expected));
	}
	
	/**
	 * weaponにおいて{@link DisplaySort#getDisplayList}で返却されるListを返却したことを確認。
	 */
	@Test
	void testGetWeaponDisplayList() {
		List<Integer> expected = createMockList();
		doReturn(expected).when(mockDisplaySort.constructed().get(1)).getDisplayList();
		List<Integer> result = CreateDisplayList.getWeaponDisplayList();
		assertThat(result, is(expected));
	}
	
	@SuppressWarnings("unchecked")
	List<Integer> createMockList(){
		return mock(List.class);
	}
}
