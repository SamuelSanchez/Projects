package stockretrieval;

public interface StockDao extends DaoInterface {
	
	public boolean isSymbolValid(String symbol);
	
	public boolean isNameValid(String name);
	
	public String getSymbolByName(String name);
	
	public String getNameBySymbol(String symbol);
}
