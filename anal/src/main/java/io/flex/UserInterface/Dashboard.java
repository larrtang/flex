package io.flex.UserInterface;

import io.flex.business.MarketDataEngine;
import io.flex.commons.Instrument;
import io.flex.commons.Portfolio;
import io.flex.commons.Position;
import io.flex.business.OptionsRiskEngine;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import com.studerw.tda.model.option.Option;
import com.studerw.tda.model.option.Option.PutCall.*;

import io.flex.tda.TDAClient;
import org.jquantlib.math.Ops;

import javax.sound.sampled.Port;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import static com.studerw.tda.model.option.Option.PutCall.CALL;
import static com.studerw.tda.model.option.Option.PutCall.PUT;

public class Dashboard extends JFrame {

    private final TDAClient tdaClient;

    private ChartPanel chartPanel;

    private JFreeChart chart;

    private XYDataset chart_dataset;

    private String viewingSymbol = null;

    private final MarketDataEngine mdEngine;

    private final OptionsRiskEngine riskEngine;

    private final Portfolio portfolio = new Portfolio();

    private Instrument currentMasterInstrument = null;

    private ValueMarker domainMarker;

    private void __buildPortfolio() {
        String expString = "2020-11-13:22";
        //String sym = "$SPX.X";
        String sym = "SPY";
        Instrument o1 = Instrument.createOptionInstrument(tdaClient, sym, 330, expString, PUT);
        Instrument o2 = Instrument.createOptionInstrument(tdaClient, sym, 340, expString, PUT);
        Instrument o3 = Instrument.createOptionInstrument(tdaClient, sym, 350, expString, PUT);
        Instrument o4 = Instrument.createOptionInstrument(tdaClient, sym, 346, expString, CALL);

        ArrayList<Position> positions = new ArrayList<>();
        positions.add(new Position(o1, 10));
        positions.add(new Position(o2, -20));
        positions.add(new Position(o3, 10));
        positions.add(new Position(o4, 1));

        this.portfolio.put(sym, positions);
        this.currentMasterInstrument = o1;
    }

    public Dashboard() {
        this.setMinimumSize(new Dimension(1300,600));
        this.setTitle("Line chart");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.tdaClient = new TDAClient();
        this.__buildPortfolio();

        this.mdEngine = new MarketDataEngine(this.tdaClient, this::updateCharts);
        this.mdEngine.portfolio = this.portfolio;       // setting to this generated portfolio
        this.riskEngine = new OptionsRiskEngine(this.mdEngine.portfolio, 0.05);
        this.riskEngine.positions = this.portfolio.get(this.portfolio.firstSymbol);




        //this.chart_dataset = createDataset();
        this.chart = createChart(this.chart_dataset);
        chartPanel = new ChartPanel(chart);
        chartPanel.setMinimumSize(new Dimension(1200,800));
        chartPanel.setSize(1200, 800);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);

        domainMarker = new ValueMarker(this.currentMasterInstrument.underlyingMark);
        this.chart.getXYPlot().addDomainMarker(domainMarker);
        this.chart.getXYPlot().addRangeMarker(new ValueMarker(0));

        add(chartPanel);

