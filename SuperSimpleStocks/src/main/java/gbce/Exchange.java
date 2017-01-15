package gbce;

import gbce.stocks.Stock;
import gbce.stocks.BuyOrSell;
import gbce.stocks.exceptions.ExistingStockException;
import gbce.stocks.exceptions.NoTradeVolumeException;
import gbce.stocks.exceptions.UnknownStockException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface Exchange {
	void addStock(Stock newStock) throws ExistingStockException;
	Stock getStock(String symbol) throws UnknownStockException;

	boolean bookTrade(String symbol, LocalDateTime timestamp, long quantity, BuyOrSell buyOrSell, BigDecimal price) throws UnknownStockException;
	boolean bookTrade(String symbol, long quantity, BuyOrSell buyOrSell, BigDecimal price) throws UnknownStockException;

	BigDecimal getIndex(LocalDateTime timestamp) throws NoTradeVolumeException;
	BigDecimal getCurrentIndex() throws NoTradeVolumeException;
}
