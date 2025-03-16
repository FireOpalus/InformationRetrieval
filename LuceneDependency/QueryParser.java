/**
 *
 * @author FireOpalus
 * @date 2024/5/23
 */
package LuceneDependency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryParser {
    private int hits;
    private List<String> keyWords = new ArrayList<>();

    public int getHits(){
        return hits;
    }
    public List<String> getKeyWords() {
        return keyWords;
    }

    public QueryParser(String query) throws Exception{
        List<String> terms = new ArrayList<>(Arrays.asList(query.split(" ")));
        // get the first word as the command
        String term0 = terms.get(0);
        // default hits is 10
        hits = 10;
        // if command is valid
        if (term0.equals("search")) {
            for(int i = 1; i < terms.size(); i++) {
                String term = terms.get(i);
                // if it's a doubly quoted phrase query, build the phrase.
                if(term.startsWith("\"")) {
                    StringBuilder phraseBuilder = new StringBuilder(term);
                    while(!term.endsWith("\"") && i < terms.size() - 1) {
                        i++;
                        term = terms.get(i);
                        phraseBuilder.append(" ").append(term);
                    }
                    // before filter the punctuations and numbers, delete the double quotation mark
                    keyWords.add(sanitizeQuery(phraseBuilder.toString().replaceAll("\"","")));
                }
                // evaluation of hits
                else if (term.startsWith("--")) {
                    if(term.startsWith("--hits==")) {
                        hits = Integer.parseInt(term.substring(7));
                    }
                    else{
                        throw new Exception("Not a valid hits");
                    }

                }
                else{
                    keyWords.add(sanitizeQuery(term));
                }
            }
        }
        else{
            throw new Exception("Not a valid query");
        }
    }

    // function to filter the punctuation and numbers
    private String sanitizeQuery(String query){
        return query.replaceAll("\\d+", "").replaceAll("\\p{Punct}", " ").toLowerCase();
    }

}
