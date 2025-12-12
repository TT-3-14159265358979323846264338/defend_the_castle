package custommatcher;

import java.awt.Insets;

import javax.swing.JButton;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

class MatcherOfDisplayAllText extends BaseMatcher<JButton>{
	private int displayWidth;
	private int displayHeight;
	private int actualWidth;
	private int actualHeight;
	private final int CORRECTION = 8;
	
	@Override
	public boolean matches(Object obj) {
		if(!(obj instanceof JButton)) {
			return false;
		}
		JButton button = (JButton) obj;
		Insets margin = button.getMargin();
		displayWidth = button.getFontMetrics(button.getFont()).stringWidth(button.getText()) + margin.left + margin.right + CORRECTION;
		actualWidth = button.getWidth() ;
		displayHeight = button.getFontMetrics(button.getFont()).getHeight() + margin.top + margin.bottom;
		actualHeight = button.getHeight();
		if(displayWidth == 0 || displayHeight == 0 || actualWidth == 0 || actualHeight == 0) {
			return false;
		}
		if(actualWidth < displayWidth) {
			return false;
		}
		if(actualHeight < displayHeight) {
			return false;
		}
		return true;
	}

	@Override
	public void describeTo(Description desc) {
		if(displayWidth == 0) {
			desc.appendValue("このJButtonにテキストを表示できません。");
			return;
		}
		desc.appendText("このテキストを表示するには最低限");
		desc.appendValue(String.format("幅%d, 高さ%d", displayWidth, displayHeight));
		desc.appendText("が必要です。しかし、このJButtonの設定値は");
		desc.appendValue(String.format("幅%d, 高さ%d", actualWidth, actualHeight));
		desc.appendText("です。");
	}
}