package defendthecastle;

import static custommatcher.CustomMatcher.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.swing.JButton;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import testdataedit.TestDataEdit;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MenuMainTest {
	@Mock
	private MainFrame MainFrame;
	
	@InjectMocks
	private MenuMain MenuMain;

	/**
	 * タイトル画像が取り込まれていることを確認。<br>
	 * コア画像が全て取り込まれていることを確認。<br>
	 * コア画像番号がランダムに格納されたリストであるか確認。
	 */
	@Test
	void testVariables() {
		assertThat(MenuMain.getTitleImage(), notNullValue());
		assertThat(MenuMain.getCoreImage(), everyItem(notNullValue()));
		assertThat(MenuMain.getRandamList(), everyItem(allOf(lessThan(MenuMain.getCoreImage().size()), greaterThanOrEqualTo(0))));
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
		assertThat(MenuMain.getComponents(), hasAllItemInArray(allButton));
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
		assertThat(button, displayAllText());
	}
	
	/**
	 * ガチャ画面が1回のみ呼び出されることを確認。
	 */
	@Test
	void testItemGetButtonAction() {
		ActionEvent e = mock(ActionEvent.class);
		MenuMain.itemGetButtonAction(e);
		verify(MainFrame, times(1)).itemGetMenuDraw();
	}
	
	/**
	 * リサイクル画面が1回のみ呼び出されることを確認。
	 */
	@Test
	void testItemDisposeButtonAction() {
		ActionEvent e = mock(ActionEvent.class);
		MenuMain.itemDisposeButtonAction(e);
		verify(MainFrame, times(1)).itemDisposeMenuDraw();
	}
	
	/**
	 * 編成画面が1回のみ呼び出されることを確認。
	 */
	@Test
	void testCompositionButtonAction() {
		ActionEvent e = mock(ActionEvent.class);
		MenuMain.compositionButtonAction(e);
		verify(MainFrame, times(1)).compositionDraw();
	}
	
	/**
	 * ステージ選択画面が1回のみ呼び出されることを確認。
	 */
	@Test
	void testBattleButtonAction() {
		ActionEvent e = mock(ActionEvent.class);
		MenuMain.battleButtonAction(e);
		verify(MainFrame, times(1)).selectStageDraw();
	}
	
	/**
	 * データ編集画面が1回のみ呼び出されることを確認。
	 */
	@Test
	void testTestButtonAction() {
		MockedConstruction<TestDataEdit> mockTestDataEdit = mockConstruction(TestDataEdit.class);
		ActionEvent e = mock(ActionEvent.class);
		MenuMain.testButtonAction(e);
		assertThat(mockTestDataEdit.constructed(), hasSize(1));
		mockTestDataEdit.close();
	}
	
	/**
	 * futureがキャンセルされていれば、全てのコアとタイトルを描写したか確認。
	 */
	@Test
	void testDrawImageFutureCancelled() {
		Graphics mockGraphics = createMockGraphics();
		createMockFuture(true);
		MenuMain.drawImage(mockGraphics);
		verify(mockGraphics, times(MenuMain.getFinalMotion().length + 1)).drawImage(Mockito.any(Image.class), anyInt(), anyInt(), Mockito.any(ImageObserver.class));
	}
	
	/**
	 * futureが実行中であれば、全ての実行中のコアを描写したか確認。
	 */
	@Test
	void testDrawImageFutureOperation() {
		Graphics mockGraphics = createMockGraphics();
		createMockFuture(false);
		createMockFallMotion();
		MenuMain.drawImage(mockGraphics);
		verify(mockGraphics, times(MenuMain.getFallMotion().length)).drawImage(Mockito.any(Image.class), anyInt(), anyInt(), Mockito.any(ImageObserver.class));
	}
	
	Graphics createMockGraphics() {
		Graphics mockGraphics = mock(Graphics.class);
		doReturn(true).when(mockGraphics).drawImage(Mockito.any(Image.class), anyInt(), anyInt(), Mockito.any(ImageObserver.class));
		return mockGraphics;
	}
	
	void createMockFuture(boolean exists) {
		ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);
		MenuMain.setMainFuture(mockFuture);
		doReturn(exists).when(mockFuture).isCancelled();
	}
	
	FallMotion createMockFallMotion() {
		FallMotion mockFallMotion = mock(FallMotion.class);
		FallMotion[] mockFallMotionArray = new FallMotion[MenuMain.getFallMotion().length];
		Arrays.fill(mockFallMotionArray, mockFallMotion);
		MenuMain.setFallMotion(mockFallMotionArray);
		doReturn(true).when(mockFallMotion).canStart();
		doNothing().when(mockFallMotion).fallTimerStart(Mockito.any(ScheduledExecutorService.class));
		return mockFallMotion;
	}
	
	/**
	 * タイマーのカウントが1増加していることを確認。<br>
	 * 落下用タイマーが1つ以上呼ばれたことを確認。<br>
	 * 落下用タイマーが稼働中はメインのタイマーが停止しないことを確認。
	 */
	@Test
	void testEffectTimerProcessNotCancel() {
		FallMotion mockFallMotion = createMockFallMotion();
		doReturn(true).when(mockFallMotion).canStart();
		int oldCount = MenuMain.getCount();
		MenuMain.effectTimerProcess();
		assertThat(MenuMain.getCount(), is(oldCount + 1));
		verify(mockFallMotion, atLeastOnce()).fallTimerStart(Mockito.any(ScheduledExecutorService.class));
		assertThat(MenuMain.getMainFuture().isCancelled(), is(false));
	}
	
	/**
	 * カウントがユニット数以上になったら落下用タイマーが呼ばれないことを確認。<br>
	 * 落下用タイマーが停止すればメインのタイマーが停止し、最終段階タイマーが稼働することを確認。
	 */
	@Test
	void testEffectTimerProcessCanCancel() {
		MenuMain.setCount(MenuMain.getFallMotion().length + 1);
		FallMotion mockFallMotion = createMockFallMotion();
		FinalMotion mockFinalMotion = createMockFinalMotion();
		doReturn(false).when(mockFallMotion).canStart();
		MenuMain.effectTimerProcess();
		verify(mockFallMotion, never()).fallTimerStart(Mockito.any(ScheduledExecutorService.class));
		verify(mockFinalMotion, atLeastOnce()).finalTimerStart(Mockito.any(ScheduledExecutorService.class));
		assertThat(MenuMain.getMainFuture().isCancelled(), is(true));
	}
	
	FinalMotion createMockFinalMotion() {
		FinalMotion mockFinalMotion = mock(FinalMotion.class);
		FinalMotion[] mockFinalMotionArray = new FinalMotion[MenuMain.getFinalMotion().length];
		Arrays.fill(mockFinalMotionArray, mockFinalMotion);
		MenuMain.setFinalMotion(mockFinalMotionArray);
		doNothing().when(mockFinalMotion).finalTimerStart(Mockito.any(ScheduledExecutorService.class));
		return mockFinalMotion;
	}
	
	/**
	 * 最終段階タイマーが稼働中ならschedulerも稼働していることを確認。
	 * 最終段階タイマーが停止したならschedulerも停止することを確認。
	 */
	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void testsSchedulerEndProcess(boolean exists) {
		FinalMotion mockFinalMotion = createMockFinalMotion();
		doReturn(exists).when(mockFinalMotion).canEnd();
		MenuMain.schedulerEndProcess();
		assertThat(MenuMain.getScheduler().isShutdown(), is(exists));
	}
}