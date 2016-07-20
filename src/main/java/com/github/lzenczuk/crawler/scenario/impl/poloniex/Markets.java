package com.github.lzenczuk.crawler.scenario.impl.poloniex;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by dev on 19/07/16.
 */
public class Markets {

    private Set<String> currencies;
    private Map<String, Integer> marketsIdByMarketName;
    private Map<Integer, String> marketsNameByMarketId;

    public Markets() {
        currencies = new HashSet<>();
        marketsIdByMarketName = new HashMap<>();
        marketsNameByMarketId = new HashMap<>();

    }

    public void addMarket(String marketName, int id){
        String[] cr = marketName.split("_");
        if(cr.length==2){
            String currency1 = cr[0];
            String currency2 = cr[1];

            currencies.add(currency1);
            currencies.add(currency2);
        }

        marketsIdByMarketName.put(marketName, id);
        marketsNameByMarketId.put(id, marketName);
    }

    public String getMarketNameById(int id){
        return marketsNameByMarketId.get(id);
    }

    public Integer getMarketIdByName(String marketName){
        return marketsIdByMarketName.get(marketName);
    }

    public List<String> getMarketsNames(){
        return marketsIdByMarketName.keySet().stream().collect(Collectors.toList());
    }

    public List<String> getFilteredMarketsNames(String pharse){
        return getMarketsNames().stream().filter(s -> s.contains(pharse)).collect(Collectors.toList());
    }

    public List<String> getCurrencies(){
        return currencies.stream().collect(Collectors.toList());
    }
}
