package com.e3k.fountain.webcontrol.web;

import com.google.gson.Gson;
import spark.ResponseTransformer;

public enum JsonResponseTransformer implements ResponseTransformer {

    ONE;

    private final Gson gson = new Gson();

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }
}
