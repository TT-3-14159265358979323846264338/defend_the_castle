package defendthecastle.composition;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import screendisplay.DisplayStatus;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImagePanelTest {
	@InjectMocks
	private ImagePanel ImagePanel;
	
	/**
	 * コンストラクタでユニット非選択(selectNumber = -1)であることを確認。
	 * MouseListenerが設定されていることを確認。
	 */
	@Test
	void testImagePanel() {
		assertThat(ImagePanel.getSelectNumber(), is(-1));
		assertThat(ImagePanel.getMouseListeners(), notNullValue());
	}
	
	/**
	 * Graphicsに指定のメソッドで追加したことを確認。<br>
	 * (ユニット画像表示, 色指定, フォント指定, 保有数表示は表示ループ回数呼ばれる)<br>
	 * (上記に加えてユニット選択中であるならば、色指定1回, 四角表示1回を追加)
	 */
	@Test
	void testPaintComponent() {
		createImagePanelData(true, 1);
		Graphics spyGraphics = createSpyGraphics();
		ImagePanel.paintComponent(spyGraphics);
		verify(spyGraphics, times(loopTimes() + 1)).setColor(Mockito.any(Color.class));
		verify(spyGraphics, times(1)).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
		verify(spyGraphics, times(loopTimes())).drawImage(Mockito.any(Image.class), anyInt(), anyInt(), Mockito.any(ImageObserver.class));
		verify(spyGraphics, times(loopTimes())).setFont(Mockito.any(Font.class));
		verify(spyGraphics, times(loopTimes())).drawString(anyString(), anyInt(), anyInt());
	}
	
	void createImagePanelData(boolean exists, int selectNumber) {
		List<BufferedImage> mockImageList = createMockImageList();
		List<Integer> brankDisplayList = createBrankDisplayList();
		List<Integer> mockNumberList = createMockNumberList();
		ImagePanel.setImagePanel(mockImageList, brankDisplayList, mockNumberList, exists);
		ImagePanel.setSelectNumber(selectNumber);
	}
	
	@SuppressWarnings("unchecked")
	List<BufferedImage> createMockImageList(){
		List<BufferedImage> mockImageList = (List<BufferedImage>) createMockList();
		doReturn(brankBufferedImage()).when(mockImageList).get(anyInt());
		return mockImageList;
	}
	
	BufferedImage brankBufferedImage() {
		return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	}
	
	List<Integer> createBrankDisplayList() {
		return Arrays.asList(0, 1, 2);
	}
	
	int loopTimes() {
		return createBrankDisplayList().size();
	}
	
	@SuppressWarnings("unchecked")
	List<Integer> createMockNumberList() {
		List<Integer> mockNumberList = (List<Integer>) createMockList();
		doReturn(1).when(mockNumberList).get(anyInt());
		return mockNumberList;
	}
	
	List<?> createMockList(){
		return mock(List.class);
	}
	
	Graphics createSpyGraphics() {
		return spy(brankBufferedImage().createGraphics());
	}
	
	/**
	 * ユニットを選択していない状態(selectNumber = -1)となることを確認。
	 */
	@Test
	void testResetSelectNumber() {
		ImagePanel.setSelectNumber(10);
		ImagePanel.resetSelectNumber();
		assertThat(ImagePanel.getSelectNumber(), is(-1));
	}
	
	/**
	 * クリック地点にユニットがいなければ、選択を解除したことを確認。
	 * クリックしたユニットが選択されていなければ、そのユニットを選択したことを確認。
	 * 既に選択されていた場合、exists = trueならcore, falseならweaponのステータス画面を表示したことを確認。
	 */
	@ParameterizedTest
	@CsvSource({"false, true, 0", "true, true, 0", "true, true, 1", "true, false, 0", "true, false, 1"})
	void testMousePressed(boolean canSelect, boolean exists, int selectNumber) {
		MockedConstruction<ValueRange> mockValueRange = createMockValueRange(canSelect);
		MockedConstruction<DisplayStatus> mockDisplayStatus = createMockDisplayStatus();
		createImagePanelData(exists, selectNumber);
		ImagePanel.mousePressed(createMockMouseEvent());
		if(!canSelect) {
			assertThat(ImagePanel.getSelectNumber(), is(-1));
		}else if(selectNumber != createBrankDisplayList().get(0)){
			assertThat(ImagePanel.getSelectNumber(), is(0));
		}else if(exists) {
			verify(mockDisplayStatus.constructed().get(0)).core(Mockito.any(BufferedImage.class), anyInt());
		}else {
			verify(mockDisplayStatus.constructed().get(0)).weapon(Mockito.any(BufferedImage.class), anyInt());
		}
		mockValueRange.close();
		mockDisplayStatus.close();
	}
	
	MockedConstruction<ValueRange> createMockValueRange(boolean canSelect){
		return mockConstruction(ValueRange.class,
				(mock, context) -> doReturn(canSelect).when(mock).isValidIntValue(anyLong()));
	}
	
	MockedConstruction<DisplayStatus> createMockDisplayStatus(){
		return mockConstruction(DisplayStatus.class,
				(mock, context) -> {
					doNothing().when(mock).core(Mockito.any(BufferedImage.class), anyInt());
					doNothing().when(mock).weapon(Mockito.any(BufferedImage.class), anyInt());
				});
	}
	
	MouseEvent createMockMouseEvent() {
		return mock(MouseEvent.class);
	}
}