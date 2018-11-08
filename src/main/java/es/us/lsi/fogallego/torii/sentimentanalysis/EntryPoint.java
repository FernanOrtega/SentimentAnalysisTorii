package es.us.lsi.fogallego.torii.sentimentanalysis;

import es.us.lsi.fogallego.torii.sentimentanalysis.model.Sentence;
import es.us.lsi.fogallego.torii.sentimentanalysis.model.Token;
import es.us.lsi.fogallego.torii.sentimentanalysis.nlp.Annotators;
import es.us.lsi.fogallego.torii.sentimentanalysis.util.DataLoaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class EntryPoint {

    private static final String BOOSTERS_FILE = "boosters.txt";
    private static final String NEGATIONS_FILE = "negations.txt";

    public static void main(String[] args) throws IOException {

        if (args.length != 5) {
            System.err.println("Wrong number of parameters");
            System.err.println("Usage: EntryPoint <language> <domain> <sentences file> <resources root folder> <num samples>");
        } else if(!Files.exists(Paths.get(args[2]))) {
            System.err.println("File " + args[2] + " doesn't exist.");
        } else if(!Files.exists(Paths.get(args[3]))) {
            System.err.println("File " + args[3] + " doesn't exist.");
        } else if(!isInteger(args[4]) && Integer.parseInt(args[4]) <= 0) {
            System.err.println("Num samples " + args[4] + " wrong.");
        } else {
            computeSentiment(args[0], args[1], args[2], args[3], Integer.parseInt(args[4]));
        }
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException | NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    private static void computeSentiment(String language, String domain, String sentencesFile, String resourcesFolder,
                                         Integer numSamples) throws IOException {

        List<Sentence> sentences;

        // Load dictionaries
        Annotators annotators = Annotators.getInstance(language, resourcesFolder, BOOSTERS_FILE, NEGATIONS_FILE);

        // Load sentences and compute the token's tuple
        sentences = DataLoaders.loadSentences(language, domain, sentencesFile, numSamples);

        assert sentences != null;
        for (int i = 0; i < sentences.size(); i++) {
            Sentence sentence = sentences.get(i);
            // For each token, annotate sentiment, negation and boosters.
            annotators.annotate(sentence.getLstToken());
            // Compute sentiment at aspect-level
            System.out.println(i+": " + sentence.getText() + ", " + getSentiment(sentence.getLstToken()));
        }
    }

    private static double getSentiment(List<Token> lstToken) {

        double max;
        double min;

        max = 0;
        min = 0;

        for (Token token : lstToken) {
            double score = token.getSentimentWeight() * token.getNegationWeight();
            if (score > 0) {
                score += token.getBoosterWeight();
                if (score > max) {
                    max = score;
                }
            } else if (score < 0) {
                score -= token.getBoosterWeight();
                if (score < min) {
                    min = score;
                }
            }
        }

        return max + min;
    }

}
