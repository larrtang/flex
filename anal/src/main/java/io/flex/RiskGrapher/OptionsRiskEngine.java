package io.flex.RiskGrapher;

import io.flex.Commons.Instrument;
import io.flex.Commons.Position;
import com.studerw.tda.model.option.Option;
import com.studerw.tda.model.quote.EquityQuote;
import com.studerw.tda.model.quote.EtfQuote;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class OptionsRiskEngine {

    private double granularity;

    private Instrument option;

    private double evalStartPrice;
    private double evalEndPrice;
    private final double EVAL_PRICE_THRESH_PERCENT = 0.10;

    public  ArrayList<Position> positions = new ArrayList<>();

    public OptionsRiskEngine(
            Instrument option,
            double granularity
    ) {
        this.option = option;
        this.granularity = granularity;

        // calculate range, to calculate risk graph
        if (option.quote instanceof EquityQuote) {
            EquityQuote equote = (EquityQuote) option.quote;
            this.evalStartPrice = Math.round(equote.getMark().doubleValue() * (1 - EVAL_PRICE_THRESH_PERCENT));
            this.evalEndPrice = Math.round(equote.getMark().doubleValue() * (1 + EVAL_PRICE_THRESH_PERCENT));
        }
        else if (option.quote instanceof EtfQuote) {
            EtfQuote equote = (EtfQuote) option.quote;
            this.evalStartPrice = Math.round(equote.getMark().doubleValue() * (1 - EVAL_PRICE_THRESH_PERCENT));
            this.evalEndPrice = Math.round(equote.getMark().doubleValue() * (1 + EVAL_PRICE_THRESH_PERCENT));
        }
    }

    public Map<Double, Double> getRiskGraphToday() {
        Map<Double, Double> riskMap = new HashMap<>();



        return riskMap;
    }

    public Map<Timestamp, Map<Double, Double>> getAllExpirationRiskGraphs() {
        Map<Timestamp, Map<Double, Double>> riskMap = new HashMap<>();



        return riskMap;
    }


    public Map<Double, Double> getRiskGraphExpiration(ArrayList<Position> legs) {
        Map<Double, Double> riskMap = new HashMap<>();

        for (double i = this.evalStartPrice; i <= this.evalEndPrice; i += this.granularity) {
            for (Position leg : legs) {
                if (riskMap.containsKey(i))
                    riskMap.put(i, riskMap.get(i) + getExpirationValue(leg.instrument, i)*leg.quantity);
                else
                    riskMap.put(i, getExpirationValue(leg.instrument, i)*leg.quantity);
            }
        }

        return riskMap;
    }

    public Map<Double, Double> getRiskGraphExpiration(Instrument option) {
        Map<Double, Double> riskMap = new HashMap<>();

        for (double i = this.evalStartPrice; i <= this.evalEndPrice; i += this.granularity) {
            riskMap.put(i, getExpirationValue(option, i));
        }

        return riskMap;
    }

    public Map<Double, Double> getRiskGraphExpiration(Option option) {
        Map<Double, Double> riskMap = new HashMap<>();

        for (double i = this.evalStartPrice; i <= this.evalEndPrice; i += this.granularity) {
            riskMap.put(i, getExpirationValue(option, i));
        }

        return riskMap;
    }

    private double getExpirationValue(Instrument option, double underlyingMark) {
        double strike = option.getStrikePrice().doubleValue();
        double extrinsic = option.getMarkPrice().doubleValue()*100;
        if (option.getPutCall() == Option.PutCall.CALL) {
            double val = -extrinsic + (underlyingMark - strike)*100;
            return Math.max(val, -extrinsic);
        }
        else if (option.getPutCall() == Option.PutCall.PUT) {
            double val = -extrinsic - (underlyingMark - strike)*100;
            return Math.max(val, -extrinsic);
        }

        return Double.MIN_VALUE;
    }

    private double getExpirationValue(Option option, double underlyingMark) {
        double strike = option.getStrikePrice().doubleValue();
        double extrinsic = option.getMarkPrice().doubleValue()*100;
        if (option.getPutCall() == Option.PutCall.CALL) {
            double val = -extrinsic + (underlyingMark - strike)*100;
            return Math.max(val, -extrinsic);
        }
        else if (option.getPutCall() == Option.PutCall.PUT) {
            double val = -extrinsic - (underlyingMark - strike)*100;
            return Math.max(val, -extrinsic);
        }

        return Double.MIN_VALUE;
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }
}
