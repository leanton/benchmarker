/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package me.antonle.jmh;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PrimitiveCalcBenchmark {

    private static final int SIZE = 100_000_000;

    @State(Scope.Thread)
    public static class BoxState {

        public List<Integer> list = new ArrayList<>(SIZE);

        @Setup(Level.Trial)
        public void doSetup() {
            System.out.println("ArrayList setup");
            Random random = new Random();
            for (int i = 0; i < SIZE; i++) {
                list.add(random.nextInt());
            }
        }

    }

    @State(Scope.Thread)
    public static class UnboxedState {

        public int[] list = new int[SIZE];

        @Setup(Level.Trial)
        public void doSetup() {
            System.out.println("Primitive Array setup");
            Random random = new Random();
            for (int i = 0; i < SIZE; i++) {
                list[i] = random.nextInt();
            }
        }

    }

    @Benchmark
    public void boxedPrimitiveMath(BoxState state) {
        int res = state.list.stream()
            .map(x -> x * 2)
            .map(x -> x - 12)
            .map(x -> x * x)
            .mapToInt(x -> x)
            .sum();
        System.out.println(res);
    }

    @Benchmark
    public void unboxedPrimitiveMath(UnboxedState state) {
        final int res = Arrays.stream(state.list)
            .map(x -> x * 2)
            .map(x -> x - 12)
            .map(x -> x * x)
            .sum();
        System.out.println(res);
    }

    @Benchmark
    public void unboxedPrimitiveWithoutStream(UnboxedState state) {
        int res = 0;
        int[] list = state.list;
        for (int i = 0; i < list.length; i++) {
            int x = list[i];
            x *= 2;
            x -= 12;
            x *= x;
            res += x;
        }
        System.out.println(res);
    }

    @Benchmark
    public void boxedPrimitiveWithoutStream(BoxState state) {
        int res = 0;
        for (Integer t : state.list) {
            Integer x = t;
            x *= 2;
            x -= 12;
            x *= x;
            res += x;
        }
        System.out.println(res);
    }

    @Benchmark
    public void boxedPrimitiveMathSingleCall(BoxState state) {
        int res = state.list.stream()
            .map((x) -> {
                x = x * 2;
                x -= 12;
                x *= x;
                return x;
            })
            .mapToInt(x -> x)
            .sum();

        System.out.println(res);
    }

    @Benchmark
    public void unboxedPrimitiveMathSingleCall(UnboxedState state) {
        int res = Arrays.stream(state.list)
            .map((x) -> {
                x = x * 2;
                x -= 12;
                x *= x;
                return x;
            })
            .sum();

        System.out.println(res);
    }
/*
Benchmark                                              Mode  Cnt   Score   Error  Units
PrimitiveCalcBenchmark.boxedPrimitiveMath             thrpt   25   0.677 ± 0.009  ops/s
PrimitiveCalcBenchmark.unboxedPrimitiveMath           thrpt   25   1.183 ± 0.023  ops/s
PrimitiveCalcBenchmark.unboxedPrimitiveWithoutStream  thrpt   25  30.318 ± 0.145  ops/s
*/
}
