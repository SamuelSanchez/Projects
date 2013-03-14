package stockretrieval;

public class CompanyInformation {

	private String Symbol;
	private String Name;
	private Stock.Exchange Exchange;
	private String IPOYear;
	private String Sector;
	private String Industry;
	private String SummaryQuote;
	
	public CompanyInformation(){
		Symbol = null;
		Name = null;
		Exchange = null;
		IPOYear = null;
		Sector = null;
		Industry = null;
		SummaryQuote = null;
	}

	//////////////////////
	//		Getters		//
	//////////////////////
	
	public String getSymbol() {
		return Symbol;
	}

	public String getName() {
		return Name;
	}

	public Stock.Exchange getExchange() {
		return Exchange;
	}

	public String getIPOYear() {
		return IPOYear;
	}

	public String getSector() {
		return Sector;
	}

	public String getIndustry() {
		return Industry;
	}

	public String getSummaryQuote() {
		return SummaryQuote;
	}

	//////////////////////
	//		Setters		//
	//////////////////////
	
	public void setSymbol(String symbol) {
		Symbol = symbol;
	}

	public void setName(String name) {
		Name = name;
	}

	public void setExchange(Stock.Exchange exchange) {
		Exchange = exchange;
	}

	public void setIPOYear(String iPOYear) {
		IPOYear = iPOYear;
	}

	public void setSector(String sector) {
		Sector = sector;
	}

	public void setIndustry(String industry) {
		Industry = industry;
	}

	public void setSummaryQuote(String summaryQuote) {
		SummaryQuote = summaryQuote;
	}
}
