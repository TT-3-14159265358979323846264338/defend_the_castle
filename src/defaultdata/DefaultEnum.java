package defaultdata;

public interface DefaultEnum<T> {
	public int getId();
	public T getLabel();
	
	static <T, U extends Enum<U> & DefaultEnum<T>> T getLabel(U[] data, int id) {
		var selectEnum = getEnum(data, id);
		if(selectEnum != null) {
			return selectEnum.getLabel();
		}
		return null;
	}
	
	static <T, U extends Enum<U> & DefaultEnum<T>> U getEnum(U[] data, int id) {
		for(U i: data) {
			if(i.getId() == id) {
				return i;
			}
		}
		return null;
	}
}