package gbce.stocks;

import gbce.stocks.exceptions.NoTradeVolumeException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CommonStock extends AbstractStock {

	public CommonStock(String symbol, BigDecimal parValue, BigDecimal lastDividend) {
		super(symbol, parValue, lastDividend);
	}

	@Override
	public BigDecimal getDividendYield(LocalDateTime timestamp) throws NoTradeVolumeException {
		return getLastDividend()
				.divide(getStockPrice(timestamp), DECIMAL_PLACE_PRECISION, BigDecimal.ROUND_HALF_UP)
				.stripTrailingZeros();
	}
}
