package gbce;

import gbce.stocks.exceptions.NoTradeVolumeException;
import org.junit.Before;
import org.junit.Test;
import gbce.stocks.*;
import gbce.stocks.exceptions.ExistingStockException;
import gbce.stocks.exceptions.UnknownStockException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static gbce.stocks.BuyOrSell.*;

public class ExchangeTest {

	private final static LocalDateTime now = LocalDateTime.now();
	private final static LocalDateTime fiveMinutesAgo = now.minusMinutes(5);
	private final static LocalDateTime fifteenMinutesAndOneSecondAgo = now.minusMinutes(15).minusSeconds(1);
	private final static LocalDateTime twentyMinutesAndOneSecondAgo = now.minusMinutes(20).minusSeconds(1);

	private Exchange exchange;

	@Before
	public void setUp() throws Throwable {
		exchange = new ExchangeInstance();
		exchange.addStock(new CommonStock("TEA", BigDecimal.valueOf(100), BigDecimal.valueOf(0)));
		exchange.addStock(new CommonStock("POP", BigDecimal.valueOf(100), BigDecimal.valueOf(8)));
		exchange.addStock(new CommonStock("ALE", BigDecimal.valueOf(60), BigDecimal.valueOf(23)));
		exchange.addStock(new PreferredStock("GIN", BigDecimal.valueOf(100), BigDecimal.valueOf(8), BigDecimal.valueOf(2.0)));
		exchange.addStock(new CommonStock("JOE", BigDecimal.valueOf(250), BigDecimal.valueOf(13)));
	}

	@Test
	public void stocksAreInitialisedInExchangeCorrectly() throws Throwable {
		Stock stock = exchange.getStock("TEA");
		assertEquals(stock.getSymbol(), "TEA");
		assertEquals(stock.getParValue(), BigDecimal.valueOf(100));
		
		stock = exchange.getStock("POP");
		assertEquals(stock.getSymbol(), "POP");
		assertEquals(stock.getParValue(), BigDecimal.valueOf(100));
		
		stock = exchange.getStock("ALE");
		assertEquals(stock.getSymbol(), "ALE");
		assertEquals(stock.getParValue(), BigDecimal.valueOf(60));
		
		stock = exchange.getStock("GIN");
		assertEquals(stock.getSymbol(), "GIN");
		assertEquals(stock.getParValue(), BigDecimal.valueOf(100));
		
		stock = exchange.getStock("JOE");
		assertEquals(stock.getSymbol(), "JOE");
		assertEquals(stock.getParValue(), BigDecimal.valueOf(250));
	}
	
	@Test(expected = UnknownStockException.class)
	public void attemptToGetUnknownStockResultsInException() throws Throwable {
		exchange.getStock("H20");
	}
	
	@Test(expected = ExistingStockException.class)
	public void attemptToAddExistingStockResultsInException() throws Throwable {
		exchange.addStock(new CommonStock("TEA", BigDecimal.valueOf(-1), BigDecimal.valueOf(-1)));
	}

	@Test
	public void bookTradesAgainstOnlyOneStockAndCalculateIndex() throws Throwable {
		exchange.bookTrade("TEA", 1000, BUY, BigDecimal.valueOf(102));
		exchange.bookTrade("TEA", 1000, BUY, BigDecimal.valueOf(101));
		exchange.bookTrade("TEA", 1000, BUY, BigDecimal.valueOf(100));
		exchange.bookTrade("TEA", 1000, BUY, BigDecimal.valueOf(99));
		exchange.bookTrade("TEA", 1000, BUY, BigDecimal.valueOf(98));

		assertEquals("100", exchange.getCurrentIndex().toPlainString());
	}

	@Test
	public void bookTradesAndCalculateIndex() throws Throwable {
		exchange.bookTrade("TEA", 1000, BUY, BigDecimal.valueOf(102));
		exchange.bookTrade("TEA", 1000, BUY, BigDecimal.valueOf(101));
		exchange.bookTrade("TEA", 1000, BUY, BigDecimal.valueOf(100));
		exchange.bookTrade("TEA", 1000, BUY, BigDecimal.valueOf(99));
		exchange.bookTrade("TEA", 1000, BUY, BigDecimal.valueOf(98));
		exchange.bookTrade("POP", 1000, BUY, BigDecimal.valueOf(110));
		exchange.bookTrade("ALE", 1000, BUY, BigDecimal.valueOf(150));
		exchange.bookTrade("GIN", 1000, BUY, BigDecimal.valueOf(50));
		exchange.bookTrade("JOE", 1000, BUY, BigDecimal.valueOf(90));

		assertEquals("94.21917", exchange.getCurrentIndex().toPlainString());
	}

