package savedata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import defaultdata.DefaultStage;
import defaultdata.DefaultUnit;

//データ保存用ファイルの確認
public class FileCheck extends SQLOperation{
	/**
	 * ゲーム初回起動時に設定される編成名
	 */
	private final String DEFAULT_NEW_NAME = "基本編成";
	
	/**
	 * ゲーム初回起動時に設定される選択箇所
	 */
	private final int DEFAULT_SELECT = 0;
	
	/**
	 * ゲーム初回起動時に設定されるコアの数
	 */
	private final List<Integer> DEFAULT_CORE_NUMBER = Arrays.asList(8);
	
	/**
	 * ゲーム初回起動時に設定される武器の数
	 */
	private final List<Integer> DEFAULT_WEAPON_NUMBER = Arrays.asList(2, 2);
	
	/**
	 * ゲーム初回起動時に設定されるメダル枚数
	 */
	private int DEFAULT_MEDAL = 1000;
	
	/**
	 * 新規追加された際の初期保有数
	 */
	private final int HAVE_NO_ITEM = 0;
	
	public FileCheck() {
		operateSQL(mysql -> {
			if(hasPlayedGame(mysql)) {
				everyTimeCheck(mysql);
				return;
			}
			firstTimeCheck(mysql);
		});
	}
	
	void everyTimeCheck(Connection mysql) throws Exception{
		addNewUnit(mysql);
		initializeProgress(mysql);
	}
	
	void firstTimeCheck(Connection mysql) throws Exception{
		newCompositionTable(mysql);
		initializeComposition();
		newSelectTable(mysql);
		initializeSelect(mysql);
		newUnitTable(mysql);
		initializeUnit(mysql);
		newItemTable(mysql);
		initializeItem(mysql);
		newProgress(mysql);
		initializeProgress(mysql);
	}
	
	boolean hasPlayedGame(Connection mysql) throws Exception{
		try(PreparedStatement gemaPrepared = mysql.prepareStatement(createGameConditionCode(mysql));
				ResultSet gameResult = gemaPrepared.executeQuery()){
			gameResult.next();
			return 0 < gameResult.getInt(1);
		}
	}
	
	String createGameConditionCode(Connection mysql) throws Exception{
		return String.format("SELECT COUNT(*) FROM "
				+ "information_schema.TABLES "
				+ "WHERE table_schema = '%s' "
				+ "AND table_name = '%s'",
				mysql.getCatalog(),
				SaveComposition.COMPOSITION_NAME);
	}
	
	void newCompositionTable(Connection mysql) throws Exception{
		try(Statement compositionStatement = mysql.createStatement()){
			compositionStatement.executeUpdate(createCompositionTableCode());
		}
	}
	
	String createCompositionTableCode() {
		return String .format("CREATE TABLE %s ("
				+ "%s INT AUTO_INCREMENT NOT NULL PRIMARY KEY,"
				+ "%s VARCHAR(20) NOT NULL"
				+ ")",
				SaveComposition.COMPOSITION_NAME,
				SaveComposition.ID_COLUMN,
				SaveComposition.NAME_COLUMN);
	}
	
	void initializeComposition() {
		new SaveComposition().newComposition(DEFAULT_NEW_NAME);
	}
	
	void newSelectTable(Connection mysql) throws Exception{
		try(Statement selectStatement = mysql.createStatement()){
			selectStatement.executeUpdate(createSelectTableCode());
		}
	}
	
	String createSelectTableCode() {
		return String.format("CREATE TABLE %s ("
				+ "%s TINYINT AUTO_INCREMENT NOT NULL PRIMARY KEY,"
				+ "%s TINYINT NOT NULL"
				+ ")",
				SaveSelect.SELECT_NAME,
				SaveSelect.ID_COLUMN,
				SaveSelect.SELECT_COLUMN);
	}
	
	void initializeSelect(Connection mysql) throws Exception{
		String newSelect = String.format("INSERT INTO %s (%s) VALUES (?) ", SaveSelect.SELECT_NAME, SaveSelect.SELECT_COLUMN);
		try(PreparedStatement selectPrepared = mysql.prepareStatement(newSelect)){
			for(int i = 0; i < 2; i++) {
				selectPrepared.setInt(1, DEFAULT_SELECT);
				selectPrepared.addBatch();
			}
			selectPrepared.executeBatch();
		}
	}
	
	void newUnitTable(Connection mysql) throws Exception{
		try(Statement itemStatement = mysql.createStatement()){
			itemStatement.executeUpdate(createUnitTableCode(SaveHoldItem.CORE_NAME));
			itemStatement.executeUpdate(createUnitTableCode(SaveHoldItem.WEAPON_NAME));
		}
	}
	
	String createUnitTableCode(String tableName) {
		return String.format("CREATE TABLE %S ("
				+ "%s INT AUTO_INCREMENT NOT NULL PRIMARY KEY,"
				+ "%s INT UNSIGNED NOT NULL"
				+ ")",
				tableName,
				SaveHoldItem.ID_COLUMN,
				SaveHoldItem.NUMBER_COLUMN);
	}
	
	void initializeUnit(Connection mysql) throws Exception{
		initialUnit(mysql, SaveHoldItem.CORE_NAME, DEFAULT_CORE_NUMBER);
		initialUnit(mysql, SaveHoldItem.WEAPON_NAME, DEFAULT_WEAPON_NUMBER);
		addNewUnit(mysql);
	}
	
