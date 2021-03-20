/**
 * GsonLib - It provides a simple library using Gson.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.yuttyann.gsonlib;

import java.io.File;

import com.github.yuttyann.gsonlib.annotation.JsonTag;
import com.github.yuttyann.gsonlib.basic.OneJson;
import com.github.yuttyann.gsonlib.basic.OneJson.OneElement;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * GsonLib JsonTest クラス
 * @author yuttyann44581
 */
public class JsonTest {

    public static void main(String[] args) {
        var current = System.getProperty("user.dir");
        var json = new ExampleJson(new File(current + "/debug-json/test.json"));
        for (int i = 0; i < 10; i++) {
            json.load("test" + i).setAmount(100); 
        }
        json.saveJson();
    }

    @JsonTag
    public static class ExampleJson extends OneJson<String, ExampleElement> {

        public ExampleJson(@NotNull File file) {
            super(file);
        }

        @Override
        @NotNull
        protected ExampleElement newInstance(@Nullable String name) {
            return new ExampleElement(name);
        }
    }

    public static class ExampleElement extends OneElement<String> {

        @SerializedName("testName")
        private final String name;

        @SerializedName("testAmount")
        private int amount;

        public ExampleElement(@NotNull String name) {
            this.name = name;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        @NotNull
        public String getName() {
            return name;
        }

        @Override
        @Nullable
        protected String getA() {
            return name;
        }
    }
}