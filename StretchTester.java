import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;

public class StretchTester {
    public static void main(String[] args) throws IOException {
        // System.out.println(verify());
        // System.out.println(cleanup());
        System.out.println(Git.createGitRepository());
        System.out.println(verify());
        // System.out.println(cleanup());
    }

    public static boolean verify() {
        File git = new File("git");
        if (!git.exists()) {
            return false;
        }
        File objects = new File("git/objects");
        if (!objects.exists()) {
            return false;
        }
        File index = new File("git/index");
        if (!index.exists()) {
            return false;
        }
        File head = new File("git/HEAD");
        if (!head.exists()) {
            return false;
        }
        return true;
    }

    public static boolean cleanup() throws IOException { //swithing to nio for the future
        Path current = Path.of("git");
        if (Files.isDirectory(current)) {
            deleteHelper(current);
            Files.delete(current);
            return true;
        }
        return false;
    }

    public static void deleteHelper(Path path) throws IOException {//docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html
        Stream<Path> stream = Files.walk(path);
        boolean isFirst = true;
        for (Path p : (Iterable<Path>) stream::iterator) {
            if (isFirst) {
                isFirst = false;
                continue;
            }
            if (Files.isDirectory(p)) {
                deleteHelper(p);
            }
            Files.delete(p);
        }
        stream.close();
    }

    
}
