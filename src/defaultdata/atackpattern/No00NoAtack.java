package defaultdata.atackpattern;

import java.util.Arrays;
import java.util.List;

import defendthecastle.battle.InternalData.BattleData;

public class No00NoAtack extends AtackPatternData{

	@Override
	public String getExplanation() {
		return "なし";
	}

	@Override
	public List<BattleData> getTarget() {
		return Arrays.asList();
	}
}