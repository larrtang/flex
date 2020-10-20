package io.flex.Commons;

import com.studerw.tda.model.option.Option;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class OptionChain {
    public Map<String, Map<BigDecimal, List<Option>>> putExpDateMap;
    public Map<String, Map<BigDecimal, List<Option>>> callExpDateMap;

    public OptionChain(Map<String, Map<BigDecimal, List<Option>>> putExpDateMap,
                       Map<String, Map<BigDecimal, List<Option>>> callExpDateMap
    ) {
        this.putExpDateMap = putExpDateMap;
        this.callExpDateMap = callExpDateMap;
    }

    public OptionChain(com.studerw.tda.model.option.OptionChain optionChain) {
        this.putExpDateMap = optionChain.getPutExpDateMap();
        this.callExpDateMap = optionChain.getCallExpDateMap();
    }

    public void printExpirations() {
        System.out.println("PUTS");
        putExpDateMap.forEach((k, v) -> System.out.println(k));
        System.out.println("\nCALLS");
        callExpDateMap.forEach((k, v) -> System.out.println(k));
    }
}
