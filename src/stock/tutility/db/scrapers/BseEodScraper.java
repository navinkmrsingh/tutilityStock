package stock.tutility.db.scrapers;

import java.io.BufferedReader;
import java.io.File;
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
import java.util.Arrays;

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
	
	/**
	 * Scrapes BSE EODs from yahoo REST api in XML format for a given SCRIP
	 */
	public static boolean scrapeBseEod(DBtype dbType, String scripId, String yahooCode) throws SQLException, ParseException {

		Statement stmt = null;
		ResultSet rs = null;
		String scripUrl = "";
		if(yahooCode.equals("^BSESN")){
			scripUrl = "%255EBSESN";
		}else{
			scripUrl = yahooCode + ".BO";
		}
			
		try {
//				File input = new File("files/sbiEod1.xml");
//				Document doc = Jsoup.parse(input, "UTF-8", "");
			
			String url = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20csv%20where%20url%3D'http%3A%2F%2Fichart.finance.yahoo.com%2Ftable.csv%3Fs%3D"+scripUrl+"%26d%3D"+7+"%26e%3D"+26+"%26f%3D"+2013+"%26g%3Dd%26a%3D"+7+"%26b%3D"+26+"%26c%3D"+1900+"%26ignore%3D.csv'";
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

	/**
	 * Calls scrapeBseEod() one by one for each SCRIP  
	 */
	public static void downloadBseEod(DBtype dbType){
		String sql = "SELECT id, scripId, bseCode, eodStatus FROM " + SCRIPS_TABLE_NAME + " WHERE eodStatus=0";
		try (				
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				){
			
			int count = 0;
			while (rs.next()) {
				int id = rs.getInt("id");
				String scripId = rs.getString("scripId");
				String yahooCode = rs.getString("bseCode");
				try {
					boolean affected = scrapeBseEod(dbType, scripId, yahooCode);
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
	
	/**
	 * Copies & Inserts EODs from HSQLdb to MySql one-by-one
	 * @param dbType
	 * @throws IOException
	 */
	public static void copyEod4mHql(DBtype dbType) throws IOException{
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader("files/hSqlBseEodData.txt"));
			int count = 0;
			
			try {
				while (in.ready()) {
				  String sql = in.readLine();
				  count++;
				  DButil.runSqlUpdate(dbType, sql, conn);
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
	
	/**
	 * Copies  & Inserts EODs from HSQLdb to MySql 1000 at a time
	 * @param dbType
	 * @throws IOException
	 */
	public static void copyMultiEod4mHql(DBtype dbType) throws IOException{
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader("files/hSqlBseEodData5.txt"));
			int count = 0;
			String sql = "";
			String sq = "INSERT INTO `tseodbse` (`scripId`, `date`, `open`, `high`, `low`, `close`, `volume`, `adjClose`) VALUES	";
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
					  multiEodExecSql(dbType, sql, sq, l);
					  l = "";
				  }					  
				  System.out.println(count);
				}
				multiEodExecSql(dbType, sql, sq, l);
				
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
	
	private static void multiEodExecSql (DBtype dbType, String sql, String sq, String l) {
		if(l != ""){
			sql = sq+l;
			sql = sql.substring(0, sql.length()-1);
			DButil.runSqlUpdate(dbType, sql, conn);
			System.out.println(sql);
		}
		
	}

	/**
	 * Scrapes BSE EODs from GetBhavCopy files and Inserts into MySql
	 * @throws IOException 
	 */
	private static void scrapeBseEodGbc(DBtype dbType, String fileUrl, String fileName) throws IOException{
		
//		String fileUrl = "files/getBhav/bse/" + fileName + ".txt";
		BufferedReader in;
		String date = fileName.replace("-BSE-EQ", "");
		String sql = "";
		String sq = "INSERT INTO `tsEodBse` (`scripId`, `date`, `open`, `high`, `low`, `close`, `volume`, `adjClose`) VALUES";
		String l = "";
		
		String[] sectors = {"AUTO", "BANKEX", "CONSDURBL", "FMCG", "HLTHCARE", "IT", "METAL", "OILGAS", "TECK", 
							"CAPGOODS", "POWER", "PSU", "REALTY", "DOL-30", "DOL-100", "DOL-200"};
		ArrayList<String> sectorsArList = new ArrayList<String>();
		sectorsArList.addAll(Arrays.asList(sectors));
		
//		String[] scrips2 = {"CAPGOODS", "POWER", "PSU", "REALTY", "DOL-30", "DOL-100", "DOL-200"};		
//		ArrayList<String> scrips2ArList = new ArrayList<String>();
//		scrips2ArList.addAll(Arrays.asList(scrips2));
		
		
		try {
			in = new BufferedReader(new FileReader(fileUrl));
			int count = 0;
			
			try {
				while (in.ready()) {
				  String line = in.readLine();				  
				  String[] parts = line.split(",");				  
				  
				  if((!line.isEmpty()) && (!parts[0].equals("<ticker>")) /* && scripsArList.contains(parts[0])*/){
					  count++;
					  
					  String scripId = parts[0];
					  String open = parts[2];
					  String high = parts[3];
					  String low = parts[4];
					  String close = parts[5];
					  String volume = parts[6];
					  String adjClose = parts[5];
					  
					  l+= "('" + scripId + "','" + date + "'," + open + "," + high + "," + low + "," + close + "," + volume + "," + adjClose + "),";					 
					  
					  if(count%1000 == 0){
						  multiEodExecSql(dbType, sql, sq, l);
						  l = "";
					  }
					  
//					  System.out.println(line);
//					  System.out.println(count);
//					  System.out.println("open:"+ open);
//					  System.out.println("Array Length: "+parts.length);					  
				  }				  
				}
				multiEodExecSql(dbType, sql, sq, l);
				
//				if(l != ""){
//					l = l.substring(0, l.length()-1);
//				}				
//				sql = sq + l;
//				DButil.runSql(dbType, sql, conn);
				
				System.out.println("Date: " + date);
				System.out.println("Total # of lines: " + count);
//				System.out.println(sql);
//				System.out.println("-------------------------------------------------------------------");
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

	/**
	 * Calls scrapeBseEodGbc() for each file in GBC/BSE folder
	 * @throws IOException 
	 */
	public static void scrapeBseEodGbcAllFiles(DBtype dbType) throws IOException {
		String fileDir = "files/getBhav/bse/";
		File folder = new File(fileDir);
		File[] listOfFiles = folder.listFiles();

	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	    	  String fileFullName = listOfFiles[i].getName();
	    	  String fileName = fileFullName.replace(".txt", "");
	    	  String fileUrl = fileDir + fileFullName;
	    	  scrapeBseEodGbc(dbType, fileUrl, fileName);
	    	  
	    	  System.out.println("File: " + fileName);
	    	  System.out.println("File Url:" + fileUrl);	    	 
	      } else if (listOfFiles[i].isDirectory()) {
	    	  System.out.println("Directory: " + listOfFiles[i].getName());
	      }
	      System.out.println("-------------------------------------------------------------------");
	    }
	    System.out.println("Number of Files: "+ listOfFiles.length);
	    
	}
}
