package io.flex.commons;

import com.studerw.tda.model.option.Option;

public class Position {
    public Option option;
    public Instrument instrument;
    public long quantity;

    public Position(Option option, long quantity) {
        this.option = option;
        this.quantity = quantity;
    }

    public Position(Instrument instrument, long quantity) {
        this.instrument = instrument;
        this.quantity = quantity;
    }
}