	void initialUnit(Connection mysql, String tableName, List<Integer> initial) throws Exception{
		String newUnit = String.format("INSERT INTO %s (%s) VALUES (?)", tableName, SaveHoldItem.NUMBER_COLUMN);
		try(PreparedStatement unitPrepared = mysql.prepareStatement(newUnit)){
			for(int i = 0; i < initial.size(); i++) {
				unitPrepared.setInt(1, initial.get(i));
				unitPrepared.addBatch();
			}
			unitPrepared.executeBatch();
		}
	}
	
	void addNewUnit(Connection mysql) throws Exception{
		addNewUnitData(mysql, SaveHoldItem.CORE_NAME, DefaultUnit.CORE_DATA_MAP.size());
		addNewUnitData(mysql, SaveHoldItem.WEAPON_NAME, DefaultUnit.WEAPON_DATA_MAP.size());
	}
	
	void addNewUnitData(Connection mysql, String tableName, int size) throws Exception{
		int addCount = changeCount(mysql, tableName, size);
		if(addCount == 0) {
			return;
		}
		String addNewUnit = String.format("INSERT INTO %s (%s) VALUES (?)", tableName, SaveHoldItem.NUMBER_COLUMN);
		try(PreparedStatement addPrepared = mysql.prepareStatement(addNewUnit)){
			for(int i = 0; i < addCount; i++) {
				addPrepared.setInt(1, HAVE_NO_ITEM);
				addPrepared.addBatch();
			}
			addPrepared.executeBatch();
		}
	}
	
	int changeCount(Connection mysql, String tableName, int size) throws Exception{
		String unitLoad = String.format("SELECT COUNT(*) FROM %s", tableName);
		try(PreparedStatement unitPrepared = mysql.prepareStatement(unitLoad);
				ResultSet unitResult = unitPrepared.executeQuery()){
			unitResult.next();
			int count = unitResult.getInt(1);
			if(count < size) {
				return size - count;
			}
		}
		return 0;
	}
	
	void newItemTable(Connection mysql) throws Exception{
		try(Statement itemStatement = mysql.createStatement()){
			itemStatement.executeUpdate(createItemTableCode());
		}
	}
	
	String createItemTableCode() {
		return String.format("CREATE TABLE %s ("
				+ "%s TINYINT AUTO_INCREMENT NOT NULL PRIMARY KEY,"
				+ "%s INT UNSIGNED NOT NULL"
				+ ")",
				SaveItem.ITEM_NAME,
				SaveItem.ID_COLUMN,
				SaveItem.ITEM_COLUMN);
	}
	
	void initializeItem(Connection mysql) throws Exception{
		String newItem = String.format("INSERT INTO %S (%s) VALUES (?)", SaveItem.ITEM_NAME, SaveItem.ITEM_COLUMN);
		try(PreparedStatement itemPrepared = mysql.prepareStatement(newItem)){
			itemPrepared.setInt(1, DEFAULT_MEDAL);
			itemPrepared.executeUpdate();
		}
	}
	
	void newProgress(Connection mysql) throws Exception{
		try(Statement progressStatement = mysql.createStatement()){
			progressStatement.executeUpdate(createProgressTableCode());
		}
	}
	
	String createProgressTableCode() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("CREATE TABLE %s ("
				+ "%s TINYINT AUTO_INCREMENT NOT NULL PRIMARY KEY,"
				+ "%s BOOLEAN NOT NULL,",
				SaveGameProgress.STAGE_NAME,
				SaveGameProgress.ID_COLUMN,
				SaveGameProgress.STAGE_COLUMN));
		for(int i = 0; i < SaveGameProgress.MERIT_MAX_NUMBER; i++){
			builder.append(String.format(" %s%d BOOLEAN NOT NULL,",
					SaveGameProgress.MERIT_COLUMN,
					i));
		}
		builder.delete(builder.length() - 1, builder.length());
		builder.append(")");
		return builder.toString();
	}
	
	void initializeProgress(Connection mysql) throws Exception{
		int addCount = changeCount(mysql, SaveGameProgress.STAGE_NAME, DefaultStage.STAGE_DATA.size());
		if(addCount == 0) {
			return;
		}
		try(PreparedStatement progressPrepared = mysql.prepareStatement(createNewProgressCode())){
			for(int i = 0; i < addCount; i++) {
				progressPrepared.setBoolean(1, false);
				for(int j = 0; j < SaveGameProgress.MERIT_MAX_NUMBER; j++) {
					progressPrepared.setBoolean(j + 2, false);
				}
				progressPrepared.addBatch();
			}
			progressPrepared.executeBatch();
		}
	}
	
	String createNewProgressCode() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("INSERT INTO %S (%s,", SaveGameProgress.STAGE_NAME, SaveGameProgress.STAGE_COLUMN));
		for(int i = 0; i < SaveGameProgress.MERIT_MAX_NUMBER; i++){
			builder.append(String.format(" %s%d,",
					SaveGameProgress.MERIT_COLUMN,
					i));
		}
		builder.delete(builder.length() - 1, builder.length());
		builder.append(") VALUES (");
		for(int i = 0; i < SaveGameProgress.MERIT_MAX_NUMBER + 1; i++){
			builder.append(" ?,");
		}
		builder.delete(builder.length() - 1, builder.length());
		builder.append(")");
		return builder.toString();
	}
}