package gbce;

import gbce.stocks.exceptions.NoTradeVolumeException;
import org.junit.Before;
import org.junit.Test;
import gbce.stocks.*;

import java.math.BigDecimal;

import static gbce.stocks.BuyOrSell.*;
import static org.junit.Assert.*;

public class CommonStockTest extends AbstractStockTest {

    @Before
    public void setup() {
        stock = new CommonStock(symbol, parValue, lastDividend);
        zeroDivStock = new CommonStock(symbol, parValue, lastDividendOfZero);
    }

    @Test(expected = NoTradeVolumeException.class)
    public void attemptToGetDivYieldWithNoTradesBookedResultsInException() throws Throwable {
        stock.getDividendYield(now);
    }

    @Test
    public void divYieldCalculatedCorrectlyForTwoTradesBooked() throws Throwable{
        stock.bookTrade(tenMinutesAgo, 1000, BUY, BigDecimal.valueOf(99));
        stock.bookTrade(tenMinutesAgo, 1000, SELL, BigDecimal.valueOf(101));
        assertEquals("0.1", stock.getDividendYield(now).toPlainString());
    }
}
