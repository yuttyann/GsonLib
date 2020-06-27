package com.github.yuttyann.scriptentityplus.json.tellraw;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;

public class JsonElement {

    private final JSONObject jsonObject = new JSONObject();

    public JsonElement(@NotNull String text, @Nullable ChatColor color) {
        this(text, color, new ChatFormat[0]);
    }

    public JsonElement(@NotNull String text, @Nullable ChatColor color, @NotNull ChatFormat... format) {
        jsonObject.put("text", text);
        if (color != null) {
            jsonObject.put("color", color.name().toLowerCase());
        }
        if (format.length > 0) {
            for (ChatFormat chatFormat : format) {
                jsonObject.put(chatFormat.getFormat(), true);
            }
        }
    }

    public void setClickEvent(@NotNull ClickEventType action, @NotNull String value) {
        JSONObject clickEvent = new JSONObject();
        clickEvent.put("action", action.getType());
        clickEvent.put("value", value);
        jsonObject.put("clickEvent", clickEvent);
    }

    public void setHoverEvent(@NotNull HoverEventType action, @NotNull String value) {
        JSONObject hoverEvent = new JSONObject();
        hoverEvent.put("action", action.getType());
        hoverEvent.put("value", value);
        jsonObject.put("hoverEvent", hoverEvent);
    }

    @NotNull
    public JSONObject getJson() {
        return jsonObject;
    }
}