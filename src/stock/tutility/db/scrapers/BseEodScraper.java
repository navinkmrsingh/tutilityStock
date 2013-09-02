package stock.tutility.db.scrapers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import stock.tutility.db.ConnectionManager;
import stock.tutility.db.DBtype;
import stock.tutility.db.DButil;
import stock.tutility.db.beans.EodBse;
import stock.tutility.db.tables.EodBseManager;
import stock.tutility.db.tables.ScripsBseManager;

public class BseEodScraper {
	private static Connection conn = ConnectionManager.getInstance().getConnection();
	private static final String SCRIPS_TABLE_NAME = "tsScripsBse";
	
	public static boolean scrapeBseEod(DBtype dbType, int scripId, String scripCode) throws SQLException, ParseException {

		Statement stmt = null;
		ResultSet rs = null;		

		try {
//				File input = new File("files/sbiEod1.xml");
//				Document doc = Jsoup.parse(input, "UTF-8", "");
			
			String url = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20csv%20where%20url%3D'http%3A%2F%2Fichart.finance.yahoo.com%2Ftable.csv%3Fs%3D"+scripCode+".BO%26d%3D"+7+"%26e%3D"+24+"%26f%3D2013%26g%3Dd%26a%3D0%26b%3D3%26c%3D1900%26ignore%3D.csv'";
			Document doc = Jsoup.connect(url).get();
			Elements results = doc.select("results");
			Elements rows = results.select("row");
			ArrayList<String> list = new ArrayList<String>();
			int count = 0;
			
			list.add("Date");
			list.add("Open");
			list.add("High");
			list.add("Low");
			list.add("Close");
			list.add("Volume");
			list.add("Adj Close");			
			
			for (Element tr : rows) {
				String date = tr.select("col0").html();
				String open = tr.select("col1").html();
				String high =  tr.select("col2").html();					
				String low =  tr.select("col3").html();					
				String close =  tr.select("col4").html();					
				String volume =  tr.select("col5").html();					
				String adjClose =  tr.select("col6").html();					
				
				if(!list.contains(date)){
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
					java.util.Date sDate = sdf1.parse(date);
					String dateString = sdf1.format(sDate);
					java.sql.Date sqlDate = new java.sql.Date(sDate.getTime());
					
					BigDecimal sOpen = new BigDecimal(open);
					BigDecimal sHigh = new BigDecimal(high);
					BigDecimal sLow = new BigDecimal(low);
					BigDecimal sClose = new BigDecimal(close);
					BigDecimal sAdjClose = new BigDecimal(adjClose);
					long sVolume = Long.parseLong(volume);						
					
					//int scripId = Integer.parseInt(smbl.html().toString());
					System.out.println("Date: " + dateString);
					System.out.println("Open: " + open);
					System.out.println("High: " + high);
					System.out.println("Low: " + low);
					System.out.println("Close: " + close);
					System.out.println("Volume: " + volume);
					System.out.println("Adjusted Close: " + adjClose);
					count++;
					
					EodBse bean = new EodBse();
					bean.setScripId(scripId);
					bean.setDate(sqlDate);
					bean.setOpen(sOpen);
					bean.setHigh(sHigh);
					bean.setLow(sLow);
					bean.setClose(sClose);
					bean.setVolume(sVolume);
					bean.setAdjClose(sAdjClose);
					
					
					
					
					/**
					 * MySql/hSql INSERT Method
					 */
						try {
							boolean result = EodBseManager.insert(bean);
							
							if (result) {
								System.out.println("New row with primary key was inserted");
							}
						} catch (Exception e) {
							System.out.println(e);
						}
					
					
					/**
					 * MongoDb INSERT Method 
					 */
//							MongoClient mongo = new MongoClient( "localhost" , 27017 );
//
//							DB db = mongo.getDB("tsTutilityStock");
//							DBCollection table = db.getCollection("tsScripBse");
//							
//							BasicDBObject document = new BasicDBObject();
//							document.put("company", company.html().toString());
//							document.put("bseCode", code.html().toString());
//							document.put("bseSymbol", scripId);
//							
//							table.insert(document);
//							System.out.println("New row with primary key " + count + " was inserted");
				}
				
				System.out.println("--------------------------------------");
			}
			
			System.out.println("Total scrips: " + count);
			System.out.println("==================================================");
			
			/**
			 * MySql/hSql display method
			 */
//				ScripsBseManager.displayAllRows();				
			
		} catch (IOException e) {				
			System.err.println(e);
			return false;
		} finally {
			if (rs != null){
				rs.close();
			}
			if (stmt != null){
				stmt.close();
			}
		}
		return true;	
	}

	public static void downloadBseEod(DBtype dbType){
		String sql = "SELECT id, scripId, bseCode, eodStatus FROM " + SCRIPS_TABLE_NAME + " WHERE eodStatus=0";
		try (				
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				){

			int count = 0;
			while (rs.next()) {
				int id = rs.getInt("id");
				int scripId = rs.getInt("scripId");
				String bseCode = rs.getString("bseCode");
				try {
					boolean affected = scrapeBseEod(dbType, scripId, bseCode);
					if(affected){
						try {
							ScripsBseManager.updateEodStatus(scripId);
						} catch (Exception e) {
							System.err.println(e);
						}
					}
				} catch (ParseException e) {					
					e.printStackTrace();
				}
				
				count++;
				
				System.out.println("ID: " + id);				
				System.out.println("Count: " + count);
			}
			
		}
		catch (SQLException e) {
			System.err.println(e);			
		} 
	}
	
	public static void copyEod4mHql(DBtype dbType) throws IOException{
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader("files/hSqlBseEodData.txt"));
			int count = 0;
			
			try {
				while (in.ready()) {
				  String sql = in.readLine();
				  count++;
				  DButil.runSql(dbType, sql, conn);
//				  System.out.println(sql);
				  System.out.println(count);
				}
				System.out.println("Total # of lines: " + count);
			} catch (IOException e) {
				System.err.println(e);
			}finally{
				if(in!=null){
					in.close();
				}
			}			
			
		} catch (FileNotFoundException e) {
			System.err.println(e);
		}		
		
	}
	
	public static void copyMultiEod4mHql(DBtype dbType) throws IOException{
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader("files/hSqlBseEodData5.txt"));
			int count = 0;
			String sql = "";
			String sq = "INSERT INTO `tseodbse` (`scripId`, `date`, `open`, `high`, `low`, `close`, `volume`, `adjClose`) VALUES";
			String l = "";
			
			try {
				while (in.ready()) {
				  count++;
				  String s = in.readLine();				  
				  
				  String insert = "";						  
				  insert = s.replace("INSERT INTO TSEODBSE VALUES", "");
				  insert = insert.replace(")", "),");
				  l+= insert;
				  if(count%1000 == 0){
					  copyMultiEod4mHqlExecSql(dbType, sql, sq, l);
					  l = "";
				  }					  
				  System.out.println(count);
				}
				copyMultiEod4mHqlExecSql(dbType, sql, sq, l);
				
				System.out.println("Total # of lines: " + count); 
			} catch (IOException e) {
				System.err.println(e);
			}finally{
				if(in!=null){
					in.close();
				}
			}			
			
		} catch (FileNotFoundException e) {
			System.err.println(e);
		}		
		
	}
	
	public static void copyMultiEod4mHqlExecSql (DBtype dbType, String sql, String sq, String l) {
		sql = sq+l;
		sql = sql.substring(0, sql.length()-1);
		DButil.runSql(dbType, sql, conn);		
//		System.out.println(sql);
	}
}
