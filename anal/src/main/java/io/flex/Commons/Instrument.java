package io.flex.Commons;

import com.studerw.tda.model.option.Option;
import com.studerw.tda.model.quote.Quote;

import java.math.BigDecimal;

public class Instrument {

    public String symbol;
    public Quote quote;
    public OptionChain optionChain;
    public BigDecimal strike = new BigDecimal(300.0);
    public BigDecimal optionMark = new BigDecimal(3.0);
    public Option.PutCall putcall = Option.PutCall.PUT;


    public Instrument(
            Quote quote,
            OptionChain optionChain
    ) {
        this.symbol = quote.getSymbol();
        this.quote = quote;
        this.optionChain = optionChain;
    }

    public Instrument() {
        String fdsa = "creating a mock instrument.";
        System.out.println(fdsa);
    }

    public BigDecimal getStrikePrice() {
        return strike;
    }

    public BigDecimal getMarkPrice() {
        return optionMark;
    }

    public Option.PutCall getPutCall() {
        return putcall;
    }

}
