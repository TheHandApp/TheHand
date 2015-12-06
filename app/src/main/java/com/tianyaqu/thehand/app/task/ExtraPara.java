package com.tianyaqu.thehand.app.task;

import java.util.ArrayList;

/**
 * Created by Alex on 2015/12/3.
 */
public class ExtraPara {
    private ArrayList<String> parameters;

    public ExtraPara add(String para){
        if(parameters == null){
            parameters = new ArrayList<String>();
        }
        parameters.add(para);
        return this;
    }

    public ArrayList<String> getParameters() {
        return parameters;
    }
}
