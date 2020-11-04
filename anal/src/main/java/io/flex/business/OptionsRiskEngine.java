package io.flex.business;

import io.flex.commons.Instrument;
import io.flex.commons.Portfolio;
import io.flex.commons.Position;
import com.studerw.tda.model.option.Option;
import com.studerw.tda.model.quote.EquityQuote;
import com.studerw.tda.model.quote.EtfQuote;
import org.jfree.data.general.Dataset;
import org.jquantlib.Settings;
import org.jquantlib.daycounters.Actual365Fixed;
import org.jquantlib.daycounters.DayCounter;
import org.jquantlib.exercise.AmericanExercise;
import org.jquantlib.exercise.BermudanExercise;
import org.jquantlib.exercise.EuropeanExercise;
import org.jquantlib.exercise.Exercise;
import org.jquantlib.instruments.EuropeanOption;
import org.jquantlib.instruments.Payoff;
import org.jquantlib.instruments.PlainVanillaPayoff;
import org.jquantlib.instruments.VanillaOption;
import org.jquantlib.math.Ops;
import org.jquantlib.methods.lattices.*;
import org.jquantlib.pricingengines.AnalyticEuropeanEngine;
import org.jquantlib.pricingengines.vanilla.*;
import org.jquantlib.pricingengines.vanilla.finitedifferences.FDAmericanEngine;
import org.jquantlib.pricingengines.vanilla.finitedifferences.FDBermudanEngine;
import org.jquantlib.pricingengines.vanilla.finitedifferences.FDEuropeanEngine;
import org.jquantlib.processes.BlackScholesMertonProcess;
import org.jquantlib.quotes.Handle;
import org.jquantlib.quotes.Quote;
import org.jquantlib.quotes.SimpleQuote;
import org.jquantlib.termstructures.BlackVolTermStructure;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.termstructures.volatilities.BlackConstantVol;
import org.jquantlib.termstructures.yieldcurves.FlatForward;
import org.jquantlib.time.*;
import org.jquantlib.time.calendars.Target;

import javax.sound.sampled.Port;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Class used to calculate theoretical and actual options prices and greeks.
 *
 */
public class OptionsRiskEngine {

    private double granularity;

    private Instrument option;

    private double evalStartPrice;
    private double evalEndPrice;
    private final double EVAL_PRICE_THRESH_PERCENT = 0.08;

    public  ArrayList<Position> positions = new ArrayList<>();

    public int eval_days_from_now = 0;
    public double vol_offset = 0;

    public OptionsRiskEngine(
            Instrument option,
            double granularity
    ) {
        this.option = option;
        this.granularity = granularity;

        // calculate range, to calculate risk graph
        if (option.underlyingQuote instanceof EquityQuote) {
            EquityQuote equote = (EquityQuote) option.underlyingQuote;
            this.evalStartPrice = Math.round(equote.getMark().doubleValue() * (1 - EVAL_PRICE_THRESH_PERCENT));
            this.evalEndPrice = Math.round(equote.getMark().doubleValue() * (1 + EVAL_PRICE_THRESH_PERCENT));
        }
        else if (option.underlyingQuote instanceof EtfQuote) {
            EtfQuote equote = (EtfQuote) option.underlyingQuote;
            this.evalStartPrice = Math.round(equote.getMark().doubleValue() * (1 - EVAL_PRICE_THRESH_PERCENT));
            this.evalEndPrice = Math.round(equote.getMark().doubleValue() * (1 + EVAL_PRICE_THRESH_PERCENT));
        }
        // TODO: add support for index quotes
    }
    public OptionsRiskEngine(
            Portfolio portfolio,
            double granularity
    ) {
        // only supporting first instrument now
        this(portfolio.firstInstrument, granularity);
    }

