package defaultdata.atackpattern;

import java.util.Comparator;
import java.util.List;

import battle.BattleData;

public class No01Far extends AtackPattern{

	@Override
	public String getExplanation() {
		return "最遠";
	}

	@Override
	public List<BattleData> getTarget() {
		return candidate.stream().filter(this::activeCheck).filter(this::rangeCheck).sorted(Comparator.comparing(this::distanceCalculate).reversed()).limit(myself.getAtackNumber()).toList();
	}
}