import java.io.File;
import java.util.Scanner;
import java.util.concurrent.*;

public class FileSearchWorkStealing {

    static class FileSearchTask extends RecursiveTask<Integer> {
        private final File dir;
        private final String extension;

        FileSearchTask(File dir, String extension) {
            this.dir = dir;
            this.extension = extension;
        }

        @Override
        protected Integer compute() {
            int count = 0;

            File[] files = dir.listFiles();
            if (files == null) return 0;

            var tasks = new java.util.ArrayList<FileSearchTask>();

            for (File file : files) {
                if (file.isDirectory()) {
                    FileSearchTask task = new FileSearchTask(file, extension);
                    task.fork();
                    tasks.add(task);
                } else if (file.getName().toLowerCase().endsWith(extension)) {
                    count++;
                }
            }

            for (var t : tasks) count += t.join();
            return count;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String path;
        File dir;

        while (true) {
            System.out.print("Введіть шлях до директорії: ");
            path = sc.nextLine();
            dir = new File(path);
            if (dir.exists() && dir.isDirectory()) break;
            System.out.println("Невірний шлях або це не директорія.");
        }

        System.out.print("Введіть формат (наприклад .pdf): ");
        String ext = sc.nextLine().toLowerCase().trim();

        if (!ext.startsWith(".")) ext = "." + ext;

        ForkJoinPool pool = new ForkJoinPool();
        int count = pool.invoke(new FileSearchTask(dir, ext));

        System.out.println("\nЗнайдено файлів: " + count);
    }
}
