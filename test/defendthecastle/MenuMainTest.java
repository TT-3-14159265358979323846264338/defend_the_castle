package defendthecastle;

import static custommatcher.MatcherOfDisplayAllText.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
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
		MenuMain = new MenuMain(MainFrame);
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
		MenuMain.paintComponent(brankGraphics());
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
	
	/**
	 * futureがキャンセルされていれば、全てのコアとタイトルを描写したか確認。
	 */
	@Test
	void testDrawImageFutureCancelled() {
		Graphics mockGraphics = setMockGraphics();
		setMockFuture(true);
		MenuMain.drawImage(mockGraphics);
		verify(mockGraphics, times(MenuMain.getFinalMotion().length + 1)).drawImage(Mockito.any(Image.class), anyInt(), anyInt(), Mockito.any(ImageObserver.class));
	}
	
	Graphics setMockGraphics() {
		Graphics mockGraphics = mock(Graphics.class);
		doReturn(true).when(mockGraphics).drawImage(Mockito.any(Image.class), anyInt(), anyInt(), Mockito.any(ImageObserver.class));
		return mockGraphics;
	}
	
	void setMockFuture(boolean exists) {
		ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);
		MenuMain.setMainFuture(mockFuture);
		doReturn(exists).when(mockFuture).isCancelled();
	}
	
	/**
	 * futureが実行中であれば、全ての実行中のコアを描写したか確認。
	 */
	@Test
	void testDrawImageFutureOperation() {
		Graphics mockGraphics = setMockGraphics();
		setMockFuture(false);
		setMockFallMotion();
		MenuMain.drawImage(mockGraphics);
		verify(mockGraphics, times(MenuMain.getFallMotion().length)).drawImage(Mockito.any(Image.class), anyInt(), anyInt(), Mockito.any(ImageObserver.class));
	}
	
	void setMockFallMotion() {
		FallMotion mockFallMotion = mock(FallMotion.class);
		FallMotion[] mockFallMotionArray = new FallMotion[MenuMain.getFallMotion().length];
		Arrays.fill(mockFallMotionArray, mockFallMotion);
		MenuMain.setFallMotion(mockFallMotionArray);
		doReturn(true).when(mockFallMotion).canStart();
	}
	
	/**
	 * 
	 */
	@Test
	void testEffectTimerProcess() {
		
		
		
		
		
	}
}