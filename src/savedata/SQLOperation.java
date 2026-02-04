package savedata;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

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
	
	/**
	 * 例外は全て{@link #operateSQL}で呼び出すメソッドで処理するため、スローできるメソッドとして定義。
	 * Connection mysqlを渡して記述する。
	 */
	@FunctionalInterface
	interface SQLTask {
	    void run(Connection mysql) throws Exception;
	}
	
	/**
	 * MySQLへ接続し、指定されたメソッドを実行する。
	 * メソッド終了後、接続を破棄する。
	 * メソッドで例外が発生した場合、rollbackが行われる。
	 * そのため、メソッド中で例外処理を記載する時は、必ずスローも記載する。
	 * @param task - MySQLでの操作メソッド。Connection mysqlを渡して記述する。
	 */
	void operateSQL(SQLTask task) {
		CompletableFuture.runAsync(() -> {
			try(Connection mysql = connectMysql()){
				executeSQL(mysql, task);
			}catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	Connection connectMysql() throws Exception{
		try(InputStream selectData = new FileInputStream(MYSQL_FILE)) {
			Properties mysqlData = new Properties();
			mysqlData.load(selectData);
			return DriverManager.getConnection(mysqlData.getProperty(URL), mysqlData.getProperty(USER), mysqlData.getProperty(PASS));
		}
	}
	
	void executeSQL(Connection mysql, SQLTask task) throws Exception{
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
	 * 
	 */
	@FunctionalInterface
	interface ResultTask {
	    void run(ResultSet result) throws Exception;
	}
	
	/**
	 * 
	 * @param mysql
	 * @param code
	 * @param task
	 * @throws Exception
	 */
	void GetResult(Connection mysql, String code, ResultTask task) throws Exception{
		try(PreparedStatement prepared = mysql.prepareStatement(code);
				ResultSet result = prepared.executeQuery()) {
			task.run(result);
		}
	}
	
	void dataLoad(Connection mysql, String tableName, String column, List<Integer> numberList) throws Exception {
		String dataLoad = String.format("SELECT * FROM %s", tableName);
		try(PreparedStatement prepared = mysql.prepareStatement(dataLoad);
				ResultSet table = prepared.executeQuery()){
			numberList.clear();
			while(table.next()) {
				numberList.add(table.getInt(column));
			}
		}
	}
	
	void dataSave(Connection mysql, String tableName, String numberColumn, String idColumn, List<Integer> numberList) throws Exception{
		String dataSave = String.format("UPDATE %s SET %s = ? WHERE %s = ?", tableName, numberColumn, idColumn);
		try(PreparedStatement dataPrepared = mysql.prepareStatement(dataSave)) {
			for(int i = 0; i < numberList.size(); i++) {
				dataPrepared.setInt(1, numberList.get(i));
				dataPrepared.setInt(2, i + 1);
				dataPrepared.addBatch();
			}
			dataPrepared.executeBatch();
		}
	}
}