import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class Toolkit {
    private static List<String> listVocabulary = null;
    private static List<double[]> listVectors = null;
    private static final String FILENAME_GLOVE = "glove.6B.50d_Reduced.csv";
    private static final String FILENAME_STOPWORDS = "stopwords.csv";

    /**
     * Loads the gloves from the relevant file and populates the listVectors and listVocabulary lists
     * @throws IOException thrown if the file uri cannot be parsed properly
     */
    public static void loadGLOVE() throws IOException {
        try (BufferedReader myReader = new BufferedReader(new FileReader(getFileFromResource(FILENAME_GLOVE)))) {

            //instantiate the lists we must populate. This also clears them if they were populated before
            listVectors = new ArrayList<>();
            listVocabulary = new ArrayList<>();

            //iterate using lines()
            myReader.lines()
                    .map(line -> new StringTokenizer(line, ",")) //map every line to a StringTokenizer
                    .forEach(tokenizer -> {
                        double[] vector = new double[50]; //create a new double array for the vector numbers

                        listVocabulary.add(tokenizer.nextToken()); //add the first token to the vocabulary list

                        //iterate and parse the rest of the tokens, adding them to the vector array
                        for (int i = 0; i < vector.length; i++) vector[i] = Double.parseDouble(tokenizer.nextToken());

                        listVectors.add(vector); //add the vector array to the greater list of vectors
                    });

        } catch (URISyntaxException throwables) { throw new RuntimeException(throwables); }
    }

    /**
     * Loads the stopwords from the relevant file and returns them in a list
     * @return list of stop words
     * @throws IOException thrown if the file uri cannot be parsed properly
     */
    public static List<String> loadStopWords() throws IOException {
        try (BufferedReader myReader = new BufferedReader(new FileReader(getFileFromResource(FILENAME_STOPWORDS)))) {

            return myReader.lines().collect(Collectors.toList()); //collect with lines() since nothing else is needed

        } catch (URISyntaxException e) { throw new RuntimeException(e); }
    }

    private static File getFileFromResource(String fileName) throws URISyntaxException {
        ClassLoader classLoader = Toolkit.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);

        if (resource == null) throw new IllegalArgumentException("file not found! " + fileName);
        else return new File(resource.toURI());
    }

    public static List<String> getListVocabulary() { return listVocabulary; }

    public static List<double[]> getlistVectors() { return listVectors; }

    /**
     * DO NOT MODIFY Method to print out the semantic information.
     * <p>
     * Example: uk is to london as china is to XXXX. _firISRef  _firTORef _secISRef In the above example, "uk" is the
     * first IS reference; "london" is the first TO reference and "china" is the second IS reference.
     * @param _secISRef The second IS reference
     * @param _firISRef The first IS reference
     * @param _firTORef The first TO reference
     * @param _list The CosSimilarityPair list
     */
    public static void PrintSemantic(String _secISRef, String _firISRef, String _firTORef,
                                     List<CosSimilarityPair> _list) {
        System.out.println("=============================");
        System.out.printf("Identifying the logical analogies of %s (use %s and %s as a reference).\r\n", _secISRef,
                _firISRef, _firTORef);
        System.out.printf("%s is to %s as %s is to %s.\r\nOther options include:\r\n", _firISRef, _firTORef,
                _secISRef, _list.get(0).getWord2());

        for (int i = 1; i < _list.size(); i++)
            System.out.println(_list.get(i).getWord2() + ", " + _list.get(i).getCosineSimilarity());

        System.out.println("=============================");
    }

    /**
     * DO NOT MODIFY
     * @param _listCosineSimilarity The CosSimilarityPair list.
     * @param _top How many vocabularies to print.
     */
    public static void PrintSemantic(List<CosSimilarityPair> _listCosineSimilarity, int _top) {
        if (_listCosineSimilarity.size() > 0) {
            System.out.println("============" + _listCosineSimilarity.get(0).getWord1() + "============");
            System.out.println("The nearest words are:");

            for (int i = 0; i < _top; i++)
                System.out.printf("%s,%.5f\r\n", _listCosineSimilarity.get(i).getWord2(),
                        _listCosineSimilarity.get(i).getCosineSimilarity());

        } else System.out.println("The specified word doesn't exist in the vocabulary.");
    }
}
