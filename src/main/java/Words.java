import java.io.Serializable;


public class Words implements Serializable, Comparable<Words> {
    private int id;
    private String wordName;
    private int wordCount;

    private String link;

    public Words(int id, String wordName, int wordCount, String link) {
        this.id = id;
        this.wordName = wordName;
        this.wordCount = wordCount;
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public String getWordName() {
        return wordName;
    }

    public int getWordCount() {
        return wordCount;
    }

    public String getLink() {
        return link;
    }

    @Override
    public int compareTo(Words o) {
        return Integer.compare(o.getWordCount(), getWordCount());
    }
}