	@Test
	public void tradesOlderThanFifteenMinsUsedForCalculatingHistoricIndex() throws Throwable {
		exchange.bookTrade("TEA", fifteenMinutesAndOneSecondAgo, 1000, BUY, BigDecimal.valueOf(102));
		exchange.bookTrade("TEA", fifteenMinutesAndOneSecondAgo, 1000, BUY, BigDecimal.valueOf(101));
		exchange.bookTrade("TEA", fifteenMinutesAndOneSecondAgo, 1000, BUY, BigDecimal.valueOf(100));
		exchange.bookTrade("TEA", fifteenMinutesAndOneSecondAgo, 1000, BUY, BigDecimal.valueOf(99));
		exchange.bookTrade("TEA", fifteenMinutesAndOneSecondAgo, 1000, BUY, BigDecimal.valueOf(98));
		exchange.bookTrade("POP", twentyMinutesAndOneSecondAgo, 1000, BUY, BigDecimal.valueOf(110));
		exchange.bookTrade("ALE", twentyMinutesAndOneSecondAgo, 1000, BUY, BigDecimal.valueOf(150));
		exchange.bookTrade("GIN", now, 1000, BUY, BigDecimal.valueOf(50));
		exchange.bookTrade("JOE", now, 1000, BUY, BigDecimal.valueOf(90));

		assertEquals("100", exchange.getIndex(fiveMinutesAgo).toPlainString());
	}

	@Test
	public void tradesOlderThanFifteenMinsIgnoredForCalculatingIndex() throws Throwable {
		exchange.bookTrade("TEA", now, 1000, BUY, BigDecimal.valueOf(102));
		exchange.bookTrade("TEA", now, 1000, BUY, BigDecimal.valueOf(101));
		exchange.bookTrade("TEA", now, 1000, BUY, BigDecimal.valueOf(100));
		exchange.bookTrade("TEA", now, 1000, BUY, BigDecimal.valueOf(99));
		exchange.bookTrade("TEA", now, 1000, BUY, BigDecimal.valueOf(98));
		exchange.bookTrade("POP", fifteenMinutesAndOneSecondAgo, 1000, BUY, BigDecimal.valueOf(110));
		exchange.bookTrade("ALE", fifteenMinutesAndOneSecondAgo, 1000, BUY, BigDecimal.valueOf(150));
		exchange.bookTrade("GIN", fifteenMinutesAndOneSecondAgo, 1000, BUY, BigDecimal.valueOf(50));
		exchange.bookTrade("JOE", fifteenMinutesAndOneSecondAgo, 1000, BUY, BigDecimal.valueOf(90));

		assertEquals("100", exchange.getCurrentIndex().toPlainString());
	}

	@Test
	public void commonStockFinancialsCanBeAccessedOnExchange() throws Throwable {
		exchange.bookTrade("POP", 1000, BUY, BigDecimal.valueOf(102));
		exchange.bookTrade("POP", 1000, BUY, BigDecimal.valueOf(101));
		exchange.bookTrade("POP", 1000, BUY, BigDecimal.valueOf(100));
		exchange.bookTrade("POP", 1000, BUY, BigDecimal.valueOf(99));
		exchange.bookTrade("POP", 1000, BUY, BigDecimal.valueOf(98));

		Stock stock = exchange.getStock("POP");
		assertEquals("100", stock.getCurrentStockPrice().toPlainString());
		assertEquals("0.08", stock.getCurrentDividendYield().toPlainString());
		assertEquals("12.5", stock.getCurrentPriceEarningsRatio().toPlainString());
	}

	@Test
	public void preferredStockFinancialsCanBeAccessedOnExchange() throws Throwable {
		exchange.bookTrade("GIN", 1000, BUY, BigDecimal.valueOf(102));
		exchange.bookTrade("GIN", 1000, BUY, BigDecimal.valueOf(101));
		exchange.bookTrade("GIN", 1000, BUY, BigDecimal.valueOf(100));
		exchange.bookTrade("GIN", 1000, BUY, BigDecimal.valueOf(99));
		exchange.bookTrade("GIN", 1000, BUY, BigDecimal.valueOf(98));

		Stock stock = exchange.getStock("GIN");
		assertEquals("100", stock.getCurrentStockPrice().toPlainString());
		assertEquals("2", stock.getCurrentDividendYield().toPlainString());
		assertEquals("12.5", stock.getCurrentPriceEarningsRatio().toPlainString());
	}

	@Test(expected = NoTradeVolumeException.class)
	public void gettingIndexWithNoTradesBookedResultsInException() throws Throwable {
		exchange.getIndex(now);
	}
}
