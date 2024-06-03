import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicLong;

public class TemperatureMetrics {
//    private final String name;
    private final AtomicLong quantity = new AtomicLong(1);
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal total;

    public TemperatureMetrics(String name, String temperature) {
//        this.name = name;
        BigDecimal value = new BigDecimal(temperature);
        this.min = value;
        this.max = value;
        this.total = value;
    }

//    public String getName() {
//        return name;
//    }

    public BigDecimal getMin() {
        return min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public synchronized void add(String temperature) {
        BigDecimal bigDecimal = new BigDecimal(temperature);
        this.total = this.total.add(bigDecimal);
        this.quantity.incrementAndGet();
        if (bigDecimal.compareTo(this.max) > 0) {
            this.max = bigDecimal;
        } else if (bigDecimal.compareTo(this.min) < 0) {
            this.min = bigDecimal;
        }
    }

    public BigDecimal getAverage() {
        return this.total.divide(new BigDecimal(this.quantity.get()), 1, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return this.min.setScale(1, RoundingMode.HALF_UP) + "/" + this.getAverage().toString() + "/" + this.max.setScale(1, RoundingMode.HALF_UP);
    }
}
