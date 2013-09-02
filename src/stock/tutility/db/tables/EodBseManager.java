package stock.tutility.db.tables;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import stock.tutility.db.ConnectionManager;
import stock.tutility.db.DBtype;
import stock.tutility.db.DButil;
import stock.tutility.db.beans.EodBse;

public class EodBseManager {

	private static Connection conn = ConnectionManager.getInstance().getConnection();
	private static final String TABLE_NAME = "tsEodBse";
	
	public static void installTsEodBseTable(DBtype dbType){
		createTsEodBseTable(dbType);
		addIndex(dbType);
	}
	
	public static void createTsEodBseTable(DBtype dbType) {
		
		String sql = "";
		
		switch (dbType) {
		case MYSQL:
			sql = "CREATE TABLE IF NOT EXISTS `"+TABLE_NAME+ "` ("+
					"`scripId` int(6) NOT NULL, " +
					"`date` date NOT NULL, " +
					"`open` decimal(11,2) DEFAULT NULL, " +
					"`high` decimal(11,2) DEFAULT NULL, " +
					"`low` decimal(11,2) DEFAULT NULL, " +
					"`close` decimal(11,2) DEFAULT NULL, " +
					"`volume` bigint(20) DEFAULT NULL, " +
					"`adjClose` decimal(11,2) DEFAULT NULL, " +
					"PRIMARY KEY (`scripId`,`date`)" +
				") ENGINE=InnoDB DEFAULT CHARSET=latin1";
			break;
			
		/**
		 * WARNING : Since HSQL doesn't check on itself that if a table exists or not, it falls on
		 * 			the caller to check if the desired table already exists.
		 * 			 Failing to do so will throw an SQL error for duplicate creation.
		 */
		case HSQLDB:
			sql = "CREATE TABLE PUBLIC."+TABLE_NAME+ "(" +
					"SCRIPID INTEGER NOT NULL, " +
					"\"DATE\" DATE NOT NULL, " +
					"\"OPEN\" DECIMAL(11,2), " +
					"HIGH DECIMAL(11,2), " +
					"LOW DECIMAL(11,2), " +
					"\"CLOSE\" DECIMAL(11,2), " +
					"VOLUME BIGINT, " +
					"ADJCLOSE DECIMAL(11,2)," +
					"PRIMARY KEY (SCRIPID, \"DATE\")" +
				")";
			break;	
		default:
			break;
		}		
	
		DButil.createTable(dbType, TABLE_NAME, sql, conn);		
	}
	
	public static void addIndex(DBtype dbType) {
		String sql = "";
		String indexName = "indScripId";
		String columnName = "scripId";
		switch (dbType) {
		case MYSQL:
			sql = "CREATE INDEX "+ indexName + " ON " + TABLE_NAME + " ("+columnName+")";
			break;
		case HSQLDB:
			sql = "CREATE INDEX PUBLIC."+ indexName + " ON PUBLIC." + TABLE_NAME + " ("+columnName+")";
			break;	

		default:
			break;
		}
		
		DButil.alterTable(dbType, TABLE_NAME, sql, conn);
		
	}
	
	public static void dropTsEodBseTable(DBtype dbType) {		
		DButil.dropTable(dbType, TABLE_NAME, conn);
	}
	
	public static boolean displayAllRows(String selectCondition) throws SQLException {

		String sql = "SELECT scripId, date, open, high, low, close, volume, adjClose  FROM " + TABLE_NAME + selectCondition;
		try (				
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				){

			int count = 0;
			System.out.println("BSE EOD Table:");
			while (rs.next()) {
				StringBuffer bf = new StringBuffer();
				bf.append(rs.getInt("scripId") + ": ");
				bf.append(rs.getString("date") +", ");
				bf.append(rs.getString("open") +", ");
				bf.append(rs.getString("high") +", ");
				bf.append(rs.getString("low") +", ");
				bf.append(rs.getString("close") +", ");
				bf.append(rs.getString("volume") +", ");
				bf.append(rs.getString("adjClose"));
				System.out.println(bf.toString());
				count++;
			}
			if(count == 0){
				System.out.println("*********** No Result to Display ***********");
			}else{
				System.out.println("Total output: " + count);
			}
		}
		catch (SQLException e) {
			System.err.println(e);
			return false;
		} 
		return true;
	}
	
	public static boolean insert(EodBse bean) throws Exception {

		String sql = "INSERT into " + TABLE_NAME + " (scripId, date, open, high, low, close, volume, adjClose) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		ResultSet keys = null;
		
		try (
				PreparedStatement stmt = conn.prepareStatement(sql, 
						Statement.RETURN_GENERATED_KEYS);
				) {
			
			stmt.setInt(1, bean.getScripId());
			stmt.setDate(2, (Date) bean.getDate());
			stmt.setBigDecimal(3, bean.getOpen());
			stmt.setBigDecimal(4, bean.getHigh());
			stmt.setBigDecimal(5, bean.getLow());
			stmt.setBigDecimal(6, bean.getClose());
			stmt.setLong(7, bean.getVolume());                         ///////////// vulnerable ///////////////
			stmt.setBigDecimal(8, bean.getAdjClose());
			
			int affected = stmt.executeUpdate();
			
			if (affected != 0) {
				System.out.println("Row inserted!");
			} else {
				System.err.println("No rows affected");
			}
			
		} catch (SQLException e) {
			System.err.println(e);
			return false;
		} finally{
			if(keys != null) keys.close();
		}
		return true;
	}
}