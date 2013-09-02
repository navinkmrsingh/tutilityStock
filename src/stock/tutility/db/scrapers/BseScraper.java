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
import stock.tutility.db.beans.ScripsBse;
import stock.tutility.db.tables.ScripsBseManager;

public class BseScraper {	
	
	public static void scrapeBseSymbols(DBtype dbType) throws SQLException {

		Statement stmt = null;
		ResultSet rs = null;	
		
		try {

			try {
				File input = new File("files/bse_symbols.html");
				Document doc = Jsoup.parse(input, "UTF-8", "");
				
//				Document doc = Jsoup.connect("http://www.marutidelhi.in/bse_symbols.html").get();
				Elements tbody = doc.select("tbody");
				Elements trs = tbody.select("tr");
				int count = 0;
				
				for (Element tr : trs) {
						Element company = tr.select("td").first();
						Element code = company.nextElementSibling();
						Element smbl = code.nextElementSibling();					
						
						if(smbl != null){
							int scripId = Integer.parseInt(smbl.html().toString());
							System.out.println("Company: " + company.html());
							System.out.println("BSE code: " + code.html());
							System.out.println("BSE symbol: " + smbl.html());
							count++;
							
							ScripsBse bean = new ScripsBse();
							bean.setScripId(scripId);
							bean.setCompanyName(company.html().toString());
							bean.setBseCode(code.html().toString());
							
							
							
							
							/**
							 * MySql/hSql INSERT Method
							 */
							try {
								boolean result = ScripsBseManager.insert(bean);
								
								if (result) {
									System.out.println("New row with primary key " + bean.getScripId() + " was inserted");
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
				ScripsBseManager.displayAllRows();
				
				
				
				
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
		}
	}

}
