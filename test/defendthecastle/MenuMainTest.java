package defendthecastle;

import static custommatcher.MatcherOfDisplayAllText.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.swing.JButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class MenuMainTest {
	private MainFrame MainFrame;
	private MenuMain MenuMain;

	@BeforeEach
	void setUp() throws Exception {
		MainFrame = mock(MainFrame.class);
		MenuMain = spy(new MenuMain(MainFrame));
	}

	/**
	 * MainFrameが更新されていることを確認。<br>
	 * JButtonがJPanelに追加されているか確認。<br>
	 * 全てのJButtonにActionListenersが追加されているか確認。<br>
	 * scheduleAtFixedRateでのタイマーがセットされているか確認。
	 */
	@Test
	void testMenuMain() {
		ScheduledExecutorService mockScheduler = mock(ScheduledExecutorService.class);
		MockedStatic<Executors> mockExecutor = mockStatic(Executors.class);
		mockExecutor.when(() -> Executors.newScheduledThreadPool(anyInt())).thenReturn(mockScheduler);
		MenuMain = new MenuMain(MainFrame);
		JButton[] allButton = buttonArray();
		assertThat(MenuMain.getMainFrame(), is(MainFrame));
		assertThat(MenuMain.getComponents(), arrayContainingInAnyOrder(allButton));
		Stream.of(allButton).forEach(this::assertActionListeners);
		verify(mockScheduler).scheduleAtFixedRate(Mockito.any(Runnable.class), anyLong(), anyLong(), Mockito.any(TimeUnit.class));
		mockExecutor.close();
	}
	
	JButton[] buttonArray() {
		return new JButton[]{MenuMain.getItemGetButton(),
			MenuMain.getItemDisposeButton(),
			MenuMain.getCompositionButton(),
			MenuMain.getSelectStageButton(),
			MenuMain.getTestButton()
		};
	}
	
	void assertActionListeners(JButton button) {
		assertThat(button.getActionListeners(), not(emptyArray()));
	}
	
	/**
	 * JButtonにテキストが設定されており、テキストの全文が表示可能であるか確認。
	 */
	@Test
	void testPaintComponent() {
		Graphics g = brankGraphics();
		MenuMain.paintComponent(g);
		JButton[] allButton = buttonArray();
		Stream.of(allButton).forEach(this::assertButton);
	}
	
	Graphics brankGraphics() {
		return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).createGraphics();
	}
	
	void assertButton(JButton button) {
		assertThat(button.getText(), not(emptyOrNullString()));
		assertThat(button, displayAllText());
	}
}