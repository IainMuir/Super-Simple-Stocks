package gbce.stocks.exceptions;

public class ExistingStockException extends Exception {
	public ExistingStockException(String symbol) {
		super("AbstractStock already exists. Symbol : " + symbol);
	}
}
