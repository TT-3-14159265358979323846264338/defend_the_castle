package defaultdata;

import defaultdata.facility.*;

public enum Facility implements DefaultEnum<FacilityData>{
	CASTLE(0, new No0000Castle()),
	GATE(1, new No0001Gate()),
	STRONGHOLD(2, new No0002Stronghold());
	
	private final int id;
	private final FacilityData label;

	Facility(int id, FacilityData label) {
		this.id = id;
		this.label = label;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public FacilityData getLabel() {
		return label;
	}
}