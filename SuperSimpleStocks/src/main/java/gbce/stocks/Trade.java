package gbce.stocks;

import java.math.BigDecimal;
import java.time.LocalDateTime;

class Trade {
	private LocalDateTime timestamp;
	private long quantity;
	private BuyOrSell buyOrSell;
	private BigDecimal price;
	
	Trade(LocalDateTime timestamp, long quantity, BuyOrSell buyOrSell, BigDecimal price) {
		this.timestamp = timestamp;
		this.quantity = quantity;
		this.buyOrSell = buyOrSell;
		this.price = price;

		if (!tradeIsValid()) {
			throw new IllegalArgumentException("Trade booked is invalid.");
		}
	}

	LocalDateTime getTimestamp() {
		return timestamp;
	}

	long getQuantity() {
		return quantity;
	}

	BuyOrSell getBuyOrSell() {
		return buyOrSell;
	}

	BigDecimal getPrice() {
		return price;
	}

	private boolean tradeIsValid() {
		return quantity > 0 && price.compareTo(BigDecimal.ZERO) > 0;
	}
}
