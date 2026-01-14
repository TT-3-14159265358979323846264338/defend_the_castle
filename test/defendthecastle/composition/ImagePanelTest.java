package defendthecastle.composition;

import static custommatcher.CustomMatcher.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.awt.image.BufferedImage;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImagePanelTest {
	@InjectMocks
	ImagePanel ImagePanel;
	
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
	 * ImagePanelに必要な変数が設定されたことを確認。
	 */
	@Test
	@SuppressWarnings("unchecked")
	void testSetImagePanel() {
		List<BufferedImage> mockImageList = (List<BufferedImage>) createMockList();
		List<Integer> mockDisplayList = (List<Integer>) createMockList();
		List<Integer> mockNumberList = (List<Integer>) createMockList();
		boolean exists = true;
		ImagePanel.setImagePanel(mockImageList, mockDisplayList, mockNumberList, exists);
		assertThat(ImagePanel.getImageList(), is(mockImageList));
		assertThat(ImagePanel.getDisplayList(), is(mockDisplayList));
		assertThat(ImagePanel.getNumberList(), is(mockNumberList));
		assertThat(ImagePanel.isExistsWhich(), is(exists));
	}
	
	List<?> createMockList(){
		return mock(List.class);
	}
}
