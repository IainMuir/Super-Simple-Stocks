package gbce;

import gbce.stocks.PreferredStock;
import gbce.stocks.exceptions.NoTradeVolumeException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static gbce.stocks.BuyOrSell.BUY;
import static gbce.stocks.BuyOrSell.SELL;
import static org.junit.Assert.assertEquals;

public class PreferredStockTest extends AbstractStockTest {

    private final static BigDecimal fixedDividend = BigDecimal.valueOf(2.0);

    @Before
    public void setup() {
        stock = new PreferredStock(symbol, parValue, lastDividend, fixedDividend);
        zeroDivStock = new PreferredStock(symbol, parValue, lastDividendOfZero, fixedDividend);
    }

    @Test(expected = NoTradeVolumeException.class)
    public void attemptToGetDivYieldWithNoTradesBookedResultsInException() throws Throwable {
        stock.getDividendYield(now);
    }

    @Test
    public void divYieldCalculatedCorrectlyForTwoTradesBooked() throws Throwable{
        stock.bookTrade(tenMinutesAgo, 1000, BUY, BigDecimal.valueOf(99));
        stock.bookTrade(tenMinutesAgo, 1000, SELL, BigDecimal.valueOf(101));
        assertEquals("2", stock.getDividendYield(now).toPlainString());
    }
}