    public Map<Double, Payload> getRiskGraphToday() {
        Map<Double, Payload> riskMap = new HashMap<>();

        for (double i = this.evalStartPrice; i <= this.evalEndPrice; i += this.granularity) {
            for (Position leg : this.positions) {
                Payload payload = getTheoreticalValue(leg.instrument, i);
                if (payload != null) {
                    payload.theoPrice *= leg.quantity;
                    payload.delta *= leg.quantity;
                    payload.gamma *= leg.quantity;
                    payload.theta *= leg.quantity;
                    payload.vega  *= leg.quantity;


                    if (riskMap.containsKey(i)) {
                        Payload mapPayload = riskMap.get(i);
                        mapPayload.theoPrice += payload.theoPrice;
                        if (!Double.isNaN(payload.getDelta())
                                && !Double.isNaN(payload.getTheta())
                                && !Double.isNaN(payload.getVega())
                                && !Double.isNaN(payload.getGamma())) {
                            mapPayload.delta += payload.delta;
                            mapPayload.gamma += payload.gamma;
                            mapPayload.theta += payload.theta;
                            mapPayload.vega += payload.vega;
                        }

                        riskMap.put(i, mapPayload);
                    } else {
                        riskMap.put(i, payload);

                    }
                }
            }
        }
//        riskMap.forEach((k,v) -> {
//            System.out.println(k +" " + v);
//        });
        return riskMap;
    }


