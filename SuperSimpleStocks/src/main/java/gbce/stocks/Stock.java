package gbce.stocks;

import gbce.stocks.exceptions.NoTradeVolumeException;
import gbce.stocks.exceptions.NotApplicablePeRatio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface Stock {
    int MAX_TRADE_AGE_FOR_PRICE_IN_MINS = 15;
    int DECIMAL_PLACE_PRECISION = 5;

    String getSymbol();
    BigDecimal getParValue();
    BigDecimal getLastDividend();

    BigDecimal getDividendYield(LocalDateTime timestamp) throws NoTradeVolumeException;
    BigDecimal getPriceEarningsRatio(LocalDateTime timestamp) throws NoTradeVolumeException, NotApplicablePeRatio;
    BigDecimal getStockPrice(LocalDateTime timestamp) throws NoTradeVolumeException;

    BigDecimal getCurrentDividendYield() throws NoTradeVolumeException;
    BigDecimal getCurrentPriceEarningsRatio() throws NoTradeVolumeException, NotApplicablePeRatio;
    BigDecimal getCurrentStockPrice() throws NoTradeVolumeException;

    boolean bookTrade(LocalDateTime timestamp, long quantity, BuyOrSell buyOrSell, BigDecimal price);
}
