package io.flex.business;

import com.studerw.tda.model.option.Option;
import com.studerw.tda.model.quote.Quote;
import io.flex.commons.Instrument;
import io.flex.commons.OptionChain;
import io.flex.commons.Portfolio;
import io.flex.commons.Position;
import io.flex.tda.TDAClient;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MarketDataEngine extends Thread {

    private final TDAClient client;

    public Portfolio portfolio = new Portfolio();

    public Consumer<Portfolio> callback;

    private static final long REFRESH_INTERVAL = 5000;

    public String current_symbol;

    public MarketDataEngine(TDAClient client, Consumer<Portfolio> callback) {
        this.client = client;
        this.callback = callback;
        OptionChain optionChain = this.client.getOptionChain("SPY");
        optionChain.printExpirations();
    }

    public MarketDataEngine(
            TDAClient client,
            Portfolio portfolio,
            Consumer<Portfolio> callback
    ) {
        this(client, callback);
        this.portfolio = portfolio;
        this.callback = callback;


    }


    @Override
    public void run() {
        try {
            current_symbol = portfolio.firstSymbol;

            while (this.isAlive()) {

                //update quotes and positions
                String symbol = current_symbol;
                ArrayList<Position> positions = this.portfolio.get(symbol);

                Quote quote = this.client.getClient().fetchQuote(symbol);
                OptionChain optionChain = this.client.getOptionChain(symbol);

                for (Position position : positions) {
                    Instrument instrument = position.instrument;
                    try {
                        instrument.underlyingQuote = quote;
                        if (instrument.putcall == Option.PutCall.CALL) {
                            instrument.option = optionChain.callExpDateMap.get(instrument.expirationString).get(instrument.strike).get(0);
                        } else {
                            instrument.option = optionChain.putExpDateMap.get(instrument.expirationString).get(instrument.strike).get(0);
                        }
                    } catch (NullPointerException e) {
                        System.err.printf("No option found for instrument: %s", instrument.toString());
                    }
                    instrument.syncWithOption();
                }


                callback.accept(this.portfolio);
                Thread.sleep(REFRESH_INTERVAL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
