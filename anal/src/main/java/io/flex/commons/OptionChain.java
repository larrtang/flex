package io.flex.commons;

import com.studerw.tda.model.option.Option;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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

    public HashMap<Option.PutCall, ArrayList<String>> getExpirations() {
        HashMap<Option.PutCall, ArrayList<String>> map = new HashMap<>();
        ArrayList<String> putList  = new ArrayList<>();
        ArrayList<String> callList  = new ArrayList<>();

        putExpDateMap.forEach((k, v) -> putList.add(k));
        callExpDateMap.forEach((k, v) -> callList.add(k));

        map.put(Option.PutCall.PUT, putList);
        map.put(Option.PutCall.PUT, callList);
        return map;
    }
}
