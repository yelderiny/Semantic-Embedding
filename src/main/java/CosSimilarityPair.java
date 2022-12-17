public class CosSimilarityPair {
    private String strWord1;
    private String strWord2;
    private double doubCS;
    private Vector vecVector;

    public CosSimilarityPair(String _word1, String _word2, double _cosinesimilarity) {
        strWord1 = _word1;
        strWord2 = _word2;
        doubCS = _cosinesimilarity;
    }

    public CosSimilarityPair(Vector _vector, String _word2, double _cosinesimilarity) {
        vecVector = _vector;
        strWord2 = _word2;
        doubCS = _cosinesimilarity;
    }

    public String getWord1() { return strWord1; }
    public String getWord2() { return strWord2; }
    public double getCosineSimilarity() { return doubCS; }
    public Vector getVector() { return vecVector; }

    public void setWord1(String _word) { strWord1 = _word; }
    public void setWord2(String _word) { strWord2 = _word; }
    public void setCosineSimilarity(double _cs) { doubCS = _cs; }
    public void setVector(Vector _vector) { vecVector = _vector; }
}
