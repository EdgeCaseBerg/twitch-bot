package space.peetseater.bot.rewards;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BestGirlVotesDB {
    ConcurrentHashMap<String, Integer> score;
    public BestGirlVotesDB() {
        score = new ConcurrentHashMap<>();
    }

    public void loadScore(Path path) throws IOException {
        String data = Files.readString(path, StandardCharsets.UTF_8);
        StringTokenizer tokenizer = new StringTokenizer(data, "|\n", false);
        assert (tokenizer.countTokens() % 2 == 0);
        while (tokenizer.hasMoreTokens()) {
            String name = tokenizer.nextToken();
            String votes = tokenizer.nextToken();
            score.put(name, Integer.parseInt(votes));
        }
    }

    public void addVote(String girl) {
        score.put(girl, score.getOrDefault(girl, 0) + 1);
    }

    public void saveScore(Path path) throws IOException {
        String perCharacterFormat = "%s||%d\n";
        // For the sake of testing sanity, save the characters in alphabetical order by name
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(score.entrySet().stream().toList());
        entries.sort(Map.Entry.comparingByKey());
        Iterator<Map.Entry<String, Integer>> toSave = entries.iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while (toSave.hasNext()) {
            Map.Entry<String, Integer> entry = toSave.next();
            stringBuilder.append(
                perCharacterFormat.formatted(entry.getKey(), entry.getValue())
            );
        }
        Files.writeString(path, stringBuilder.toString(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    }

    public int getScore(String characterName) {
        return score.getOrDefault(characterName, 0);
    }

    @Override
    public String toString() {
        Iterator<Map.Entry<String, Integer>> entries = score.entrySet().iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while(entries.hasNext()) {
            Map.Entry<String, Integer> entry = entries.next();
            stringBuilder.append(entry.getKey()).append(":").append(entry.getValue());
        }
        return stringBuilder.toString();
    }
}
