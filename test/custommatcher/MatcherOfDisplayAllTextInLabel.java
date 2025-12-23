package custommatcher;

import java.awt.Insets;

import javax.swing.JLabel;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

class MatcherOfDisplayAllTextInLabel extends BaseMatcher<JLabel>{
	private int displayWidth;
	private int displayHeight;
	private int actualWidth;
	private int actualHeight;
	private final int CORRECTION = 6;

	@Override
	public boolean matches(Object obj) {
		if(!(obj instanceof JLabel)) {
			return false;
		}
		JLabel label = (JLabel) obj;
		Insets margin = label.getBorder().getBorderInsets(label);
		displayWidth = label.getFontMetrics(label.getFont()).stringWidth(label.getText()) + margin.left + margin.right + CORRECTION;
		actualWidth = label.getWidth() ;
		displayHeight = label.getFontMetrics(label.getFont()).getHeight() + margin.top + margin.bottom;
		actualHeight = label.getHeight();
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
			desc.appendValue("このJLabelにテキストを表示できません。");
			return;
		}
		desc.appendText("このテキストを表示するには最低限");
		desc.appendValue(String.format("幅%d, 高さ%d", displayWidth, displayHeight));
		desc.appendText("が必要です。しかし、このJLabelの設定値は");
		desc.appendValue(String.format("幅%d, 高さ%d", actualWidth, actualHeight));
		desc.appendText("です。");
	}
}