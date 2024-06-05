import java.util.concurrent.atomic.AtomicLong;

public class TemperatureMetrics {
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
        return min;
    }

    public float getMax() {
        return max;
    }

    public synchronized void add(float temperature) {
        this.total += (long) (temperature * 10);
        this.quantity += 1;
        if (temperature > this.max) {
            this.max = temperature;
        } else if (temperature < this.min) {
            this.min = temperature;
        }
    }

    public float getAverage() {
        return this.total / (this.quantity * 10f);
    }

    @Override
    public String toString() {
        return this.min + "/" + String.format("%.1f", this.getAverage()) + "/" + this.max;
    }
}
