package savedata;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class OperationSQL {
	/**
	 * MySQLのデータベース接続情報用ファイルの名称。
	 */
	private static final String MYSQL_FILE = "db.properties";
	
	/**
	 * MYSQL_FILEでのデータベースURL名保存先
	 */
	private static final String URL = "url";
	
	/**
	 * MYSQL_FILEでのユーザー名保存先
	 */
	private static final String USER = "user";
	
	/**
	 * MYSQL_FILEでのパスワード保存先
	 */
	private static final String PASS = "pass";
	
	@FunctionalInterface
	static interface Task {
	    void run() throws Exception;
	}
	
	static Connection connectMysql() {
		try(InputStream selectData = new FileInputStream(MYSQL_FILE)) {
			Properties mysqlData = new Properties();
			mysqlData.load(selectData);
			return DriverManager.getConnection(mysqlData.getProperty(URL), mysqlData.getProperty(USER), mysqlData.getProperty(PASS));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static void closeConnection(Connection mysql) {
		try {
			mysql.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	static void executeSQL(Connection mysql, Task task) {
		try {
			mysql.setAutoCommit(false);
			task.run();
			mysql.commit();
		}catch (Exception e1) {
			try {
				if(mysql != null) {
					mysql.rollback();
				}
			}catch (Exception e2) {
				e2.printStackTrace();
			}
			e1.printStackTrace();
		}finally{
			try {
				if(mysql != null) {
					mysql.setAutoCommit(true);
				}
			}catch (Exception e3) {
				e3.printStackTrace();
			}
		}
	}
}