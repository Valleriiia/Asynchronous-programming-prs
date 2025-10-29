import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MultiplierTask implements Callable<List<Integer>> {
    private final List<Integer> numbers;
    private final int multiplier;

    public MultiplierTask(List<Integer> numbers, int multiplier) {
        this.numbers = numbers;
        this.multiplier = multiplier;
    }

    @Override
    public List<Integer> call() {
        List<Integer> result = new ArrayList<>();
        for (Integer num : numbers) {
            result.add(num * multiplier);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return result;
    }
}
