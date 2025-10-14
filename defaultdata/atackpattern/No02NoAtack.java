package defaultdata.atackpattern;

import java.util.Arrays;
import java.util.List;

import battle.BattleData;

public class No02NoAtack extends AtackPattern{

	@Override
	public String getExplanation() {
		return "";
	}

	@Override
	public List<BattleData> getTarget() {
		return Arrays.asList();
	}
}
