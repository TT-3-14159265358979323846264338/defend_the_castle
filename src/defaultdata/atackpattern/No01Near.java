package defaultdata.atackpattern;

import java.util.Comparator;
import java.util.List;

import defendthecastle.battle.BattleData;

public class No01Near extends AtackPatternData{

	@Override
	public String getExplanation() {
		return "最近接";
	}

	@Override
	public List<BattleData> getTarget() {
		return candidate.stream().filter(this::activeCheck).filter(this::rangeCheck).sorted(Comparator.comparing(this::distanceCalculate)).limit(myself.getAtackNumber()).toList();
	}
}