package es.us.lsi.fogallego.torii.sentimentanalysis.nlp;

import es.us.lsi.fogallego.torii.sentimentanalysis.model.Token;
import es.us.lsi.fogallego.torii.sentimentanalysis.util.DataLoaders;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Annotators {

    private Map<String, Double> mapSentimentWeight;
    private Set<String> boosters;
    private Set<String> negations;
    private static Annotators instance;

    private Annotators(String language, String resourcesFolder, String boostersFile, String negationsFile)
            throws IOException {
        mapSentimentWeight = DataLoaders.loadSentimentWeights(language, resourcesFolder);
        boosters = DataLoaders.loadFileWithString(language, resourcesFolder, boostersFile);
        negations = DataLoaders.loadFileWithString(language, resourcesFolder, negationsFile);

    }

    public static Annotators getInstance(String language, String resourcesFolder, String boostersFile, String negationsFile) throws IOException {
        if (instance == null) {
            instance = new Annotators(language, resourcesFolder, boostersFile, negationsFile);
        }

        return instance;
    }

    public void annotate(List<Token> lstToken) {
        annotateSentimentWeightsAndSelfBoosters(lstToken);
        annotateNegationsAndBoosters(lstToken);
    }

    private void annotateSentimentWeightsAndSelfBoosters(List<Token> lstToken) {
        for (Token token : lstToken) {
            if (token.getTag().startsWith("aq")) {
                Double sentimentWeight = mapSentimentWeight.getOrDefault(token.getStem(), 0.0);
                if (sentimentWeight != 0) {
                    token.setSentimentWeight(sentimentWeight);
                    token.setBoosterWeight(getSelfBoosters(token.getOriginal()));
                }
            }
        }
    }

    private void annotateNegationsAndBoosters(List<Token> lstToken) {
        for (int i = 0; i < lstToken.size(); i++) {
            Token token = lstToken.get(i);

            if (negations.contains(token.getStem())) {
                Token influencedToken = getInfluencedToken(lstToken, i);
                if (influencedToken != null) {
                    influencedToken.setNegationWeight(token.getNegationWeight() * -1);
                }
            } else if (boosters.contains(token.getStem())) {
                Token influencedToken = getInfluencedToken(lstToken, i);
                if (influencedToken != null) {
                    influencedToken.setBoosterWeight(token.getBoosterWeight() + 1);
                }
            }
        }
    }

    private Token getInfluencedToken(List<Token> lstToken, int influencerIndex) {

        Token result;

        result = null;

        for (int i = 1; i < lstToken.size(); i++) {
            for (int j = -1; j <= 1; j+=2) {
                int candidateIndex = influencerIndex + i * j;
                if (candidateIndex >= 0 && candidateIndex < lstToken.size()) {
                    Token candidate = lstToken.get(candidateIndex);

                    if (candidate.getSentimentWeight() != 0.0) {
                        result = candidate;
                        break;
                    }
                }
            }
        }

        return result;
    }

    private double getSelfBoosters(String original) {

        double result;
        String filtered;
        filtered = original.replaceAll("[a-z]", ".");

        if ((double) filtered.length() / original.length() > 0.8
                || getMaxNumRepeatedLetters(original) >= 3) {
            result = 1;
        } else {
            result = 0.0;
        }

        return result;
    }

    private int getMaxNumRepeatedLetters(String original) {

        int maxNumRepeatedLetters;
        int currentNumRepeatedLetters;
        char lastChar;

        maxNumRepeatedLetters = 0;
        currentNumRepeatedLetters = 0;
        lastChar = '\0';

        for (int i = 0; i < original.length(); i++) {
            char c = original.charAt(i);

            if (lastChar == c) {
                currentNumRepeatedLetters++;
            } else {
                if (currentNumRepeatedLetters > maxNumRepeatedLetters) {
                    maxNumRepeatedLetters = currentNumRepeatedLetters;
                }
                currentNumRepeatedLetters = 1;
            }
            lastChar = c;
        }

        if (currentNumRepeatedLetters > maxNumRepeatedLetters) {
            maxNumRepeatedLetters = currentNumRepeatedLetters;
        }

        return maxNumRepeatedLetters;
    }

}