    public Map<Double, Payload> getRiskGraphAfterEvalDate(int evalDaysFromNow) {
        Map<Double, Payload> riskMap = new HashMap<>();

        for (double i = this.evalStartPrice; i <= this.evalEndPrice; i += this.granularity) {
            for (Position leg : this.positions) {
                Payload payload = getTheoreticalValue(leg.instrument, i, evalDaysFromNow);
                if (payload != null) {
                    payload.theoPrice *= leg.quantity;
                    payload.delta *= leg.quantity;
                    payload.gamma *= leg.quantity;
                    payload.theta *= leg.quantity;
                    payload.vega  *= leg.quantity;


                    if (riskMap.containsKey(i)) {
                        Payload mapPayload = riskMap.get(i);
                        mapPayload.theoPrice += payload.theoPrice;
                        if (!Double.isNaN(payload.getDelta())
                                && !Double.isNaN(payload.getTheta())
                                && !Double.isNaN(payload.getVega())
                                && !Double.isNaN(payload.getGamma())) {
                            mapPayload.delta += payload.delta;
                            mapPayload.gamma += payload.gamma;
                            mapPayload.theta += payload.theta;
                            mapPayload.vega += payload.vega;
                        }

                        riskMap.put(i, mapPayload);
                    } else {
                        riskMap.put(i, payload);

                    }
                }
            }
        }
//        riskMap.forEach((k,v) -> {
//            System.out.println(k +" " + v);
//        });
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

    private Payload getTheoreticalValue(Instrument option, double underlyingMark) {
        return this.getTheoreticalValue(option,underlyingMark, this.eval_days_from_now);
    }

    private Payload getTheoreticalValue(Instrument option, double underlyingMark, int evalDaysFromNow) {
        //double val = 0;
        Payload payload = new Payload();

        // I have no idea what the fuck we should use


        // set up dates
        final Calendar calendar = new Target();
        final Date todaysDate = Date.todaysDate();
        final Date settlementDate = todaysDate.add(evalDaysFromNow);  //days from the current day, projected pnl
        new Settings().setEvaluationDate(todaysDate);

        final org.jquantlib.instruments.Option.Type type;
        // our options
        if (option.putcall == Option.PutCall.CALL) {
            type = org.jquantlib.instruments.Option.Type.Call;
        } else {
            type = org.jquantlib.instruments.Option.Type.Put;
        }
        final double strike = option.strike.doubleValue();
        final double underlying = underlyingMark;
        /*@Rate*/final double riskFreeRate = 0.01;
        final double volatility = option.iv + vol_offset;
        final double dividendYield = 0.00;


        final Date maturity = option.getExpirationDate();
        final DayCounter dayCounter = new Actual365Fixed();

        final Exercise europeanExercise = new EuropeanExercise(maturity);

        // Define exercise for Bermudan Options
        final int bermudanForwards = 4;
        final Date[] exerciseDates = new Date[bermudanForwards];
        for (int i = 1; i <= bermudanForwards; i++) {
            exerciseDates[i-1] = settlementDate.add(new Period(3*i, TimeUnit.Months));
        }
        final Exercise bermudanExercise = new BermudanExercise(exerciseDates);

        // Define exercise for American Options
        final Exercise americanExercise = new AmericanExercise(settlementDate, maturity);

        // bootstrap the yield/dividend/volatility curves
        final Handle<Quote> underlyingH = new Handle<Quote>(new SimpleQuote(underlying));
        final Handle<YieldTermStructure> flatDividendTS = new Handle<YieldTermStructure>(new FlatForward(settlementDate, dividendYield, dayCounter));
        final Handle<YieldTermStructure> flatTermStructure = new Handle<YieldTermStructure>(new FlatForward(settlementDate, riskFreeRate, dayCounter));
        final Handle<BlackVolTermStructure> flatVolTS = new Handle<BlackVolTermStructure>(new BlackConstantVol(settlementDate, calendar, volatility, dayCounter));
        final Payoff payoff = new PlainVanillaPayoff(type, strike);

        final BlackScholesMertonProcess bsmProcess = new BlackScholesMertonProcess(underlyingH, flatDividendTS, flatTermStructure, flatVolTS);

        // European Options
        final VanillaOption europeanOption = new EuropeanOption(payoff, europeanExercise);

        // Bermudan options (can be thought as a collection of European Options)
        final VanillaOption bermudanOption = new VanillaOption(payoff, bermudanExercise);

        // American Options
        final VanillaOption americanOption = new VanillaOption(payoff, americanExercise);


        // Analytic formulas:

        // Black-Scholes for European
        String method = "Black-Scholes";
        europeanOption.setPricingEngine(new AnalyticEuropeanEngine(bsmProcess));

        // Barone-Adesi and Whaley approximation for American
        method = "Barone-Adesi/Whaley";
        americanOption.setPricingEngine(new BaroneAdesiWhaleyApproximationEngine(bsmProcess));
//        payload.theoPrice = (americanOption.NPV()-option.optionMark.doubleValue())*100;
//        payload.delta = americanOption.delta();
//        payload.gamma = americanOption.gamma();
//        payload.theta = americanOption.theta();
//        payload.vega = americanOption.vega();
     //   System.out.println(option.strike + "\t" + underlyingMark + "\t" + payload);
      //  System.out.println(americanOption.delta());
////        if (Double.isNaN(payload.delta) || Double.isNaN(payload.gamma) || Double.isNaN(payload.theta)) {
////            return null;
////        }
//
        // Bjerksund and Stensland approximation for American
        method = "Bjerksund/Stensland";
        americanOption.setPricingEngine(new BjerksundStenslandApproximationEngine(bsmProcess));
        payload.theoPrice = (americanOption.NPV()-option.optionMark.doubleValue())*100;
        payload.delta = americanOption.delta();
        payload.gamma = americanOption.gamma();
        payload.theta = americanOption.theta();
        payload.vega = americanOption.vega();
        // Ju Quadratic approximation for American
        method = "Ju Quadratic";
        americanOption.setPricingEngine(new JuQuadraticApproximationEngine(bsmProcess));
//
//        // Integral
//        method = "Integral";
//        europeanOption.setPricingEngine(new IntegralEngine(bsmProcess));
//
//        int timeSteps = 801;
//
//        // Binomial method
//        method = "Binomial Jarrow-Rudd";
//        europeanOption.setPricingEngine(new BinomialVanillaEngine<JarrowRudd>(JarrowRudd.class, bsmProcess, timeSteps));
//        bermudanOption.setPricingEngine(new BinomialVanillaEngine<JarrowRudd>(JarrowRudd.class, bsmProcess, timeSteps));
//        americanOption.setPricingEngine(new BinomialVanillaEngine<JarrowRudd>(JarrowRudd.class, bsmProcess, timeSteps));
//        double bNPV = Double.NaN;
//        if (System.getProperty("EXPERIMENTAL") != null) {
//            bNPV = bermudanOption.NPV();
//        }
//
//        method = "Binomial Cox-Ross-Rubinstein";
//        europeanOption.setPricingEngine(new BinomialVanillaEngine<CoxRossRubinstein>(CoxRossRubinstein.class, bsmProcess, timeSteps));
//        bermudanOption.setPricingEngine(new BinomialVanillaEngine<CoxRossRubinstein>(CoxRossRubinstein.class, bsmProcess, timeSteps));
//        americanOption.setPricingEngine(new BinomialVanillaEngine<CoxRossRubinstein>(CoxRossRubinstein.class, bsmProcess, timeSteps));
//        if (System.getProperty("EXPERIMENTAL") != null) {
//            bNPV = bermudanOption.NPV();
//        }
//
//        method = "Additive EquiProbabilities";
//        europeanOption.setPricingEngine(new BinomialVanillaEngine<AdditiveEQPBinomialTree>(AdditiveEQPBinomialTree.class, bsmProcess, timeSteps));
//        bermudanOption.setPricingEngine(new BinomialVanillaEngine<AdditiveEQPBinomialTree>(AdditiveEQPBinomialTree.class, bsmProcess, timeSteps));
//        americanOption.setPricingEngine(new BinomialVanillaEngine<AdditiveEQPBinomialTree>(AdditiveEQPBinomialTree.class, bsmProcess, timeSteps));
//        if (System.getProperty("EXPERIMENTAL") != null) {
//            bNPV = bermudanOption.NPV();
//        }
//
//        method = "Binomial Trigeorgis";
//        europeanOption.setPricingEngine(new BinomialVanillaEngine<Trigeorgis>(Trigeorgis.class, bsmProcess, timeSteps));
//        bermudanOption.setPricingEngine(new BinomialVanillaEngine<Trigeorgis>(Trigeorgis.class, bsmProcess, timeSteps));
//        americanOption.setPricingEngine(new BinomialVanillaEngine<Trigeorgis>(Trigeorgis.class, bsmProcess, timeSteps));
//        if (System.getProperty("EXPERIMENTAL") != null) {
//            bNPV = bermudanOption.NPV();
//        }
//
//        method = "Binomial Tian";
//        europeanOption.setPricingEngine(new BinomialVanillaEngine<Tian>(Tian.class, bsmProcess, timeSteps));
//        bermudanOption.setPricingEngine(new BinomialVanillaEngine<Tian>(Tian.class, bsmProcess, timeSteps));
//        americanOption.setPricingEngine(new BinomialVanillaEngine<Tian>(Tian.class, bsmProcess, timeSteps));
//        if (System.getProperty("EXPERIMENTAL") != null) {
//            bNPV = bermudanOption.NPV();
//        }
//
//        method = "Binomial Leisen-Reimer";
//        europeanOption.setPricingEngine(new BinomialVanillaEngine<LeisenReimer>(LeisenReimer.class, bsmProcess, timeSteps));
//        bermudanOption.setPricingEngine(new BinomialVanillaEngine<LeisenReimer>(LeisenReimer.class, bsmProcess, timeSteps));
//        americanOption.setPricingEngine(new BinomialVanillaEngine<LeisenReimer>(LeisenReimer.class, bsmProcess, timeSteps));
//        if (System.getProperty("EXPERIMENTAL") != null) {
//            bNPV = bermudanOption.NPV();
//        }
//
//        method = "Binomial Joshi";
//        europeanOption.setPricingEngine(new BinomialVanillaEngine<Joshi4>(Joshi4.class, bsmProcess, timeSteps));
//        bermudanOption.setPricingEngine(new BinomialVanillaEngine<Joshi4>(Joshi4.class, bsmProcess, timeSteps));
//        americanOption.setPricingEngine(new BinomialVanillaEngine<Joshi4>(Joshi4.class, bsmProcess, timeSteps));
//        if (System.getProperty("EXPERIMENTAL") != null) {
//            bNPV = bermudanOption.NPV();
//        }
//
//
//        //
//        //
//        //
//
//        // Finite differences
//        method = "Finite differences";
//        europeanOption.setPricingEngine(new FDEuropeanEngine(bsmProcess, timeSteps, timeSteps-1, false));
//        bermudanOption.setPricingEngine(new FDBermudanEngine(bsmProcess, timeSteps, timeSteps-1));
//        americanOption.setPricingEngine(new FDAmericanEngine(bsmProcess, timeSteps, timeSteps-1, false));
//        if (System.getProperty("EXPERIMENTAL") != null) {
//            bNPV = bermudanOption.NPV();
//        }

        return payload;
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }

    public class Payload {
        public double getTheoPrice() {
            return theoPrice;
        }

        public double getDelta() {
            return delta;
        }

        public double getGamma() {
            return gamma;
        }

        public double getTheta() {
            return theta;
        }

        public double getVega() {return vega;}

        public double theoPrice;
        public double delta;
        public double gamma;
        public double theta;
        public double vega;

        @Override
        public String toString() {
            return "Payload{" +
                    "theoPrice=" + theoPrice +
                    ", delta=" + delta +
                    ", gamma=" + gamma +
                    ", theta=" + theta +
                    ", vega=" + vega +
                    '}';
        }
    }
}
