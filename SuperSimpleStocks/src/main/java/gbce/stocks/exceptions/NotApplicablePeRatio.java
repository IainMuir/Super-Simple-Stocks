package gbce.stocks.exceptions;

public class NotApplicablePeRatio extends Exception {
    public NotApplicablePeRatio(String symbol){
        super("Price to Earning Ratio not applicable as last dividend was zero. Symbol : " + symbol);
    }
}
