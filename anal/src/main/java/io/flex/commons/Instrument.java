package io.flex.commons;

import com.studerw.tda.model.option.Option;
import com.studerw.tda.model.option.Option.PutCall;

import com.studerw.tda.model.quote.EquityQuote;
import com.studerw.tda.model.quote.EtfQuote;
import com.studerw.tda.model.quote.Quote;
import io.flex.tda.TDAClient;
import org.jquantlib.time.Date;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class Instrument {

    public String       symbol;
    public Quote        underlyingQuote;
    public OptionChain  optionChain;
    public BigDecimal   strike = new BigDecimal("300.0");
    public BigDecimal   optionMark = new BigDecimal("3.0");
    public String       expirationString = "";
    public PutCall      putcall = Option.PutCall.PUT;
    public double       iv = 0.2;
    public Option       option;
    public long         expirationLong;
    public double       underlyingMark = -1;

    public static final double VOL_PERCENTAGE = 0.01;

    public static Instrument createOptionInstrument(
            TDAClient client,
            String sym,
            double strike,
            String expString,
            Option.PutCall pc
    ) {
        Instrument instrument = new Instrument();
        instrument.symbol = sym;

        DecimalFormat df = new DecimalFormat(".0");
        System.out.println(df.format(strike));
        instrument.strike = new BigDecimal(df.format(strike));
        instrument.expirationString = expString;
        instrument.putcall = pc;

        instrument.underlyingQuote = client.getClient().fetchQuote(sym);

        // get actual option?
        if (pc == PutCall.CALL) {

        } else {

        }


        return instrument;
    }


    public Instrument(
            Quote underlyingQuote,
            Option option
    ) {
        this.symbol = underlyingQuote.getSymbol();
        this.underlyingQuote = underlyingQuote;
        this.option = option;

        this.strike = option.getStrikePrice();
        this.expirationLong = option.getExpirationDate();
        this.putcall = option.getPutCall();
        this.iv = option.getVolatility().doubleValue() * VOL_PERCENTAGE;
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

    public Date getExpirationDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd:ss");
        java.util.Date date = null;
        try {
            date = df.parse(expirationString);
        } catch (Exception e) {
            System.err.printf("Date: %s is not parsable", this.expirationString);
            return null;
        }

        return new Date(date);
    }

    public boolean syncWithOption() {
        try {
            this.optionMark = option.getMarkPrice();
            this.iv = option.getVolatility().doubleValue() * VOL_PERCENTAGE;
            this.expirationLong = option.getExpirationDate();

            if (underlyingQuote instanceof EquityQuote) {
                EquityQuote equote = (EquityQuote) underlyingQuote;
                this.underlyingMark = equote.getMark().doubleValue();
            }
            else if (underlyingQuote instanceof EtfQuote) {
                EtfQuote equote = (EtfQuote) underlyingQuote;
                this.underlyingMark = equote.getMark().doubleValue();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Instrument{" +
                "symbol='" + symbol + '\'' +
                ", optionChain=" + optionChain +
                ", strike=" + strike +
                ", optionMark=" + optionMark +
                ", expirationString='" + expirationString + '\'' +
                ", putcall=" + putcall +
                ", iv=" + iv +
                ", option=" + option +
                ", expirationLong=" + expirationLong +
                ", underlyingMark=" + underlyingMark +
                '}';
    }
}
