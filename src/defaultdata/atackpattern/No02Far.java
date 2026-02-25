package defaultdata.atackpattern;

import java.util.Comparator;
import java.util.List;

import defendthecastle.battle.BattleData;

public class No02Far extends AtackPatternData{

	@Override
	public String getExplanation() {
		return "最遠";
	}

	@Override
	public List<BattleData> getTarget() {
		return candidate.stream().filter(this::activeCheck).filter(this::rangeCheck).sorted(Comparator.comparing(this::distanceCalculate).reversed()).limit(myself.getAtackNumber()).toList();
	}
}