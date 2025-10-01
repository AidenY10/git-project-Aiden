import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;

public class StretchTester {
    public static void main(String[] args) throws Exception {
        // System.out.println(verify());
        // System.out.println(cleanup());
        // System.out.println(Git.createGitRepository());
        // System.out.println(verify());
        // System.out.println(cleanup());
        // System.out.println(Git.createBlob("Priscilla.txt"));
        // System.out.println(Git.createBlob("Aiden.txt"));
        // System.out.println(Git.createBlob("AlsoPriscilla.txt"));
        // System.out.println(readIndex());
        // deleteIndex();
        // System.out.println(readIndex());
        // System.out.println(resetAidenPriscillaBlob());
        System.out.println(Git.fullReset());
        Git.createTree("RootFolder");
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

    public static boolean cleanup() throws IOException { // swithing to nio for the future
        Path current = Path.of("git");
        if (Files.isDirectory(current)) {
            deleteHelper(current);
            Files.delete(current);
            return true;
        }
        return false;
    }

    public static void deleteHelper(Path path) throws IOException {// docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html
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

    public static boolean checkIfBlobExists(String hash) {
        Path p = Path.of("git/objects/" + hash);
        return Files.isRegularFile(p);
    }

    public static boolean resetAidenPriscillaBlob() throws IOException {
        if (checkIfBlobExists("f07609351393d43cb2ce86b263720721c69e4d15")) { // priscilla
            Path p = Path.of("git/objects/f07609351393d43cb2ce86b263720721c69e4d15");
            Files.delete(p);
        } else {
            return false;
        }
        if (checkIfBlobExists("1faf1e8390cdac9204f160c35828cc423b7acd7b")) { // aiden
            Path p = Path.of("git/objects/1faf1e8390cdac9204f160c35828cc423b7acd7b");
            Files.delete(p);
        } else {
            return false;
        }
        return true;
    }

    public static String readIndex() throws IOException {
        Path p = Path.of("git/index");
        return (Files.readString(p));
    }

    public static boolean deleteIndex() throws IOException {
        Path p = Path.of("git/index");
        Files.writeString(p, "");
        return true;
    }
}
