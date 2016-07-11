package com.github.lzenczuk.crawler.scenario.impl.coindesk;

import com.github.lzenczuk.crawler.scenario.impl.iconomi.IconomiStorageException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dev on 11/07/16.
 */
public class CoinDeskPriceStorage {

    private final String dataFileName;

    public CoinDeskPriceStorage(String dataFileName) {
        this.dataFileName = dataFileName;
    }

    public void storePrice(Date date, String priceUsd, String priceEur, String priceGbp, String priceCny) throws IconomiStorageException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(dataFileName, true);
            fileOutputStream.write((dateFormat.format(date)+","+priceUsd+","+priceEur+","+priceGbp+","+priceCny+"\n").getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            throw new IconomiStorageException("Storage error: "+e.getMessage());
        }
    }
}
