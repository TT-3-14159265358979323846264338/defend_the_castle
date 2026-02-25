package defaultdata.atackpattern;

import java.util.Comparator;
import java.util.List;

import defendthecastle.battle.BattleData;

public class No03LowHP extends AtackPatternData{
	@Override
	public String getExplanation() {
		return "HP割合低";
	}

	@Override
	public List<BattleData> getTarget() {
		return candidate.stream().filter(this::activeCheck).filter(this::rangeCheck).sorted(Comparator.comparing(this::ratioHP)).limit(myself.getAtackNumber()).toList();
	}
}