package space.peetseater.bot.bible;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BibleScoreCalculatorTest {

    BibleScoreCalculator bibleScoreCalculator;
    BibleProvider provider;

    @BeforeEach
    public void setup() {
        HashMap<String, Integer> words = new HashMap<>();
        words.put("word", 1);
        provider = new BibleProvider() {
            @Override
            public HashMap<String, Integer> getWordsInBible() {
                return words;
            }
        };
        bibleScoreCalculator = new BibleScoreCalculator(provider);
    }

    @Test
    public void testAllWordsInScore() {
        float score = bibleScoreCalculator.calculateScore("word word word");
        assertEquals(1.0f, score);
    }

    @Test
    public void testHalfWordsInScore() {
        float score = bibleScoreCalculator.calculateScore("word word nope nope");
        assertEquals(0.5f, score);
    }

    @Test
    public void testNoWordsInScore() {
        float score = bibleScoreCalculator.calculateScore("nope nope");
        assertEquals(0.0f, score);
    }

}