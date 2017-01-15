package gbce;

import gbce.stocks.exceptions.NoTradeVolumeException;
import gbce.stocks.exceptions.NotApplicablePeRatio;
import org.junit.Test;
import gbce.stocks.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static gbce.stocks.BuyOrSell.*;
import static org.junit.Assert.*;

public abstract class AbstractStockTest {
    protected final static String symbol = "ABC";
    protected final static BigDecimal parValue = BigDecimal.valueOf(100);
    protected final static BigDecimal lastDividend = BigDecimal.valueOf(10);
    protected final static BigDecimal lastDividendOfZero = BigDecimal.valueOf(0);

    protected final static LocalDateTime now = LocalDateTime.now();
    protected final static LocalDateTime tenMinutesAgo = now.minusMinutes(10);
    protected final static LocalDateTime fifteenMinutesAgo = now.minusMinutes(15);
    protected final static LocalDateTime fifteenMinutesAndOneSecondAgo = now.minusMinutes(15).minusSeconds(1);

    protected Stock stock;
    protected Stock zeroDivStock;

    @Test
    public void commonStockSetupCorrectly() {
        assertEquals(symbol, stock.getSymbol());
        assertEquals(parValue, stock.getParValue());
        assertEquals(lastDividend, stock.getLastDividend());
    }

    @Test(expected = NoTradeVolumeException.class)
    public void attemptToGetPriceWithNoTradesBookedResultsInException() throws Throwable{
        stock.getStockPrice(now);
    }

    @Test
    public void priceCalculatedCorrectlyForTwoTradesBooked() throws Throwable{
        stock.bookTrade(tenMinutesAgo, 1000, BUY, BigDecimal.valueOf(99));
        stock.bookTrade(tenMinutesAgo, 1000, SELL, BigDecimal.valueOf(101));
        assertEquals("100", stock.getStockPrice(now).toPlainString());
    }

    @Test
    public void priceCalculatedCorrectlyWhenTradeBeforePriceWindow() throws Throwable {
        stock = new CommonStock(symbol, parValue, lastDividend);
        stock.bookTrade(now, 1000, BUY, BigDecimal.valueOf(10));
        stock.bookTrade(tenMinutesAgo, 1000, BUY, BigDecimal.valueOf(20));
        stock.bookTrade(fifteenMinutesAgo, 2000, SELL, BigDecimal.valueOf(50));
        stock.bookTrade(fifteenMinutesAndOneSecondAgo, 1000, SELL, BigDecimal.valueOf(90));
        assertEquals("32.5", stock.getStockPrice(now).toPlainString());
    }

    @Test
    public void historicPriceCalculatedCorrectly() throws Throwable {
        stock.bookTrade(fifteenMinutesAndOneSecondAgo, 1000, BUY, BigDecimal.valueOf(99));
        stock.bookTrade(fifteenMinutesAgo, 1000, SELL, BigDecimal.valueOf(101));
        stock.bookTrade(now, 1000, SELL, BigDecimal.valueOf(90));
        assertEquals("100", stock.getStockPrice(tenMinutesAgo).toPlainString());
    }

    @Test(expected = NoTradeVolumeException.class)
    public void attemptToGetPeRatioWithNoTradesBookedResultsInException() throws Throwable {
        stock.getPriceEarningsRatio(now);
    }

    @Test
    public void peRatioCalculatedCorrectlyForTwoTradesBooked() throws Throwable{
        stock.bookTrade(tenMinutesAgo, 1000, BUY, BigDecimal.valueOf(99));
        stock.bookTrade(tenMinutesAgo, 1000, SELL, BigDecimal.valueOf(101));
        assertEquals("10", stock.getPriceEarningsRatio(now).toPlainString());
    }

    @Test(expected = NotApplicablePeRatio.class)
    public void peRatioCalculatedOnZeroDivStockResultsInException() throws Throwable{
        zeroDivStock.bookTrade(tenMinutesAgo, 1000, BUY, BigDecimal.valueOf(99));
        zeroDivStock.bookTrade(tenMinutesAgo, 1000, SELL, BigDecimal.valueOf(101));
        assertEquals("10", zeroDivStock.getPriceEarningsRatio(now).toPlainString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void bookingTradeWithZeroPriceResultsInException() throws Throwable {
        stock.bookTrade(now, 1000, BUY, BigDecimal.valueOf(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void bookingTradeWithNegativePriceResultsInException() throws Throwable {
        stock.bookTrade(now, 1000, BUY, BigDecimal.valueOf(-100));
    }

    @Test(expected = IllegalArgumentException.class)
    public void bookingTradeWithZeroQuantityResultsInException() throws Throwable {
        stock.bookTrade(now, 0, BUY, BigDecimal.valueOf(100));
    }

    @Test(expected = IllegalArgumentException.class)
    public void bookingTradeWithNegativeQuantityResultsInException() throws Throwable {
        stock.bookTrade(now, -1000, BUY, BigDecimal.valueOf(100));
    }
}
