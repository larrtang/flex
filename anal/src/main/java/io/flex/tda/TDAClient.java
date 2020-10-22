package io.flex.tda;

import com.studerw.tda.client.HttpTdaClient;
import com.studerw.tda.client.TdaClient;
import com.studerw.tda.model.option.Option;
import com.studerw.tda.model.option.OptionChain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TDAClient {

    public static final String TDA_CLIENT_ID = "tda.client_id";
    public static final String TDA_CLIENT_ID_ENV = "tda_client_id";
    public static final String TDA_TOKEN_REFRESH = "tda.token.refresh";
    public static final String TDA_TOKEN_REFRESH_ENV = "tda_token_refresh";


    private final TdaClient client;

    public TDAClient() {
        // get io.flex.tda auth properties
        Properties properties = new Properties();
        try {
            properties.setProperty(TDA_CLIENT_ID, System.getenv(TDA_CLIENT_ID_ENV));
            properties.setProperty(TDA_TOKEN_REFRESH, System.getenv(TDA_TOKEN_REFRESH_ENV));
        } catch (NullPointerException e) {
            Map<String, String> env = System.getenv();
            // Java 8
            //env.forEach((k, v) -> System.out.println(k + ":" + v));

            // Classic way to loop a map
            for (Map.Entry<String, String> entry : env.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
            e.printStackTrace();
        }

        client = new HttpTdaClient(properties);
    }

    public io.flex.commons.OptionChain getOptionChain(String symbol) {
        return new io.flex.commons.OptionChain(client.getOptionChain(symbol));
    }


    public void test_printOptionsChain() {
        OptionChain optionChain = client.getOptionChain("spy");
        System.out.println(optionChain.getNumberOfContracts());
        Map<String, Map<BigDecimal, List<Option>>> putExpDateMap = optionChain.getPutExpDateMap();
        Map<String, Map<BigDecimal, List<Option>>> callExpDateMap = optionChain.getCallExpDateMap();

        putExpDateMap.forEach((k, v) -> System.out.println(k));
        callExpDateMap.forEach((k, v) -> System.out.println(k));

        Map<BigDecimal, List<Option>> bigDecimalListMap = putExpDateMap.get("2020-10-30:42");

        Option option = bigDecimalListMap.get(new BigDecimal("200.0")).get(0);

        System.out.println("Bid Price: " + option.getBidPrice());

//MSFT Jan 10 2020 135 Put (Weekly)
        System.out.println("Description: " + option.getDescription());

//MSFT_011020P135
        System.out.println("TDA Symbol: " + option.getSymbol());
    }

    public TdaClient getClient() {
        return client;
    }


    public void test_optionTheoVSActual() {
        OptionChain optionChain = client.getOptionChain("spy");
        System.out.println(client.fetchQuote("SPY"));
        System.out.println(optionChain.getNumberOfContracts());
        Map<String, Map<BigDecimal, List<Option>>> putExpDateMap = optionChain.getPutExpDateMap();
        Map<String, Map<BigDecimal, List<Option>>> callExpDateMap = optionChain.getCallExpDateMap();

        putExpDateMap.forEach((k, v) -> System.out.println(k));
        Map<BigDecimal, List<Option>> bigDecimalListMap = putExpDateMap.get("2020-10-30:11");
        Option option = bigDecimalListMap.get(new BigDecimal("350.0")).get(0);
        System.out.println(option.toString());
    }
}