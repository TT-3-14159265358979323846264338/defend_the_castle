package savedata;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
	
	/**
	 * データベース接続<br>
	 * {@link #operateSQL}でのみ生成を許可する
	 */
	protected Connection mysql;
	
	/**
	 * 例外は全て{@link #operateSQL}で呼び出すメソッドで処理するため、スローできるメソッドとして定義
	 */
	@FunctionalInterface
	interface Task {
	    void run() throws Exception;
	}
	
	/**
	 * MySQLへ接続し、指定されたメソッドを実行する。
	 * メソッド終了後、接続を破棄する。
	 * 接続が完了すると{@link #mysql Connection mysql}に接続情報が格納される。<br>
	 * メソッドで例外が発生した場合、rollbackが行われる。
	 * そのため、メソッド中で例外処理を記載する時は、必ずスローも記載する。
	 * @param task - MySQLでの操作メソッド。
	 * 				メソッドでは{@link #mysql Connection mysql}を使用してデータのやり取りを行うことができる。
	 */
	void operateSQL(Task task) {
		mysql = connectMysql();
		executeSQL(task);
		closeConnection();
	}
	
	Connection connectMysql() {
		try(InputStream selectData = new FileInputStream(MYSQL_FILE)) {
			Properties mysqlData = new Properties();
			mysqlData.load(selectData);
			return DriverManager.getConnection(mysqlData.getProperty(URL), mysqlData.getProperty(USER), mysqlData.getProperty(PASS));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	void executeSQL(Task task) {
		try {
			mysql.setAutoCommit(false);
			task.run();
			mysql.commit();
		}catch (Exception e) {
			rollbackMysql();
			e.printStackTrace();
		}
	}
	
	void rollbackMysql() {
		try {
			if(mysql != null) {
				mysql.rollback();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void closeConnection() {
		try {
			if(mysql != null) {
				mysql.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	void dataLoad(String tableName, String column, List<Integer> numberList) throws Exception {
		String dataLoad = String.format("SELECT * FROM %s", tableName);
		try(PreparedStatement prepared = mysql.prepareStatement(dataLoad);
				ResultSet table = prepared.executeQuery()){
			numberList.clear();
			while(table.next()) {
				numberList.add(table.getInt(column));
			}
		}
	}
	
	void dataSave(String tableName, String numberColumn, String idColumn, List<Integer> numberList) throws Exception{
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