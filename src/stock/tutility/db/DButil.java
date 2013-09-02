package stock.tutility.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import stock.tutility.helpers.InputHelper;


public class DButil {
//	private static final String MySQL_USERNAME = "navin";
//	private static final String MySQL_PASSWORD = "pass";
//	private static final String HSQL_USERNAME = "SA";
//	private static final String HSQL_PASSWORD = "";
//	private static final String HSQL_CONN_STRING = "jdbc:hsqldb:dataHsql/tutilityStock";
//	private static final String MySQL_CONN_STRING = "jdbc:mysql://localhost/tutilityStock";
//	
//	public static Connection getConnection(DBtype dbType) throws SQLException {
//		switch (dbType) {
//		case MYSQL:
//			return DriverManager.getConnection(MySQL_CONN_STRING, MySQL_USERNAME, MySQL_PASSWORD);				
//		case HSQLDB:
//			return DriverManager.getConnection(HSQL_CONN_STRING, HSQL_USERNAME, HSQL_PASSWORD);					
//		default:
//			return null;
//		}
//	}
	
	public static void dropTable(DBtype dbType, String tableName, Connection conn) {		
		String sql = "";
		
		switch (dbType) {
		case MYSQL:
			sql = "DROP TABLE ";
			break;			
		case HSQLDB:
			sql = "DROP TABLE PUBLIC.";
			break;	
		default:
			break;
		}
		
		sql+= tableName;
	
		String reply = InputHelper.getInput("You are about to delete a complete table named '" + tableName + "' and \n" +
				"all the included DATA. This can't be reverted.\n Are you sure you want to DELETE " + tableName + "? (yes/no) : ");		
		if(reply.equals("yes")){
			try (
				//Connection conn = DButil.getConnection(dbType);
				Statement stmt = conn.createStatement();				
				){
			stmt.executeUpdate(sql);
			System.out.println("\nTABLE '" + tableName + "' DELETED!");
			
			}catch (SQLException e) {
				System.err.println(e);
			}
		} else{
			if(reply.equals("no")){
				System.out.println("\nDeletion of TABLE '" + tableName + "' ABORTED!");
			}else{
				System.out.println("\nPlease reply in 'yes' or 'no' only");
				dropTable(dbType, tableName, conn);
			}			
		}		
	}

	public static void createTable(DBtype dbType, String tableName, String sql, Connection conn) {
		
		try (
				//Connection conn = DButil.getConnection(dbType);
				Statement stmt = conn.createStatement();				
				){
			stmt.executeUpdate(sql);
			System.out.println("\nTABLE '" + tableName + "' CREATED!");
			
		}catch (SQLException e) {
			System.err.println(e);			
		}
		
	}
	
	public static void alterTable(DBtype dbType, String tableName, String sql, Connection conn) {
		
		try (
				//Connection conn = DButil.getConnection(dbType);
				Statement stmt = conn.createStatement();				
				){
			stmt.executeUpdate(sql);
			System.out.println("\nTABLE '" + tableName + "' CREATED!");
			
		}catch (SQLException e) {
			System.err.println(e);			
		}
		
	}
	
	public static void runSql(DBtype dbType, String sql, Connection conn) {
		
		try (
				//Connection conn = DButil.getConnection(dbType);
				Statement stmt = conn.createStatement();				
				){
			stmt.executeUpdate(sql);
			System.out.println("Query ran successfully!");
			
		}catch (SQLException e) {
			System.err.println(e);			
		}
		
	}
}