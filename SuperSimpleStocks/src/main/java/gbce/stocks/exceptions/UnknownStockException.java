package gbce.stocks.exceptions;

public class UnknownStockException extends Exception {
	public UnknownStockException(String symbol){
		super("Unknown stock. Symbol : " + symbol);
	}	
}
