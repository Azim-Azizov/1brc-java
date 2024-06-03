import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final Integer THREADS = 20;
    private static final Integer LINES = 102400000;
    public static void main(String[] args) throws IOException {
        Map<String, TemperatureMetrics> data = new ConcurrentHashMap<String, TemperatureMetrics>();
        try (ExecutorService executorService = Executors.newFixedThreadPool(THREADS)) {
            for (int i = 0; i < THREADS; i++) {
                executorService.submit(new ReaderTask(i, i * LINES, LINES, data));
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        System.out.println(data.toString());
    }
}
