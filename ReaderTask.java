import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class ReaderTask implements Runnable {
    private static final String FILEPATH = "../1BRC/bigdata.csv";
//    private final Integer threadNumber;
    private final Integer skip;
    private final Integer read;
    private Map<String, TemperatureMetrics> map;

    public ReaderTask(Integer threadNumber, Integer skip, Integer read, Map<String, TemperatureMetrics> map) {
//        this.threadNumber = threadNumber;
        this.skip = skip;
        this.read = read;
        this.map = map;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILEPATH))) {
            if (br.skip(skip) < skip) return;
            String line;
            for (int i = 0; i < read; i++) {
                line = br.readLine();
                if (line == null) break;
//                System.out.println(threadNumber.toString() + ": " + i);
                String[] kv = line.split(";");
                Optional<TemperatureMetrics> value = Optional.ofNullable(map.getOrDefault(kv[0], null));
                value.ifPresentOrElse(
                        temperatureMetrics -> temperatureMetrics.add(kv[1]),
                        () -> map.put(kv[0], new TemperatureMetrics(kv[0], kv[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
