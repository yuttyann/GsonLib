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
import java.util.Random;

import com.github.yuttyann.gsonlib.annotation.JsonTag;
import com.github.yuttyann.gsonlib.basic.TwoJson;
import com.github.yuttyann.gsonlib.basic.TwoJson.TwoElement;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * GsonLib JsonGenerator クラス
 * @author yuttyann44581
 */
public class JsonGenerator {

    private final ExampleJson json;

    {
        var current = System.getProperty("user.dir");
        this.json = new ExampleJson(new File(current + "/debug-json/test.json"));
    }

    @NotNull
    public ExampleJson getJson() {
        return json;
    }

    @NotNull
    public ExampleJson create() {
        var random = new Random();
        json.deleteFile(); // 一度削除
        for (int i = 0; i < 1000; i++) {
            json.load("test" + i, i % 2 == 0 ? Status.ON : Status.OFF).setAmount(random.nextInt(i + 1) * Math.random()); 
        }
        json.saveJson();
        System.out.println("保存しました。");
        return json;
    }

    public enum Status { ON, OFF; }

    @JsonTag
    public class ExampleJson extends TwoJson<String, Status, ExampleElement> {

        public ExampleJson(@NotNull File file) {
            super(file);
        }

        @Override
        @NotNull
        protected ExampleElement newInstance(@Nullable String password, @NotNull JsonGenerator.Status status) {
            return new ExampleElement(password, status);
        }
    }

    public class ExampleElement extends TwoElement<String, Status> {

        @SerializedName("password")
        private final String password;

        @SerializedName("status")
        private final Status status;

        @SerializedName("amount")
        private double amount;

        public ExampleElement(@NotNull String password, @NotNull Status status) {
            this.password = password;
            this.status = status;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }
        
        public double getAmount() {
            return amount;
        }

        @NotNull
        public String getPassword() {
            return getA();
        }

        @NotNull
        public Status getStatus() {
            return getB();
        }

        @Override
        @NotNull
        protected String getA() {
            return password;
        }

        @Override
        @NotNull
        protected Status getB() {
            return status;
        }
    }
}