        pack();
        this.setVisible(true);
        this.mdEngine.start();
    }

    private XYDataset createDataset() {
        io.flex.commons.OptionChain optionChain = tdaClient.getOptionChain("SPY");
        optionChain.printExpirations();
        String expiration = "2020-11-16:26";

        Instrument instrument1 = new Instrument();
        instrument1.strike = new BigDecimal("330.0");
        instrument1.symbol = "SPY";
        instrument1.putcall = Option.PutCall.CALL;
        instrument1.expirationString = expiration;
        instrument1.underlyingQuote = this.tdaClient.getClient().fetchQuote(instrument1.symbol);
        instrument1.iv = optionChain.callExpDateMap.get(expiration).get(instrument1.strike).get(0).getVolatility().doubleValue()*0.01;
        instrument1.optionMark = optionChain.callExpDateMap.get(expiration).get(instrument1.strike).get(0).getMarkPrice();

        Instrument instrument2 = new Instrument();
        instrument2.strike = new BigDecimal("340.0");
        instrument2.symbol = "SPY";
        instrument2.putcall = Option.PutCall.CALL;
        instrument2.expirationString = expiration;
        instrument2.underlyingQuote = this.tdaClient.getClient().fetchQuote(instrument2.symbol);
        instrument2.iv = optionChain.callExpDateMap.get(expiration).get(instrument2.strike).get(0).getVolatility().doubleValue()*0.01;
        instrument2.optionMark = optionChain.callExpDateMap.get(expiration).get(instrument2.strike).get(0).getMarkPrice();

        Instrument instrument3 = new Instrument();
        instrument3.strike = new BigDecimal("350.0");
        instrument3.symbol = "SPY";
        instrument3.putcall = Option.PutCall.CALL;
        instrument3.expirationString = expiration;
        instrument3.underlyingQuote = this.tdaClient.getClient().fetchQuote(instrument3.symbol);
        instrument3.iv = optionChain.callExpDateMap.get(expiration).get(instrument3.strike).get(0).getVolatility().doubleValue()*0.01;
        instrument3.optionMark = optionChain.callExpDateMap.get(expiration).get(instrument3.strike).get(0).getMarkPrice();

        Instrument instrument4 = new Instrument();
        instrument4.strike = new BigDecimal("348.0");
        instrument4.symbol = "SPY";
        instrument4.putcall = Option.PutCall.CALL;
        instrument4.expirationString = expiration;
        instrument4.underlyingQuote = this.tdaClient.getClient().fetchQuote(instrument4.symbol);
        instrument4.iv = optionChain.callExpDateMap.get(expiration).get(instrument4.strike).get(0).getVolatility().doubleValue()*0.01;
        instrument4.optionMark = optionChain.callExpDateMap.get(expiration).get(instrument4.strike).get(0).getMarkPrice();

        ArrayList<Position> positions = new ArrayList<>();
        positions.add(new Position(instrument1,10));
        positions.add(new Position(instrument2,-20));
        positions.add(new Position(instrument3,10));
        positions.add(new Position(instrument4,1));
        OptionsRiskEngine engine = new OptionsRiskEngine(instrument1, 0.05);
        engine.positions = positions;

        Map<Double, Double> riskGraph = engine.getRiskGraphToday();
        XYSeries series = new XYSeries(instrument1.symbol);

        for (Map.Entry<Double, Double> entry : riskGraph.entrySet()) {
            series.add(entry.getKey(), entry.getValue());
        }

        Map<Double, Double> expRiskGraph = engine.getRiskGraphExpiration(positions);
        XYSeries seriesExp = new XYSeries(instrument1.symbol);

        for (Map.Entry<Double, Double> entry : expRiskGraph.entrySet()) {
            seriesExp.add(entry.getKey(), entry.getValue());
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesExp);
        dataset.addSeries(series);
        return dataset;
//        XYSeries series = new XYSeries("2016");
//        series.add(18, 567);
//        series.add(20, 612);
//        series.add(25, 800);
//        series.add(30, 980);
//        series.add(40, 1410);
//        series.add(50, 2350);
//
//        XYSeriesCollection dataset = new XYSeriesCollection();
//        dataset.addSeries(series);
//
//        return dataset;
    }

    private JFreeChart createChart(XYDataset dataset) {
        chart = ChartFactory.createXYLineChart(
                "Average salary per age",
                "Underlying Price",
                "PnL",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        SamplingXYLineRenderer renderer = new SamplingXYLineRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(0.2f));
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(0.7f));

        renderer.setSeriesPaint(2, Color.BLACK);
        renderer.setSeriesStroke(2, new BasicStroke(0.7f));



        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.lightGray);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.lightGray);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle("SPY",
                        new Font("San", java.awt.Font.BOLD, 18)
                )
        );


        return chart;
    }

    private void updateCharts(Portfolio portfolio) {
        Map<Double, Double> riskGraph = riskEngine.getRiskGraphToday();
        XYSeries series = new XYSeries(portfolio.firstSymbol);

        for (Map.Entry<Double, Double> entry : riskGraph.entrySet()) {
            series.add(entry.getKey(), entry.getValue());
        }

        Map<Double, Double> expRiskGraph = riskEngine.getRiskGraphExpiration(riskEngine.positions);
        XYSeries seriesExp = new XYSeries(portfolio.firstSymbol);

        for (Map.Entry<Double, Double> entry : expRiskGraph.entrySet()) {
            seriesExp.add(entry.getKey(), entry.getValue());
        }

        domainMarker.setValue(this.currentMasterInstrument.underlyingMark);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesExp);
        dataset.addSeries(series);
        this.chart_dataset = dataset;
        this.chart.setNotify(true);
        this.chart.getXYPlot().setDataset(this.chart_dataset);
        this.chartPanel.repaint();
        System.out.print(".");
    }

    public static void main(String[] args) {
        new Dashboard();
    }
}
