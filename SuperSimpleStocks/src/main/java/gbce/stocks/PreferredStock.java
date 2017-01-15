package gbce.stocks;

import gbce.stocks.exceptions.NoTradeVolumeException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PreferredStock extends AbstractStock {

	private BigDecimal fixedDividend;

	public PreferredStock(String symbol, BigDecimal parValue, BigDecimal lastDividend, BigDecimal fixedDividend) {
		super(symbol, parValue, lastDividend);
		this.fixedDividend = fixedDividend;
	}

	public BigDecimal getFixedDividend() {
		return fixedDividend;
	}

	@Override
	public BigDecimal getDividendYield(LocalDateTime timestamp) throws NoTradeVolumeException {
		return getFixedDividend().multiply(getParValue())
				.divide(getStockPrice(timestamp), DECIMAL_PLACE_PRECISION, BigDecimal.ROUND_HALF_UP)
				.stripTrailingZeros();
	}
}
