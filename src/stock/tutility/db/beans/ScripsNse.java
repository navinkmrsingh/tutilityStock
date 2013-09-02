package stock.tutility.db.beans;

public class ScripsNse {
	
	private int id;	
	private String nseCode;
	private String companyName;
	private String yahooCode;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNseCode() {
		return nseCode;
	}
	public void setNseCode(String nseCode) {
		this.nseCode = nseCode;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getYahooCode() {
		return yahooCode;
	}
	public void setYahooCode(String yahooCode) {
		this.yahooCode = yahooCode;
	}
	
	

}
