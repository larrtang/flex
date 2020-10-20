package io.flex.UserInterface;

import io.flex.Commons.Instrument;
import io.flex.Commons.Position;
import io.flex.RiskGrapher.OptionsRiskEngine;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import com.studerw.tda.model.option.Option;
import io.flex.tda.TDAClient;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

public class Dashboard extends JFrame {

    private final TDAClient tdaClient;

    public Dashboard() {
        this.setSize(1000,600);
        this.setTitle("Line chart");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.tdaClient = new TDAClient();

        XYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        add(chartPanel);

        pack();
        this.setVisible(true);
    }

    private XYDataset createDataset() {
        io.flex.Commons.OptionChain optionChain = tdaClient.getOptionChain("SPY");
        optionChain.printExpirations();
        String expiration = "2020-10-21:2";

        Instrument instrument1 = new Instrument();
        instrument1.strike = new BigDecimal("335.0");
        instrument1.symbol = "SPY";
        instrument1.putcall = Option.PutCall.CALL;
        instrument1.quote = this.tdaClient.getClient().fetchQuote(instrument1.symbol);
        instrument1.optionMark = optionChain.callExpDateMap.get(expiration).get(instrument1.strike).get(0).getMarkPrice();

        Instrument instrument2 = new Instrument();
        instrument2.strike = new BigDecimal("350.0");
        instrument2.symbol = "SPY";
        instrument2.putcall = Option.PutCall.CALL;
        instrument2.quote = this.tdaClient.getClient().fetchQuote(instrument2.symbol);
        instrument2.optionMark = optionChain.callExpDateMap.get(expiration).get(instrument2.strike).get(0).getMarkPrice();

        Instrument instrument3 = new Instrument();
        instrument3.strike = new BigDecimal("362.0");
        instrument3.symbol = "SPY";
        instrument3.putcall = Option.PutCall.CALL;
        instrument3.quote = this.tdaClient.getClient().fetchQuote(instrument3.symbol);
        instrument3.optionMark = optionChain.callExpDateMap.get(expiration).get(instrument3.strike).get(0).getMarkPrice();

        ArrayList<Position> positions = new ArrayList<>();
        positions.add(new Position(instrument1,1));
        positions.add(new Position(instrument2,-2));
        positions.add(new Position(instrument3,1));

        OptionsRiskEngine engine = new OptionsRiskEngine(instrument1, 0.01);
        engine.positions = positions;

        Map<Double, Double> riskGraph = engine.getRiskGraphExpiration(positions);
        XYSeries series = new XYSeries(instrument1.symbol);

        for (Map.Entry<Double, Double> entry : riskGraph.entrySet()) {
            series.add(entry.getKey(), entry.getValue());
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
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

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Average salary per age",
                "Age",
                "Salary (€)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        SamplingXYLineRenderer renderer = new SamplingXYLineRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle("Average Salary per Age",
                        new Font("San", java.awt.Font.BOLD, 18)
                )
        );

        return chart;
    }

    private ArrayList<Position> buildPositions() {
        ArrayList<Position> positions = new ArrayList<>();
        return positions;
    }

    public static void main(String[] args) {
        new Dashboard();
    }
}
