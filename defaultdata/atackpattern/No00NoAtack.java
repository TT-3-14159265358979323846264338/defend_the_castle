package defaultdata.atackpattern;

import java.util.Arrays;
import java.util.List;

import battle.BattleData;

public class No00NoAtack extends AtackPattern{

	@Override
	public String getExplanation() {
		return "なし";
	}

	@Override
	public List<BattleData> getTarget() {
		return Arrays.asList();
	}
}