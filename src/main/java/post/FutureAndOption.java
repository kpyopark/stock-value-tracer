package post;

import java.util.Date;

public class FutureAndOption {

	String stockId;
	String stockName;
	String futureOptionType;
	String targetYm;
	String endTargetYm;
	boolean closedYn = false;
	String baseStockId;
	
	// For Spread
	String startFutureId;
	String endFutureId;
	
	String targetYmd;
	float  actionPrice;
	
	Date modifedDate;
	
	boolean isSpread = true;
	String futureBaseCode;
	String targetYmCode;
	String endTargetYmCode;
	
	private static String getYearMonth(String ymCode) {
		char yearCode = ymCode.charAt(0);
		String year = (2016 + yearCode - 'L') + "";
		String yearMonth;
		char monthCode = ymCode.charAt(1);
		if (monthCode > '0' && monthCode <= '9') {
			yearMonth = year + "0" + monthCode;
		} else {
			int month = monthCode - 'A' + 10;
			yearMonth = year + month;
		}
		return yearMonth;
	}

	public String getStockId() {
		return stockId;
	}
	
	public String getFutureBaseCode() {
		return this.futureBaseCode;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
		//
		isSpread = "4".equals(stockId.substring(0, 1));
		futureBaseCode = stockId.substring(1, 3);
		this.targetYmCode = stockId.substring(3, 5);
		this.endTargetYmCode = stockId.substring(5, 7);
		this.targetYm = getYearMonth(this.targetYmCode);
		if(isSpread) {
			this.endTargetYm = getYearMonth(this.endTargetYmCode);
			this.startFutureId = "1" + futureBaseCode + this.targetYmCode + "000";
			this.endFutureId = "1" + futureBaseCode + this.endTargetYmCode + "000";
			this.setFutureOptionType("SP");
		} else {
			this.setFutureOptionType("F");
		}
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public String getFutureOptionType() {
		return futureOptionType;
	}

	public void setFutureOptionType(String futureOptionType) {
		this.futureOptionType = futureOptionType;
	}

	public String getTargetYm() {
		return targetYm;
	}

	public void setTargetYm(String targetYm) {
		this.targetYm = targetYm;
	}

	public String getEndTargetYm() {
		return endTargetYm;
	}

	public void setEndTargetYm(String endTargetYm) {
		this.endTargetYm = endTargetYm;
	}

	public Boolean getClosedYn() {
		return closedYn;
	}

	public void setClosedYn(Boolean closedYn) {
		this.closedYn = closedYn;
	}

	public String getBaseStockId() {
		return baseStockId;
	}

	public void setBaseStockId(String baseStockId) {
		this.baseStockId = baseStockId;
	}

	public String getStartFutureId() {
		return startFutureId;
	}

	public void setStartFutureId(String startFutureId) {
		this.startFutureId = startFutureId;
	}

	public String getEndFutureId() {
		return endFutureId;
	}

	public void setEndFutureId(String endFutureId) {
		this.endFutureId = endFutureId;
	}

	public String getTargetYmd() {
		return targetYmd;
	}

	public void setTargetYmd(String targetYmd) {
		this.targetYmd = targetYmd;
	}

	public float getActionPrice() {
		return actionPrice;
	}

	public void setActionPrice(float actionPrice) {
		this.actionPrice = actionPrice;
	}

	public Date getModifedDate() {
		return modifedDate;
	}

	public void setModifedDate(Date modifedDate) {
		this.modifedDate = modifedDate;
	}
	
	
	
}
