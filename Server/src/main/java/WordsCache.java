import java.util.ArrayList;
import java.util.List;

public class WordsCache {
    List<Words> wordsCache = new ArrayList<>();

    public List<Words> addWordsCache(Words words){
        wordsCache.add(words);
        return wordsCache;
    }
    public Words getWordsCache(Words words){
        return wordsCache.get(words.getId() - 1);
    }
}
