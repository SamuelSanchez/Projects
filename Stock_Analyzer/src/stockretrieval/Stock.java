package stockretrieval;

public class Stock {
	
	public enum Exchange{ NYSE, NASDAQ, AMEX };
	public enum Currency{ USD };

	private String symbol;
	private Currency currency;
	private Float lastClose;				//Current price
	private Float open;					//Open price, not necessarily the previous closing price
	private Float change;					//Amount_Change
	private Float percentChange;		//100*(current-closing)/closing ?
	private Float close;					//Price of closing price
	private Float high;					//The highest it hit on a day
	private Float low;						//The lowest it hit on a day
	private Integer volume;					//The number of shares sold on the Stock Exchange during the day
	private Integer averageVolume;			//The liquidity of the company
	private Float marketCapitalization;	//Dollar value of the company's outstanding shares (company size)
											//marketCap = Company's Share * price of a share
	private String symbolUrl;				//URL of the stock price
	private String chartUrl;				//URL of the stock chart
	
	public Stock(){
		symbol = null;
		currency = null;
		lastClose = null;
		open = null;
		change = null;
		percentChange = null;
		close = null;
		high = null;
		low = null;
		volume = null;
		averageVolume = null;
		marketCapitalization = null;
		symbolUrl = null;
		chartUrl = null;			
	}
	
	//////////////////////
	//		Setters		//
	//////////////////////

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void setCurrency(Currency uSD) {
		currency = uSD;
	}
	
	public void setLastClose(Float lastClose) {
		this.lastClose = lastClose;
	}
	
	public void setOpen(Float open) {
		this.open = open;
	}
	
	public void setChange(Float change) {
		this.change = change;
	}
	
	public void setPercentChange(Float percentageChange) {
		this.percentChange = percentageChange;
	}
	
	public void setClose(Float close) {
		this.close = close;
	}
	
	public void setHigh(Float high) {
		this.high = high;
	}
	
	public void setLow(Float low) {
		this.low = low;
	}
	
	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	public void setAverageVolume(Integer averageVolume) {
		this.averageVolume = averageVolume;
	}
	
	public void setMarketCapitalization(Float marketCapitalization) {
		this.marketCapitalization = marketCapitalization;
	}
	
	public void setSymbolUrl(String symbolUrl) {
		this.symbolUrl = symbolUrl;
	}
	
	public void setChartUrl(String chartUrl) {
		this.chartUrl = chartUrl;
	}

	//////////////////////
	//		Getters		//
	//////////////////////
	
	public String getSymbol() {
		return symbol;
	}
	
	public Currency getCurrency() {
		return currency;
	}

	public Float getLastClose() {
		return lastClose;
	}

	public Float getOpen() {
		return open;
	}

	public Float getChange() {
		return change;
	}

	public Float getPercentChange() {
		return percentChange;
	}

	public Float getClose() {
		return close;
	}

	public Float getHigh() {
		return high;
	}

	public Float getLow() {
		return low;
	}

	public Integer getVolume() {
		return volume;
	}
	
	public Integer getAverageVolume() {
		return averageVolume;
	}

	public Float getMarketCapitalization() {
		return marketCapitalization;
	}

	public String getSymbolUrl() {
		return symbolUrl;
	}

	public String getChartUrl() {
		return chartUrl;
	}
	
	////////////////////////////
	//		Other Methods	  //
	////////////////////////////
	
	public boolean equals(Object obj){
		//Return false for obvious inequalities
		if(obj == null || !(obj instanceof Stock)) return false;
		
		//Check most parameters
		if(this.getSymbol() != ((Stock) obj).getSymbol() || this.getLastClose() != ((Stock) obj).getLastClose() || 
		   this.getOpen() != ((Stock) obj).getOpen() || this.getChange() != ((Stock) obj).getChange() ||
		   this.getPercentChange() != ((Stock) obj).getPercentChange() || this.getClose() != ((Stock) obj).getClose() ||
		   this.getHigh() != ((Stock) obj).getHigh() || this.getLow() != ((Stock) obj).getLow() ||
		   this.getVolume() != ((Stock) obj).getVolume() || this.getAverageVolume() != ((Stock) obj).getAverageVolume() ||
		   this.getMarketCapitalization() != ((Stock) obj).getMarketCapitalization()){
			return false;
		}
		
		//They must be equal
		return true;
	}
		
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("Symbol : " + symbol + Global.NEWLINE);
		buffer.append("Currency : " + currency.toString() + Global.NEWLINE);
		buffer.append("Last Close - current? : " + lastClose + Global.NEWLINE);
		buffer.append("Open : " + open + Global.NEWLINE);
		buffer.append("Change : " + change + Global.NEWLINE);
		buffer.append("Percentage Change : " + percentChange + Global.NEWLINE);
		buffer.append("Close : " + close + Global.NEWLINE);
		buffer.append("High : " + high + Global.NEWLINE);
		buffer.append("Low : " + low + Global.NEWLINE);
		buffer.append("Volume : " + volume + Global.NEWLINE);
		buffer.append("Average Volume : " + averageVolume + Global.NEWLINE);
		buffer.append("Market Capitalization : " + marketCapitalization + Global.NEWLINE);
		buffer.append("Symbol Url : " + symbolUrl + Global.NEWLINE);
		buffer.append("Chart Url : " + chartUrl + Global.NEWLINE);
		return buffer.toString();
	}
}
