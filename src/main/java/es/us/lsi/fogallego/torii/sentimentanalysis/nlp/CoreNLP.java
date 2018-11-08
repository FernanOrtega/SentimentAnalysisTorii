package es.us.lsi.fogallego.torii.sentimentanalysis.nlp;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import es.us.lsi.fogallego.torii.sentimentanalysis.model.Token;
import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.SpanishStemmer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter of Stanford CoreNLP
 */
public class CoreNLP {

    static MaxentTagger maxentTagger;

    static {
        maxentTagger = new MaxentTagger("edu/stanford/nlp/models/pos-tagger/spanish/spanish.tagger");
    }

    public static List<Token> tokenise(String pText, boolean fixPeriod) {
        List<Token> lstTokens = new ArrayList<>();
        String text = pText;

        if (!pText.endsWith(".")) {
            text += " .";
        }

//        String extraOptions = "normalizeParentheses=false";
        String extraOptions = "ptb3Escaping=false";
        DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
        TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.coreLabelFactory(extraOptions);
        tokenizer.setTokenizerFactory(tokenizerFactory);

        for (List<HasWord> sentence : tokenizer) {

            List<CoreLabel> sentenceCoreLabel = sentence.stream()
                    .map(hasWord -> (CoreLabel) hasWord).collect(Collectors.toList());

            maxentTagger.tagCoreLabels(sentenceCoreLabel);

            List<Token> lstTokenPartial = sentenceCoreLabel.stream().map(coreLabel ->
                    new Token(coreLabel.word(), stem(coreLabel.word().toLowerCase()), coreLabel.tag(),
                    coreLabel.beginPosition(), coreLabel.endPosition(),0.0,
                    1.0, 0.0)).collect(Collectors.toList());

            lstTokens.addAll(lstTokenPartial);
        }

        if (!pText.endsWith(".") && !fixPeriod) {
            lstTokens.remove(lstTokens.size() - 1);
        }

        return lstTokens;
    }

    private static String stem(String word) {
        SnowballProgram stemmer = new SpanishStemmer();
        stemmer.setCurrent(word);
        stemmer.stem();
        stemmer.stem();
        stemmer.stem();

        return stemmer.getCurrent();
    }
}
