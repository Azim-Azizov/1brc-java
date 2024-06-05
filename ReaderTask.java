import java.io.*;
import java.util.Map;
import java.util.concurrent.Callable;

public class ReaderTask implements Callable<Void> {
    private final File file;
    private final long start;
    private final long end;
    private final Map<String, TemperatureMetrics> map;

    public ReaderTask(File file, long start, long end, Map<String, TemperatureMetrics> map) {
        this.file = file;
        this.start = start;
        this.end = end;
        this.map = map;
    }

    @Override
    public Void call() throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek(start);
            long bytesRead = 0;
            BufferedReader reader = new BufferedReader(new FileReader(raf.getFD()));
            if (start != 0) reader.readLine();
            String line;
            while ((line = reader.readLine()) != null && start + bytesRead < end) {
                int index = line.length();
                while (line.charAt(--index) != ';' && index >= line.length() - 7) {}
                String key = line.substring(0, index);
                float finalNumber = Float.parseFloat(line.substring(index + 1));
                map.compute(key, (k, v) -> {
                    if (v == null) return new TemperatureMetrics(finalNumber);
                    else {
                        v.add(finalNumber);
                        return v;
                    }
                });
                bytesRead += line.length() + 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
