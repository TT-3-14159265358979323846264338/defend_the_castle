package defendthecastle;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
	private MenuMain MenuMain;

	@BeforeEach
	void setUp() throws Exception {
		
	}

	/**
	 * MainFrameが更新されていることを確認。<br>
	 * JButtonがJPanelに追加されているか確認。<br>
	 * 全てのJButtonにActionListenersが追加されているか確認。<br>
	 * scheduleAtFixedRateでのタイマーがセットされているか確認。
	 */
	@Test
	void testMenuMain() {
		MainFrame MainFrame = mock(MainFrame.class);
		ScheduledExecutorService mockScheduler = mock(ScheduledExecutorService.class);
		MockedStatic<Executors> mockExecutors = mockStatic(Executors.class);
		mockExecutors.when(() -> Executors.newScheduledThreadPool(anyInt())).thenReturn(mockScheduler);
		MenuMain = new MenuMain(MainFrame);
		JButton[] allButton = allButton();
		assertThat(MenuMain.getMainFrame(), is(MainFrame));
		assertThat(MenuMain.getComponents(), arrayContainingInAnyOrder(allButton));
		Stream.of(allButton).forEach(this::assertActionListeners);
		verify(mockScheduler).scheduleAtFixedRate(Mockito.any(Runnable.class), anyLong(), anyLong(), Mockito.any(TimeUnit.class));
		mockExecutors.close();
	}
	
	JButton[] allButton() {
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
}