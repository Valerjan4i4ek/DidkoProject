public class WordsAndLinks {
    private String link;
    private StringBuffer stringBuffer;

    public WordsAndLinks(String link, StringBuffer stringBuffer) {
        this.link = link;
        this.stringBuffer = stringBuffer;
    }

    public String getLink() {
        return link;
    }

    public StringBuffer getStringBuffer() {
        return stringBuffer;
    }
}
