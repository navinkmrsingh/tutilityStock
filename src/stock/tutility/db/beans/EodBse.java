package stock.tutility.db.beans;

import java.math.BigDecimal;
import java.util.Date;

public class EodBse {

	private int scripId;
	private Date date;
	private BigDecimal open;
	private BigDecimal high;
	private BigDecimal low;
	private BigDecimal close;
	private long volume;
	private BigDecimal adjClose;
	public int getScripId() {
		return scripId;
	}
	public void setScripId(int scripId) {
		this.scripId = scripId;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public BigDecimal getOpen() {
		return open;
	}
	public void setOpen(BigDecimal open) {
		this.open = open;
	}
	public BigDecimal getHigh() {
		return high;
	}
	public void setHigh(BigDecimal high) {
		this.high = high;
	}
	public BigDecimal getLow() {
		return low;
	}
	public void setLow(BigDecimal low) {
		this.low = low;
	}
	public BigDecimal getClose() {
		return close;
	}
	public void setClose(BigDecimal close) {
		this.close = close;
	}
	public long getVolume() {
		return volume;
	}
	public void setVolume(long volume) {
		this.volume = volume;
	}
	public BigDecimal getAdjClose() {
		return adjClose;
	}
	public void setAdjClose(BigDecimal adjClose) {
		this.adjClose = adjClose;
	}	
	
}
