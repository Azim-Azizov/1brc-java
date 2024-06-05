import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    private static final Integer THREADS = 10;
    private static final File file = new File("../1brc/measurements.txt");
    private static final long FILESIZE = file.length();
    private static final long CHUNKSIZE = FILESIZE / THREADS;

    public static void main(String[] args) throws Exception {
        Map<String, TemperatureMetrics> data = new ConcurrentHashMap<>();
        try (ExecutorService executorService = Executors.newFixedThreadPool(THREADS)) {
            List<Future<Void>> futures = new ArrayList<>();
            long start = 0;
            for (int i = 0; i < THREADS; i++) {
                long end = (i == THREADS - 1) ? FILESIZE : start + CHUNKSIZE;
                futures.add(executorService.submit(new ReaderTask(file, start, end, data)));
                start = end;
            }

            for (Future<Void> future : futures) {
                future.get();
            }
        }
        Map<String, TemperatureMetrics> result = new TreeMap<>(data);
        System.out.println(result);
    }
}
