package stock.tutility.db.beans;

public class ScripsBse {

	private int id;	
	private int scripId;
	private String companyName;
	private String bseCode;
	private int eodStatus;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getScripId() {
		return scripId;
	}
	public void setScripId(int scripId) {
		this.scripId = scripId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getBseCode() {
		return bseCode;
	}
	public void setBseCode(String bseCode) {
		this.bseCode = bseCode;
	}
	public int getEodStatus() {
		return eodStatus;
	}
	public void setEodStatus(int eodStatus) {
		this.eodStatus = eodStatus;
	}
	
	
	
}
