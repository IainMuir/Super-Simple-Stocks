package gbce;

import gbce.stocks.BuyOrSell;
import gbce.stocks.Stock;
import gbce.stocks.exceptions.ExistingStockException;
import gbce.stocks.exceptions.NoTradeVolumeException;
import gbce.stocks.exceptions.UnknownStockException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class ExchangeInstance implements Exchange {

	private Set<Stock> stocks = new HashSet<>();

	@Override
	public void addStock(Stock newStock) throws ExistingStockException {
		if (!stocks.add(newStock)){
			throw new ExistingStockException(newStock.getSymbol());
		}
	}

    @Override
	public Stock getStock(String symbol) throws UnknownStockException {
		for (Stock stock : stocks) {
			if (stock.getSymbol().equals(symbol)) {
				return stock;
			}
		}
		throw new UnknownStockException(symbol);
	}

    @Override
	public boolean bookTrade(String symbol, LocalDateTime timestamp, long quantity, BuyOrSell buyOrSell, BigDecimal price) throws UnknownStockException {
		for (Stock stock : stocks) {
			if (stock.getSymbol().equals(symbol)) {
				return stock.bookTrade(timestamp, quantity, buyOrSell, price);
			}
		}
		throw new UnknownStockException(symbol);
	}

    @Override
	public boolean bookTrade(String symbol, long quantity, BuyOrSell buyOrSell, BigDecimal price) throws UnknownStockException {
		return bookTrade(symbol, LocalDateTime.now(), quantity, buyOrSell, price);
	}

    @Override
	public BigDecimal getIndex(LocalDateTime timestamp) throws NoTradeVolumeException {
		BigDecimal stockPriceProd = BigDecimal.ONE;
		int stockPriceCount = 0;
		for (Stock stock : stocks) {
			try {
				stockPriceProd = stockPriceProd.multiply(stock.getStockPrice(timestamp));
				stockPriceCount++;
			} catch (NoTradeVolumeException e) {
				System.out.println("No trades booked for " + stock.getSymbol() +
						" so no price available. Ignoring from index calculation.");
			}
		}

		if (stockPriceCount == 0) {
			throw new NoTradeVolumeException("Unable to calculate index as no trade volume on any stocks on the exchange.");
		}

		return BigDecimal.valueOf(Math.pow(stockPriceProd.doubleValue(), 1.0/stockPriceCount))
				.setScale(Stock.DECIMAL_PLACE_PRECISION, BigDecimal.ROUND_HALF_UP)
				.stripTrailingZeros();
	}

    @Override
	public BigDecimal getCurrentIndex() throws NoTradeVolumeException {
		return getIndex(LocalDateTime.now());
	}
}
