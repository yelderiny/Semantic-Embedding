import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SemanticMain {
    public List<String> listVocabulary = new ArrayList<>();  //List that contains all the vocabularies loaded from the csv file.
    public List<double[]> listVectors = new ArrayList<>(); //Associated vectors from the csv file.
    public List<Glove> listGlove = new ArrayList<>();
    public final List<String> STOPWORDS;

    public SemanticMain() throws IOException {
        STOPWORDS = Toolkit.loadStopWords();
        Toolkit.loadGLOVE();
    }

    public static void main(String[] args) throws IOException {
        StopWatch mySW = new StopWatch();
        mySW.start();
        SemanticMain mySM = new SemanticMain();
        mySM.listVocabulary = Toolkit.getListVocabulary();
        mySM.listVectors = Toolkit.getlistVectors();
        mySM.listGlove = mySM.CreateGloveList();

        List<CosSimilarityPair> listWN = mySM.WordsNearest("computer");
        Toolkit.PrintSemantic(listWN, 5);

        listWN = mySM.WordsNearest("phd");
        Toolkit.PrintSemantic(listWN, 5);

        List<CosSimilarityPair> listLA = mySM.LogicalAnalogies("china", "uk", "london", 5);
        Toolkit.PrintSemantic("china", "uk", "london", listLA);

        listLA = mySM.LogicalAnalogies("woman", "man", "king", 5);
        Toolkit.PrintSemantic("woman", "man", "king", listLA);

        listLA = mySM.LogicalAnalogies("banana", "apple", "red", 3);
        Toolkit.PrintSemantic("banana", "apple", "red", listLA);
        mySW.stop();

        if (mySW.getTime() > 2000)
            System.out.println("It takes too long to execute your code!\nIt should take less than 2 second to run.");
        else
            System.out.println("Well done!\nElapsed time in milliseconds: " + mySW.getTime());
    }

    /**
     * Instantiates a glove object for all the vocabularies in listVocabulary unless the word in question is in the
     * STOPWORDS list
     * @return a list of gloves
     */
    public List<Glove> CreateGloveList() {
        return listVocabulary.stream().parallel() //instantiate a stream
                .filter(word -> !STOPWORDS.contains(word)) //filter out words included in STOPWORDS
                .map(word -> new Glove(word, new Vector(listVectors.get(listVocabulary.indexOf(word))))) //creates a new glove for each word by accessing the parallel arrays for the vector
                .collect(Collectors.toList());
    }

    /**
     * Finds the vector associated with the input word in listVectors if the word is in listVocabulary. Otherwise, the vector
     * associated with the word "error" is returned
     * @param _word word to find the vector of
     * @return the vector representation of the input word
     */
    @NotNull
    private Vector getVector(String _word) {
        return new Vector(listVectors.get(!listVocabulary.contains(_word)
                ? listVocabulary.indexOf("error")
                : listVocabulary.indexOf(_word))
        );
    }

    /**
     * Returns an ordered list of cosine similarity pairs between the input word and all the words in listGlove. The
     * list is ordered by how similar the word is to the input word (how similar the vector representation of a word is
     * with the vector representation of the input word)
     * @param _word word to compare to
     * @return an ordered list of cosine similarity pairs
     */
    public List<CosSimilarityPair> WordsNearest(String _word) {
        Vector v = getVector(_word); //get the vector representation of the input word

        //call doHeapSort on the result of the stream to order the list
        return HeapSort.doHeapSort(listGlove.stream()
                .filter(glove -> !glove.getVocabulary().equals(_word)) //filter out the input word from listGlove
                .map(glove -> new CosSimilarityPair(_word, glove.getVocabulary(), v.cosineSimilarity(glove.getVector()))) //creates a new CosSimilarityPair
                .collect(Collectors.toList()));
    }

    /**
     * Returns an ordered list of cosine similarity pairs between the input vector and all the words in listGlove. The
     * list is ordered by how similar the vector representation of a word is to the input vector
     * @param _vector vector to compare to
     * @return an ordered list of cosine similarity pairs
     */
    public List<CosSimilarityPair> WordsNearest(Vector _vector) {

        //call doHeapSort on the result of the stream to order the list
        return HeapSort.doHeapSort(listGlove.stream()
                .filter(glove -> !glove.getVector().equals(_vector)) //filter out the input vector from listGlove if relevant
                .map(glove -> new CosSimilarityPair(_vector, glove.getVocabulary(), _vector.cosineSimilarity(glove.getVector()))) //creates a new CosSimilarityPair
                .collect(Collectors.toList()));
    }

    /**
     * Method to calculate the logical analogies by using references.
     * <p>
     * Example: uk is to london as china is to XXXX. _firISRef  _firTORef _secISRef In the above example, "uk" is the
     * first IS reference; "london" is the first TO reference and "china" is the second IS reference. Moreover, "XXXX"
     * is the vocabulary(ies) we'd like to get from this method.
     * <p>
     * If _top <= 0, then returns an empty listResult. If the vocabulary list does not include _secISRef or _firISRef or
     * _firTORef, then returns an empty listResult.
     * @param _secISRef The second IS reference
     * @param _firISRef The first IS reference
     * @param _firTORef The first TO reference
     * @param _top How many vocabularies to include.
     */
    public List<CosSimilarityPair> LogicalAnalogies(String _secISRef, String _firISRef, String _firTORef, int _top) {
        List<String> currentWords = List.of(_secISRef, _firISRef, _firTORef); //create a list of the input words. This is relevant for comparison

        //_top is less than or equal to 0, or listVocab doesn't have all three input words, return an empty arraylist
        if (_top <= 0 || !new HashSet<>(listVocabulary).containsAll(currentWords)) return new ArrayList<>();

        //call WordsNearest on the mathematical operation secIs - firIs + firTo to get a list of similar words
        return WordsNearest(getVector(_secISRef).subtraction(getVector(_firISRef)).add(getVector(_firTORef)))
                .stream().parallel()
                .filter(pair -> !currentWords.contains(pair.getWord2())) //filter out words containing the input words
                .limit(_top) //limit the list to the input _top
                .toList();
    }
}
