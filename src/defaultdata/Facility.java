package defaultdata;

import defaultdata.facility.*;

public enum Facility {
	CASTLE(0, new No0000Castle()),
	GATE(1, new No0001Gate()),
	STRONGHOLD(2, new No0002Stronghold());
	
	private final int id;
	private final FacilityData facilityData;

	Facility(int id, FacilityData facilityData) {
		this.id = id;
		this.facilityData = facilityData;
	}

	public int getId() {
		return id;
	}

	public FacilityData getFacilityData() {
		return facilityData;
	}
}