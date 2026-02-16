package defaultdata.atackpattern;

import java.util.Comparator;
import java.util.List;

import defendthecastle.battle.BattleData;

public class No04HighHP extends AtackPattern{
	@Override
	public String getExplanation() {
		return "HP割合高";
	}

	@Override
	public List<BattleData> getTarget() {
		return candidate.stream().filter(this::activeCheck).filter(this::rangeCheck).sorted(Comparator.comparing(this::ratioHP).reversed()).limit(myself.getAtackNumber()).toList();
	}
}