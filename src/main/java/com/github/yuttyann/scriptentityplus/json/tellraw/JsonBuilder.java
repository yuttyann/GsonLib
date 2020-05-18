package com.github.yuttyann.scriptentityplus.json.tellraw;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;

public class JsonBuilder {

    private final JSONArray jsonArray = new JSONArray();

    public JsonBuilder() {
        jsonArray.add("");
    }

    public void add(@NotNull JsonElement element) {
        jsonArray.add(element.getJson());
    }

    @NotNull
    public String toJson() {
        return jsonArray.toJSONString();
    }
}