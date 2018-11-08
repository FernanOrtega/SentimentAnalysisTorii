package es.us.lsi.fogallego.torii.sentimentanalysis.model;

public class Token {

    private String original;
    private String stem;
    private String tag;
    private int beginIndex;
    private int endIndex;
    private double sentimentWeight;
    private double negationWeight;
    private double boosterWeight;

    public Token(String original, String stem, String tag, int beginIndex, int endIndex, double sentimentWeight, double negationWeight, double boosterWeight) {
        this.original = original;
        this.stem = stem;
        this.tag = tag;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.sentimentWeight = sentimentWeight;
        this.negationWeight = negationWeight;
        this.boosterWeight = boosterWeight;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public void setBeginIndex(int beginIndex) {
        this.beginIndex = beginIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public double getSentimentWeight() {
        return sentimentWeight;
    }

    public void setSentimentWeight(double sentimentWeight) {
        this.sentimentWeight = sentimentWeight;
    }

    public double getNegationWeight() {
        return negationWeight;
    }

    public void setNegationWeight(double negationWeight) {
        this.negationWeight = negationWeight;
    }

    public double getBoosterWeight() {
        return boosterWeight;
    }

    public void setBoosterWeight(double boosterWeight) {
        this.boosterWeight = boosterWeight;
    }

    @Override
    public String toString() {
        return "Token{" +
                "original='" + original + '\'' +
                ", stem='" + stem + '\'' +
                ", tag='" + tag + '\'' +
                ", beginIndex=" + beginIndex +
                ", endIndex=" + endIndex +
                ", sentimentWeight=" + sentimentWeight +
                ", negationWeight=" + negationWeight +
                ", boosterWeight=" + boosterWeight +
                '}';
    }
}
