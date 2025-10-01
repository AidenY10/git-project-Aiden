import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;

public class Git {
    public static void main(String[] args) throws IOException {
        createGitRepository();
    }

    public static boolean createGitRepository() throws IOException {
        File git = new File("git");
        if (!git.exists()) {
            git.mkdir();
            File objects = new File("git/objects");
            objects.mkdir();
            File index = new File("git/index");
            index.createNewFile();
            File head = new File("git/HEAD");
            head.createNewFile();
            System.out.println("Git Repository Created");
            return true;
        }
        System.out.println("Git Repository Already Exists");
        return false;
    }

    public static String generateShaOne(String path) throws Exception { // www.baeldung.com/sha-256-hashing-java
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("File not found");
        }
        if (testHashFileEmptyFiles(path)) {
            return "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        }
        if (testHashFileLargeFiles(path)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            try (FileInputStream fis = new FileInputStream(path)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }
            return bytesToHex(digest.digest());
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            String contents = Files.readString(Path.of(path));
            byte[] encodedhash = digest.digest(contents.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (Exception e) {

        }
        return null;
    }

    private static String bytesToHex(byte[] hash) { // https://www.baeldung.com/sha-256-hashing-java
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static boolean testHashFileEmptyFiles(String path) {
        File file = new File(path);
        return file.length() == 0;
    }

    public static boolean testHashFileLargeFiles(String path) {
        File file = new File(path);
        if (file.length() > 52428800) {
            return true;
        }
        return false;
    }

    public static boolean createBlob(String pathStr) throws Exception {
        Path input = Path.of(pathStr);
        String contents = Files.readString(input);
        String blobStr = "git/objects/temp";
        Path blob = Path.of(blobStr);
        Files.createFile(blob);
        Files.writeString(blob, contents);
        String hash = generateShaOne(blobStr);
        Path renamed = Path.of("git/objects/" + hash);
        if (Files.isRegularFile(renamed)) {
            Files.delete(blob);
            addToIndex(hash, pathStr);
            return false;
        }
        Files.move(blob, renamed);
        addToIndex(hash, pathStr);
        return true;
    }

    public static boolean addToIndex(String hash, String original) throws IOException {
        Path p = Path.of("git/index");
        List<String> allLines = Files.readAllLines(p);
        int counter = 0;
        int found = -1;
        for (String str : allLines) {
            int index = str.indexOf(original);
            if (index == -1) {
                counter++;
            } else {
                found = counter;
                break;
            }
        }
        if (found != -1) {
            allLines.remove(found);
            Files.writeString(p, "");
            for (int i = 0; i < allLines.size(); i++) {
                if (Files.size(p) != 0) {
                    Files.writeString(p, "\n", StandardOpenOption.APPEND);
                }
                Files.writeString(p, allLines.get(i), StandardOpenOption.APPEND);
            }
        }
        if (Files.size(p) != 0) {
            Files.writeString(p, "\n", StandardOpenOption.APPEND);
        }
        Files.writeString(p, hash + " " + original, StandardOpenOption.APPEND);
        return true;
    }

    public static boolean fullReset() throws IOException {
        StretchTester.cleanup();
        createGitRepository();
        return true;
    }

    public static String createTree(String path) throws Exception {
        return createTree(path, 0);
    }

    public static String createTree(String path, int tempCount) throws Exception {
        Path parameterPath = Path.of(path);
        Stream<Path> stream = Files.walk(parameterPath);
        boolean isFirst = true;
        Path treePath = Path.of("git/objects/temporary" + String.valueOf(tempCount));
        Files.createFile(treePath);
        for (Path p : (Iterable<Path>) stream::iterator) {
            if (isFirst) {
                isFirst = false;
                continue;
            }
            if (Files.isDirectory(p)) {
                tempCount++;
                String workingSha = createTree(p.toString(), tempCount);
                if (Files.size(treePath) != 0) {
                    Files.writeString(treePath, "\n", StandardOpenOption.APPEND);
                }
                Files.writeString(treePath, "tree " + generateShaOne(workingSha) + " " + p.toString(),
                        StandardOpenOption.APPEND);
            } else {
                if (Files.size(treePath) != 0) {
                    Files.writeString(treePath, "\n", StandardOpenOption.APPEND);
                }
                Files.writeString(treePath, "blob " + generateShaOne(p.toString()) + " " + p.toString(),
                        StandardOpenOption.APPEND);
                createBlob(p.toString());
            }
        }
        stream.close();
        String shaOne = generateShaOne(treePath.toString());
        Files.move(treePath, Path.of("git/objects/" + shaOne));
        return shaOne;
    }
}