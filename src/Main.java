import stock.tutility.db.ConnectionManager;
import stock.tutility.db.DBtype;
import stock.tutility.db.scrapers.BseEodScraper;
import stock.tutility.db.tables.EodBseManager;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("Starting Application!\n");
		long time = System.currentTimeMillis();  // Benchmarking******************************		
		
		DBtype dbType = DBtype.MYSQL;
		
		ConnectionManager.getInstance().setDBType(dbType);
		
/*
 * ******************************************* OPERATING ZONE **********************************************
 */
		
//		BseScraper.scrapeBseSymbols(dbType); 
//		NseScraper.scrapeNseSymbols(dbType);
//		BseEodScraper.scrapeBseEod(dbType, 12132, "SBI");
		
//		BseEodScraper.downloadBseEod(dbType);
//		BseEodScraper.copyMultiEod4mHql(dbType);
//		BseEodScraper.scrapeBseEodGbcAllFiles(dbType);
		
		String selectCondition = " WHERE scripId='IT'";

//		ScripsBseManager.displayAllRows();
//		ScripsNseManager.displayAllRows();
//		EodBseManager.displayAllRows(selectCondition);
		EodBseManager.countAllRows(dbType);
		
//		EodBseManager.addIndex(dbType);
		
//		ScripsBseManager.createTsScripsBseTable(dbType);
//		ScripsNseManager.createTsScripsNseTable(dbType);
//		EodBseManager.installTsEodBseTable(dbType);
		
//		ScripsBseManager.addEodStatusColumn(dbType);
//		ScripsBseManager.resetEodStatus();
				
//		ScripsBseManager.dropTsScripsBseTable(dbType);
//		ScripsNseManager.dropTsScripsNseTable(dbType);
//		EodBseManager.dropTsEodBseTable(dbType);				
		
		
//-----------------------------------------------------------------------------------------------------------
		
		ConnectionManager.getInstance().close();
		
		/*
		 * Benchmarking--------------------------------------------------------------------------
		 */
		long completedIn = System.currentTimeMillis() - time;
		int s = (int) (completedIn/1000);
		int mili = (int) (completedIn%1000);
		int m = s/60;
		int sec = s%60;
		int hour = m/60;
		int min = m%60;
		System.out.println("");
		System.out.println(hour + "hrs " + min + "min " + sec + "sec " + mili + "milisec " + "(Total: " + completedIn + " miliseconds)");
	}	
	
}
