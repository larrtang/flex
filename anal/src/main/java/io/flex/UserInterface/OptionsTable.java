package io.flex.UserInterface;

import io.flex.commons.Portfolio;

import javax.swing.*;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class OptionsTable extends JTable {

    public static final String[] column_names = {
            "Symbol",
            "Quantity",
            "Strike" ,
            "Expiration",
            "Price",
            "Delta",
            "Gamma",
            "Theta",
            "Vega"};


    public static final int Symbol_offset       = 0;
    public static final int Quantity_offset     = 1;
    public static final int Strike_offset       = 2;
    public static final int Expiration_offset   = 3;
    public static final int Price_offset        = 4;
    public static final int Delta_offset        = 5;
    public static final int Gamma_offset        = 6;
    public static final int Theta_offset        = 7;
    public static final int Vega_offset         = 8;
    public static final int col_length          = 9;

    public OptionsTable(Portfolio synced_portfolio) {
        super(generate(synced_portfolio), column_names);
        this.setBounds(30, 40, 200, 300);
    }

    public static String[][] generate(Portfolio synced_portfolio) {
        String[][] data = new String[synced_portfolio.getTotalNumberOfPositions()][col_length];

        AtomicInteger num_options = new AtomicInteger();
        synced_portfolio.forEach((sym, position_list) -> {
            position_list.forEach((position -> {

                String[] col            = new String[col_length];
                col[Symbol_offset]      = sym;
                col[Quantity_offset]    = Long.toString(position.quantity);
                col[Strike_offset]      = position.instrument.strike.toString();
                col[Expiration_offset]  = position.instrument.expirationString;
                col[Price_offset]       = Double.toString(position.instrument.optionMark.doubleValue());
                col[Delta_offset]       = String.valueOf(position.instrument.option.getDelta().doubleValue());
                col[Gamma_offset]       = String.valueOf(position.instrument.option.getGamma().doubleValue());
                col[Theta_offset]       = String.valueOf(position.instrument.option.getTheta().doubleValue());
                col[Vega_offset]        = String.valueOf(position.instrument.option.getVega().doubleValue());

                for (String s : col) {
                    System.out.print(s + " ");
                }
                System.out.println();
                data[num_options.get()] = col;
                num_options.getAndIncrement();
            }));
        });
        return data;
    }
}
