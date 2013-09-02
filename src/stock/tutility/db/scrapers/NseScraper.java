package stock.tutility.db.scrapers;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import stock.tutility.db.DBtype;
import stock.tutility.db.beans.ScripsNse;
import stock.tutility.db.tables.ScripsNseManager;

public class NseScraper {
	
	public static void scrapeNseSymbols(DBtype dbType) throws SQLException {

//		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
//		DBtype dbType = DBtype.MYSQL;	
		
		try {

			try {
				File input = new File("files/Yahoo_Finance_NSE_Codes.xml");
				Document doc = Jsoup.parse(input, "UTF-8", "");
				
//				Document doc = Jsoup.connect("http://www.marutidelhi.in/bse_symbols.html").get();
//				Elements table = doc.select("Table");
				Elements rows = doc.select("Row");
				
//				System.out.println(table);
				int count = 0;
				
				for (Element row : rows) {
						Element nseCode = row.select("Cell").first();
						Element yahooCode = nseCode.nextElementSibling();
						Element company = yahooCode.nextElementSibling();
						
						String nseCodeStr = nseCode.select("data").html().toString();
						String yahooCodeStr = yahooCode.select("data").html().toString();
						String companyStr = company.select("data").html().toString();
						
						
						if(company != null){
						
							System.out.println("NSE Code: " + nseCodeStr);
							System.out.println("Yahoo code: " + yahooCodeStr);
							System.out.println("Company: " + companyStr);
							count++;
							
							ScripsNse bean = new ScripsNse();
							bean.setNseCode(nseCodeStr);
							bean.setCompanyName(companyStr);
							bean.setYahooCode(yahooCodeStr);
							
							
							
							
							/**
							 * MySql/hSql INSERT Method
							 */
							boolean result;
							try {
								result = ScripsNseManager.insert(bean, dbType);
								if (result) {
									System.out.println("New row with primary key " + bean.getId() + " was inserted");
								}
							} catch (Exception e) {								
								System.err.println(e);
							}
							
							
														
							
							/**
							 * MongoDb INSERT Method 
							 */
//							MongoClient mongo = new MongoClient( "localhost" , 27017 );
//
//							DB db = mongo.getDB("tsTutilityStock");
//							DBCollection table = db.getCollection("tsScripNse");
//							
//							BasicDBObject document = new BasicDBObject();
//							document.put("company", company.html().toString());
//							document.put("nseCode", code.html().toString());
//							document.put("nseSymbol", scripId);
//							
//							table.insert(document);
//							System.out.println("New row with primary key " + count + " was inserted");
						}
						
						System.out.println("--------------------------------------");
					}
				/**
				 * MySql/hSql display method
				 */
				ScripsNseManager.displayAllRows();
				
				System.out.println("Total scrips: " + count);
				
				
			} catch (IOException e) {				
				System.err.println(e);
			}
			
		} finally {
			if (rs != null){
				rs.close();
			}
			if (stmt != null){
				stmt.close();
			}
//			if (conn != null){
//				conn.close();
//			}
		}
	}
}
