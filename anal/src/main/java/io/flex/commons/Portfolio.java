package io.flex.commons;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Portfolio extends HashMap<String, ArrayList<Position>> {

    public String firstSymbol = null;
    public Instrument firstInstrument = null;

    @Override
    public ArrayList<Position> put(String s, ArrayList<Position> positions) {
        if (firstSymbol == null) {
            firstSymbol = s;
        }
        if (firstInstrument == null) {
            firstInstrument = positions.get(0).instrument;
        }
        return super.put(s, positions);
    }
}
