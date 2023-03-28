import java.util.ArrayList;
import java.util.List;

public class WordsCache {
    List<Words> wordsCache = new ArrayList<>();

    public List<Words> addWordsCache(Words words){
        wordsCache.add(words);

        return wordsCache;
    }
    public List<Words> replaceWordsCache(Words words){
        int index = 0;
        for(Words word : wordsCache){
            if(word.getWordName().equals(words.getWordName())){
                index = wordsCache.indexOf(word);
            }
        }
        wordsCache.remove(index);
        wordsCache.add(words);


        return wordsCache;
    }

    public List<Words> getListCache(){
        return wordsCache;
    }
//    public Words getWordsCache(Words words){
//        return wordsCache.get(words.getId() - 1);
//    }

    public Words getWordsCache(String word){
        for(Words words : wordsCache){
            if(words.getWordName().equals(word)){
                return words;
            }
        }
        return null;
    }
}
