package savedata;

import static savedata.OperationSQL.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import defaultdata.DefaultUnit;

//データ保存用ファイルの確認
public class FileCheck{
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
	
	/**
	 * MySQLへの接続
	 */
	private Connection mysql;
	
	public FileCheck() {
		mysql = connectMysql();
		executeSQL(mysql, () -> {
			if(hasPlayedGame()) {
				addNewUnit();
				//ステージクリアデータは後で記載
				return;
			}
			newCompositionTable();
			initializeComposition();
			newSelectTable();
			initializeSelect();
			newUnitTable();
			initializeUnit();
			newItemTable();
			initializeItem();
			//ステージクリアデータは後で記載
		});
		closeConnection(mysql);
	}
	
	boolean hasPlayedGame() throws Exception{
		try(PreparedStatement gemaPrepared = mysql.prepareStatement(createGameConditionCode());
				ResultSet gameResult = gemaPrepared.executeQuery()){
			gameResult.next();
			return 0 < gameResult.getInt(1);
		}
	}
	
	String createGameConditionCode() throws Exception{
		return String.format("SELECT COUNT(*) FROM "
				+ "information_schema.TABLES "
				+ "WHERE table_schema = %s "
				+ "AND table_name = %s",
				mysql.getCatalog(),
				SaveComposition.COMPOSITION_NAME);
	}
	
	void newCompositionTable() throws Exception{
		try(Statement compositionStatement = mysql.createStatement()){
			compositionStatement.executeUpdate(createCompositionTableCode());
		}
	}
	
	String createCompositionTableCode() {
		return String .format("CREATE TABLE %s ("
				+ "%s INT AUTO_INCREMENT NOT NULL PRIMARY KEY,"
				+ "%s VARCHAR"
				+ ")",
				SaveComposition.COMPOSITION_NAME,
				SaveComposition.ID_COLUMN,
				SaveComposition.NAME_COLUMN);
	}
	
	void initializeComposition() {
		new SaveComposition().newComposition(DEFAULT_NEW_NAME);
	}
	
