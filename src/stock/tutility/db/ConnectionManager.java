package stock.tutility.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager
{
	private static ConnectionManager instance = null;

	private static final String MySQL_USERNAME = "navin";
	private static final String MySQL_PASSWORD = "pass";
	private static final String HSQL_USERNAME = "SA";
	private static final String HSQL_PASSWORD = "";
	private static final String HSQL_CONN_STRING = "jdbc:hsqldb:dataHsql/tutilityStock";
	private static final String MySQL_CONN_STRING = "jdbc:mysql://localhost/tutilityStock";

	private DBtype dbType = DBtype.MYSQL;

	private Connection conn = null;

	private ConnectionManager() {
	}

	public static ConnectionManager getInstance() {
		if (instance == null) {
			instance = new ConnectionManager();
		}
		return instance;
	}

	public void setDBType(DBtype dbType) {
		this.dbType = dbType;
	}

	public DBtype getDBType() {
		return this.dbType;
	}
	
	private boolean openConnection()
	{
		try {
			switch (dbType) {

			case MYSQL:
				conn = DriverManager.getConnection(MySQL_CONN_STRING, MySQL_USERNAME, MySQL_PASSWORD);
				return true;

			case HSQLDB:
				conn = DriverManager.getConnection(HSQL_CONN_STRING, HSQL_USERNAME, HSQL_PASSWORD);
				return true;

			default: 
				return false;
			}
		}
		catch (SQLException e) {
			System.err.println(e);
			return false;
		}

	}

	public Connection getConnection(){
		if (conn == null) {
			if (openConnection()) {
				System.out.println("Connection opened\n");
				return conn;
			} else {
				return null;
			}
		}
		return conn;
	}

	public void close() {
		System.out.println("\nClosing connection for DB - '" + dbType + "'\n");
		try {
			conn.close();
			conn = null;
		} catch (Exception e) {
		}
	}

}