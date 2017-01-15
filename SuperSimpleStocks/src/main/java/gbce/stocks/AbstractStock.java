package gbce.stocks;

import gbce.stocks.exceptions.NoTradeVolumeException;
import gbce.stocks.exceptions.NotApplicablePeRatio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractStock implements Stock {
	private String symbol;
	private BigDecimal parValue;
	private BigDecimal lastDividend;

	private List<Trade> trades = new LinkedList<>();
	
	AbstractStock(String symbol, BigDecimal parValue, BigDecimal lastDividend) {
		this.symbol = symbol;
		this.parValue = parValue;
		this.lastDividend = lastDividend;
	}

	@Override
	public String getSymbol() {
		return symbol;
	}

	@Override
	public BigDecimal getParValue() {
		return parValue;
	}

	@Override
	public BigDecimal getLastDividend() {
		return lastDividend;
	}

	@Override
	public abstract BigDecimal getDividendYield(LocalDateTime timestamp) throws NoTradeVolumeException;

	@Override
	public BigDecimal getPriceEarningsRatio(LocalDateTime timestamp) throws NoTradeVolumeException, NotApplicablePeRatio {
		if (getLastDividend().equals(BigDecimal.ZERO)) {
			throw new NotApplicablePeRatio(symbol);
		}

		return getStockPrice(timestamp)
				.divide(getLastDividend(), DECIMAL_PLACE_PRECISION, BigDecimal.ROUND_HALF_UP)
				.stripTrailingZeros();
	}

	@Override
	public BigDecimal getStockPrice(LocalDateTime timestamp) throws NoTradeVolumeException {
		long volume = 0;
		BigDecimal volumeWeightedPrice = BigDecimal.ZERO;
		for (Trade trade : trades) {
			if (tradeIsInWindowForPricing(trade, timestamp)) {
				volume += trade.getQuantity();
				volumeWeightedPrice = volumeWeightedPrice.add((trade.getPrice().multiply(BigDecimal.valueOf(trade.getQuantity()))));
			}
		}

		if (volume == 0) {
			throw new NoTradeVolumeException("No Trades booked. Symbol : " + symbol);
		}

		return volumeWeightedPrice
				.divide(BigDecimal.valueOf(volume), DECIMAL_PLACE_PRECISION, BigDecimal.ROUND_HALF_UP)
				.stripTrailingZeros();
	}

	@Override
	public BigDecimal getCurrentDividendYield() throws NoTradeVolumeException {
		return this.getDividendYield(LocalDateTime.now());
	}

	@Override
	public BigDecimal getCurrentPriceEarningsRatio() throws NoTradeVolumeException, NotApplicablePeRatio {
		return this.getPriceEarningsRatio(LocalDateTime.now());
	}

	@Override
	public BigDecimal getCurrentStockPrice() throws NoTradeVolumeException {
		return this.getStockPrice(LocalDateTime.now());
	}

	@Override
	public boolean bookTrade(LocalDateTime timestamp, long quantity, BuyOrSell buyOrSell, BigDecimal price) {
		try {
			return trades.add(new Trade(timestamp, quantity, buyOrSell, price));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage() + " Symbol : " + symbol);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AbstractStock))
			return false;
		AbstractStock other = (AbstractStock) obj;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}

	private boolean tradeIsInWindowForPricing(Trade trade, LocalDateTime timestamp) {
		LocalDateTime windowStart = timestamp.minusMinutes(MAX_TRADE_AGE_FOR_PRICE_IN_MINS);
		LocalDateTime windowEnd = timestamp;
		LocalDateTime tradeTime = trade.getTimestamp();

		return (windowStart.isBefore(tradeTime) || windowStart.isEqual(tradeTime)) &&
				(tradeTime.isBefore(windowEnd) || tradeTime.isEqual(windowEnd));
	}
}