	void newSelectTable() throws Exception{
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
	
	void initializeSelect() throws Exception{
		String newSelect = String.format("INSERT INTO %s (%s) VALUES (?) ", SaveSelect.SELECT_NAME, SaveSelect.SELECT_COLUMN);
		try(PreparedStatement selectPrepared = mysql.prepareStatement(newSelect)){
			for(int i = 0; i < 2; i++) {
				selectPrepared.setInt(1, DEFAULT_SELECT);
				selectPrepared.addBatch();
			}
			selectPrepared.executeBatch();
		}
	}
	
	void newUnitTable() throws Exception{
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
	
	void initializeUnit() throws Exception{
		initialUnit(SaveHoldItem.CORE_NAME, DEFAULT_CORE_NUMBER);
		initialUnit(SaveHoldItem.WEAPON_NAME, DEFAULT_WEAPON_NUMBER);
		addNewUnit();
	}
	
	void initialUnit(String tableName, List<Integer> initial) throws Exception{
		String newUnit = String.format("INSERT INTO %s (%s) VALUES (?)", tableName, SaveHoldItem.NUMBER_COLUMN);
		try(PreparedStatement unitPrepared = mysql.prepareStatement(newUnit)){
			for(int i = 0; i < initial.size(); i++) {
				unitPrepared.setInt(1, initial.get(i));
				unitPrepared.addBatch();
			}
			unitPrepared.executeBatch();
		}
	}
	
	void addNewUnit() throws Exception{
		addNewUnitData(SaveHoldItem.CORE_NAME, DefaultUnit.CORE_DATA_MAP.size());
		addNewUnitData(SaveHoldItem.WEAPON_NAME, DefaultUnit.WEAPON_DATA_MAP.size());
	}
	
	void addNewUnitData(String tableName, int size) throws Exception{
		int addCount = changeCount(tableName, size);
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
	
	int changeCount(String tableName, int size) throws Exception{
		String unitLoad = String.format("SELECT COUNT(*) FROM %s", tableName);
		try(PreparedStatement unitPrepared = mysql.prepareStatement(unitLoad);
				ResultSet unitResult = unitPrepared.executeQuery()){
			unitResult.next();
			int addCount = unitResult.getInt(1);
			if(addCount < size) {
				return size - addCount;
			}
		}
		return 0;
	}
	
	void newItemTable() throws Exception{
		try(Statement itemStatement = mysql.createStatement()){
			itemStatement.executeUpdate(createItemTableCode());
		}
	}
	
	String createItemTableCode() {
		return String.format("CREATE TABLE %s ("
				+ "%s TINYINT AUTO_INCREMENT NOT NULL PRIMARY KEY,"
				+ "%s INT UNSIGNED NOT NULL"
				+ ")",
				SaveItem.ITEM_NAME);
	}
	
	void initializeItem() throws Exception{
		String newItem = String.format("INSERT INTO %S (%s) VALUES (?)", SaveItem.ITEM_NAME, SaveItem.ITEM_COLUMN);
		try(PreparedStatement itemPrepared = mysql.prepareStatement(newItem)){
			itemPrepared.setInt(1, DEFAULT_MEDAL);
			itemPrepared.executeUpdate();
		}
	}
	
	
	/*
	//item dataのデータ数確認して、足りなければ追加
	private void itemDataCheck() {
		/*
		BiConsumer<Integer, List<Integer>> addList = (count, list) -> {
			IntStream.range(0, count).forEach(i -> list.add(0));
		};
		SaveHoldItem.load();
		List<Integer> coreNumberList = SaveHoldItem.getCoreNumberList();
		if(checkSize(DefaultUnit.CORE_DATA_MAP.size(), coreNumberList.size())) {
			addList.accept(DefaultUnit.CORE_DATA_MAP.size() - coreNumberList.size(), coreNumberList);
		}
		List<Integer> weaponNumberList = SaveHoldItem.getWeaponNumberList();
		if(checkSize(DefaultUnit.WEAPON_DATA_MAP.size(), weaponNumberList.size())) {
			addList.accept(DefaultUnit.WEAPON_DATA_MAP.size() - weaponNumberList.size(), weaponNumberList);
		}
		SaveHoldItem.save(coreNumberList, weaponNumberList);
		
	}
	
	//progress dataのデータ数確認して、足りなければ追加
	private void progressDataCheck() {
		SaveGameProgress.load();
		List<Boolean> clearStatus = SaveGameProgress.getClearStatus();
		if(checkSize(DefaultStage.STAGE_DATA.size(), clearStatus.size())) {
			IntStream.range(0, DefaultStage.STAGE_DATA.size() - clearStatus.size()).forEach(i -> clearStatus.add(false));
		}
		List<List<Boolean>> meritStatus = SaveGameProgress.getMeritStatus();
		IntStream.range(0, DefaultStage.STAGE_DATA.size()).forEach(i -> {
			StageData StageData = DefaultStage.STAGE_DATA.get(i);
			try {
				//meritStatusの内側のListのデータ数を確認し、足りなければ追加
				if(checkSize(StageData.getMerit().size(), meritStatus.get(i).size())) {
					IntStream.range(0, StageData.getMerit().size() - meritStatus.get(i).size()).forEach(j -> meritStatus.get(i).add(false));
				}
			}catch(Exception e) {
				//エラーが起こる時はList<Boolean>の数が足りない時である
				//その時はList<Boolean>を追加
				meritStatus.add(StageData.getMerit().stream().map(j -> false).collect(Collectors.toList()));
			}
		});
		SaveGameProgress.save(clearStatus, meritStatus, SaveGameProgress.getMedal(), SaveGameProgress.getSelectStage());
		
	}
	*/
}