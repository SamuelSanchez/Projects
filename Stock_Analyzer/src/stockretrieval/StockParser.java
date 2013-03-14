package stockretrieval;

public final class StockParser {
	
	//Prevent from creating a class
	private StockParser(){
	}
 	
	//This assumes that only one stock is retrieve at the time
	public static Stock generateStockFromXML(String xml){
		String[] parts = xml.split("\\<*?\\>");
		Stock stock = new Stock();
		
		//Not a really good parser, but it parses
		for(String part : parts){
			//Only do the verify on one because a check will be perform on this value
			if(part.contains("<symbol ")){
				int index = part.indexOf("\""); //Get the first quote
				index++;
				String symbol = (part.substring(index, part.indexOf("\"", index)));
				if(symbol.equals("NONE")){
					break;
				}
				stock.setSymbol(symbol);
			}
			else if(part.contains("<currency ")){
				int index = part.indexOf("\""); //Get the first quote
				index++;
				String currency = part.substring(index, part.indexOf("\"", index));
				Stock.Currency curr = null;
				if(currency.equalsIgnoreCase(Stock.Currency.USD.toString())) curr = Stock.Currency.USD;
//				else if(currency.equalsIgnoreCase(Stock.Exchange.NYSE.toString())) curr = Stock.Exchange.NYSE;
				else break;
				stock.setCurrency(curr);
			}
			else if(part.contains("<last ")){
				int index = part.indexOf("\""); //Get the first quote
				index++;
				stock.setLastClose(Float.parseFloat(part.substring(index, part.indexOf("\"", index))));
			}
			else if(part.contains("<high ")){
				int index = part.indexOf("\""); //Get the first quote
				index++;
				stock.setHigh(Float.parseFloat(part.substring(index, part.indexOf("\"", index))));
			}
			else if(part.contains("<low ")){
				int index = part.indexOf("\""); //Get the first quote
				index++;
				stock.setLow(Float.parseFloat(part.substring(index, part.indexOf("\"", index))));
			}
			else if(part.contains("<volume ")){
				int index = part.indexOf("\""); //Get the first quote
				index++;
				stock.setVolume(Integer.parseInt(part.substring(index, part.indexOf("\"", index))));
			}
			else if(part.contains("<avg_volume ")){
				int index = part.indexOf("\""); //Get the first quote
				index++;
				stock.setAverageVolume(Integer.parseInt(part.substring(index, part.indexOf("\"", index))));
			}
			else if(part.contains("<market_cap ")){
				int index = part.indexOf("\""); //Get the first quote
				index++;
				stock.setMarketCapitalization(Float.parseFloat(part.substring(index, part.indexOf("\"", index))));
			}
			else if(part.contains("<open ")){
				int index = part.indexOf("\""); //Get the first quote
				index++;
				stock.setOpen(Float.parseFloat(part.substring(index, part.indexOf("\"", index))));
			}
			else if(part.contains("<y_close ")){
				int index = part.indexOf("\""); //Get the first quote
				index++;
				stock.setClose(Float.parseFloat(part.substring(index, part.indexOf("\"", index))));
			}
			else if(part.contains("<change ")){
				int index = part.indexOf("\""); //Get the first quote
				index++;
				stock.setChange(Float.parseFloat(part.substring(index, part.indexOf("\"", index))));
			}
			else if(part.contains("<perc_change ")){
				int index = part.indexOf("\""); //Get the first quote
				index++;
				stock.setPercentChange(Float.parseFloat(part.substring(index, part.indexOf("\"", index))));
			}
			else if(part.contains("<symbol_url ")){
				int index = part.indexOf("\""); //Get the first quote
				index++;
				stock.setSymbolUrl("http://www.google.com" + part.substring(index, part.indexOf("\"", index)));
			}
			else if(part.contains("<chart_url ")){
				int index = part.indexOf("\""); //Get the first quote
				index++;
				stock.setChartUrl("http://www.google.com" + part.substring(index, part.indexOf("\"", index)));
			}
		}
		
		//Throws null if the data is not good - there's no symbol...
		if(stock.getSymbol() == null || stock.getCurrency() == null)
			stock = null;
		
		return stock;
	}
}
