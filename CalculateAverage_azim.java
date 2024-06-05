/*
 *  Copyright 2023 The original authors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package dev.morling.onebrc;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class CalculateAverage_azim {
    private static final File file = new File("./measurements.txt");
    private static final long FILESIZE = file.length();
    private static final Integer THREADS = FILESIZE > 10000 ? 100 : FILESIZE > 1000 ? 10 : 1;
    private static final long CHUNKSIZE = FILESIZE / THREADS;

    public static void main(String[] args) throws Exception {
        Map<String, TemperatureMetrics> result = new HashMap<>();
        try (ExecutorService executorService = Executors.newFixedThreadPool(THREADS)) {
            List<Future<Map<String, TemperatureMetrics>>> futures = new ArrayList<>();
            long start = 0;
            for (int i = 0; i < THREADS; i++) {
                long end = (i == THREADS - 1) ? FILESIZE : start + CHUNKSIZE;
                futures.add(executorService.submit(new ReaderTask(file, start, end)));
                start = end;
            }

            for (Future<Map<String, TemperatureMetrics>> future : futures) {
                Map<String, TemperatureMetrics> map = future.get();
                for (Map.Entry<String, TemperatureMetrics> entry : map.entrySet()) {
                    result.merge(entry.getKey(), entry.getValue(), TemperatureMetrics::add);
                }
            }
        }
        result = new TreeMap<>(result);
        System.out.println(result);
    }
}

class TemperatureMetrics {
    private int quantity = 1;
    private float min;
    private float max;
    private long total;

    public TemperatureMetrics(float temperature) {
        this.min = temperature;
        this.max = temperature;
        this.total = (long) (temperature * 10);
    }

    public float getMin() {
        return this.min;
    }

    public float getMax() {
        return this.max;
    }

    public long getTotal() {
        return this.total;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public TemperatureMetrics add(float temperature) {
        this.total += (long) (temperature * 10);
        this.quantity += 1;
        if (temperature > this.max) {
            this.max = temperature;
        }
        else if (temperature < this.min) {
            this.min = temperature;
        }
        return this;
    }

    public TemperatureMetrics add(TemperatureMetrics temperatureMetrics) {
        this.total += temperatureMetrics.getTotal();
        this.quantity += temperatureMetrics.getQuantity();
        if (temperatureMetrics.getMin() < this.min)
            this.min = temperatureMetrics.getMin();
        if (temperatureMetrics.getMax() > this.max)
            this.max = temperatureMetrics.getMax();
        return this;
    }

    public float getAverage() {
        return (float) Math.ceil(((float) this.total) / this.quantity) / 10;
    }

    @Override
    public String toString() {
        float average = getAverage();
        int averageIntPart = (int) average;
        int averageDecimalPart = (int) ((average - averageIntPart) * 10);
        return new StringBuilder()
                .append(min)
                .append('/')
                .append(averageIntPart)
                .append('.')
                .append(averageDecimalPart)
                .append('/')
                .append(max)
                .toString();
    }
}

class ReaderTask implements Callable<Map<String, TemperatureMetrics>> {
    private final File file;
    private final long start;
    private final long end;
    private final Map<String, TemperatureMetrics> map;

    public ReaderTask(File file, long start, long end) {
        this.file = file;
        this.start = start;
        this.end = end;
        this.map = new HashMap<>();
    }

    @Override
    public Map<String, TemperatureMetrics> call() throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek(start);
            long bytesRead = 0;
            try (BufferedReader reader = new BufferedReader(new FileReader(raf.getFD()))) {
                int c;
                if (start != 0)
                    while ((c = reader.read()) != '\n') {
                        bytesRead++;
                    }
                StringBuilder key;
                float number;
                short sign;
                while (start + bytesRead <= end) {
                    key = new StringBuilder();
                    while ((c = reader.read()) != ';') {
                        if (c == -1)
                            return map;
                        bytesRead++; // char
                        key.append((char) c);
                    }
                    bytesRead++; // ;
                    number = 0;
                    sign = 1;
                    while ((c = reader.read()) != '\n') {
                        bytesRead++;
                        if (c == '-') {
                            sign = -1;
                        }
                        else if (c == '.') {
                            c = reader.read();
                            bytesRead++;
                            number += (c - 48) / 10f;
                        }
                        else
                            number = number * 10 + (c - 48);
                    }
                    float finalNumber = number * sign;
                    map.compute(key.toString(), (k, v) -> {
                        if (v == null)
                            return new TemperatureMetrics(finalNumber);
                        else {
                            return v.add(finalNumber);
                        }
                    });
                    bytesRead++;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
