package com.github.lzenczuk.crawler.scenario.impl.poloniex;

/**
 * Created by dev on 13/07/16.
 */
public class PoloniexWsMessage {

    /*
    #0 	[148,70136450,[["o",0,"0.01590151","3.63162302"],["t","13465180",1,"0.01590151","0.06550097",1468423945]]]
    #1	148
    #2	70136450
    #3	[["o",0,"0.01590151","3.63162302"],["t","13465180",1,"0.01590151","0.06550097",1468423945]]
     */

    private static final String mainRegex = "\\[(\\d+),(\\d+),(.*)\\]";


    private final String body;

    public PoloniexWsMessage(String body) {
        this.body = body;
    }

    public long getMessageTypeId(){
        return 0l;
    }

    private void parseBody(){

    }
}
