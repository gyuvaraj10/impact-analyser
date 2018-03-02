package com.impact.analyser.cucumber.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by Yuvaraj on 02/03/2018.
 */
public class CucumberReportFormatter{

    public static List<CucumberResultReport> parse(String jsonReportContent) {
        Gson gson = new Gson();
        return gson.fromJson(jsonReportContent, new TypeToken<List<CucumberResultReport>>(){}.getType());
    }
}
