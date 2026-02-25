package savedata;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

abstract class SQLOperation {
	/**
	 * MySQLのデータベース接続情報用ファイルの名称。
	 */
	private final String MYSQL_FILE = "db.properties";
	
	/**
	 * MYSQL_FILEでのデータベースURL名保存先
	 */
	private final String URL = "url";
	
	/**
	 * MYSQL_FILEでのユーザー名保存先
	 */
	private final String USER = "user";
	
	/**
	 * MYSQL_FILEでのパスワード保存先
	 */
	private final String PASS = "pass";
	
	@FunctionalInterface
	interface SQLTask<T> {
	    void run(T argument) throws Exception;
	}
	
	/**
	 * MySQLへ接続し、指定されたメソッドを実行する。
	 * メソッド終了後、接続を破棄する。
	 * メソッドで例外が発生した場合、rollbackが行われる。
	 * そのため、メソッド中で例外処理を記載する時は、必ずスローも記載する。
	 * @param task - MySQLでの操作メソッド。ラムダ式で{@link Connection}を渡して記述する。
	 */
	protected void operateSQL(SQLTask<Connection> task) {
		try(Connection mysql = connectMysql()){
			executeSQL(mysql, task);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	Connection connectMysql() throws Exception{
		try(InputStream selectData = new FileInputStream(MYSQL_FILE)) {
			Properties mysqlData = new Properties();
			mysqlData.load(selectData);
			return DriverManager.getConnection(mysqlData.getProperty(URL), mysqlData.getProperty(USER), mysqlData.getProperty(PASS));
		}
	}
	
	void executeSQL(Connection mysql, SQLTask<Connection> task) throws Exception{
		try {
			mysql.setAutoCommit(false);
			task.run(mysql);
			mysql.commit();
		}catch (Exception e) {
			rollbackMysql(mysql);
			throw e;
		}
	}
	
	void rollbackMysql(Connection mysql) {
		try {
			if(mysql != null) {
				mysql.rollback();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 与えられた接続情報とテーブル名からデータを取り込む。
	 * その後、記述されたメソッドを実行する。
	 * @param mysql - 使用する接続。
	 * @param tableName - 取り込むテーブル名。
	 * @param task - テーブルの内部データを取り込むメソッド。ラムダ式で{@link ResultSet}を渡して記述する。
	 * @throws Exception
	 */
	protected void operateResultSet(Connection mysql, String tableName, SQLTask<ResultSet> task) throws Exception{
		String code = String.format("SELECT * FROM %s", tableName);
		try(PreparedStatement prepared = mysql.prepareStatement(code);
				ResultSet result = prepared.executeQuery()) {
			task.run(result);
		}
	}
	
	/**
	 * 指定のリストにテーブルのデータを全て取り込む。
	 * @param mysql - 使用する接続。
	 * @param tableName - 取り込むテーブル名。
	 * @param column - 取り込むカラム名。
	 * @param numberList - 取り込み先のリスト。
	 * @throws Exception
	 */
	protected void dataLoad(Connection mysql, String tableName, String column, List<Integer> numberList) throws Exception {
		operateResultSet(mysql, tableName, result -> {
			List<Integer> loadData = new ArrayList<>();
			while(result.next()) {
				loadData.add(result.getInt(column));
			}
			numberList.clear();
			numberList.addAll(loadData);
		});
	}
	
	/**
	 * 与えられたコードを元にテーブルのデータを上書きする。
	 * @param mysql - 使用する接続。
	 * @param code - 上書きで使用するコード。
	 * @param task - テーブルに上書きするメソッド。ラムダ式で{@link PreparedStatement}を渡して記述する。
	 * @throws Exception
	 */
	protected void operatePrepared(Connection mysql, String code, SQLTask<PreparedStatement> task) throws Exception{
		try(PreparedStatement prepared = mysql.prepareStatement(code)) {
			task.run(prepared);
		}
	}
	
	/**
	 * 指定のリストのデータをテーブルに上書きする。
	 * @param mysql - 使用する接続。
	 * @param tableName - 更新するテーブル名。
	 * @param numberColumn - 更新する数量カラム名。
	 * @param idColumn - 更新するIDカラム名。
	 * @param numberList - 取り込むデータが格納したリスト。
	 * @throws Exception
	 */
	protected void dataSave(Connection mysql, String tableName, String numberColumn, String idColumn, List<Integer> numberList) throws Exception{
		String dataSave = String.format("UPDATE %s SET %s = ? WHERE %s = ?", tableName, numberColumn, idColumn);
		operatePrepared(mysql, dataSave, prepared -> {
			for(int i = 0; i < numberList.size(); i++) {
				prepared.setInt(1, numberList.get(i));
				prepared.setInt(2, i + 1);
				prepared.addBatch();
			}
			prepared.executeBatch();
		});
	}
	
	/**
	 * 与えられたコードを元に単発のテーブルの構成に関する操作を実行する。
	 * @param mysql - 使用する接続。
	 * @param code - CREATE, DROP, RENAMEのコード。
	 * @throws Exception
	 */
	protected void operateStatement(Connection mysql, String code) throws Exception{
		try(Statement dropStatement = mysql.createStatement()){
			dropStatement.executeUpdate(code);
		}
	}
}