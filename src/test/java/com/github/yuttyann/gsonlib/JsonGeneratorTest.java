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

import com.github.yuttyann.gsonlib.JsonGenerator.ExampleJson;
import com.github.yuttyann.gsonlib.JsonGenerator.Status;

import org.junit.Test;

/**
 * GsonLib JsonGeneratorTest クラス
 * @author yuttyann44581
 */
public class JsonGeneratorTest {

    private static ExampleJson json;

    @Test
    public void test1() {
        System.out.println("JSONを生成します。");
        json = new JsonGenerator().create();
    }

    @Test
    public void test2() {
        System.out.println("一覧から特定の要素を取り出す。");
        json.getElementMap().values().forEach(e -> {
            if (e.getStatus() == Status.ON && e.getAmount() >= 10 && e.getAmount() <= 100) {
                System.out.print(e.getPassword());
                System.out.print(" == ");
                System.out.println(Math.round(e.getAmount()));
            }
        });
    }
}