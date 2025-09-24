import java.io.*;
import java.nio.file.*;

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
}