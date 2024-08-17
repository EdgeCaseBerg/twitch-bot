package space.peetseater.bot.rewards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.*;

class BestGirlVotesDBTest {

    BestGirlVotesDB bestGirlVotesDB;
    Path testFile = Paths.get("test-db");

    @BeforeEach
    public void setup() throws IOException {
        bestGirlVotesDB = new BestGirlVotesDB();
        Files.deleteIfExists(testFile);
    }

    public void makeTestFile() {
        try {
            String testData = "charactername||2\ncharactertwo||3\n";
            Files.writeString(testFile, testData, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            // Throw up your hands if the test cant make a test file.
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLoad() throws IOException {
        makeTestFile();
        bestGirlVotesDB.loadScore(Paths.get("test-db"));
        assertEquals(2, bestGirlVotesDB.getScore("charactername"));
        assertEquals(3, bestGirlVotesDB.getScore("charactertwo"));
    }

    @Test
    public void testSave() throws IOException {
        bestGirlVotesDB.addVote("character1");
        bestGirlVotesDB.addVote("character1");
        bestGirlVotesDB.addVote("character2");
        bestGirlVotesDB.saveScore(testFile);
        String fileContents = Files.readString(testFile);
        assertEquals("character1||2\ncharacter2||1\n" ,fileContents);
    }

}