/**
 *
 * @author FireOpalus
 * @date 2024/5/23
 */
package LuceneMain;

import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;
import java.util.List;

import LuceneDependency.QueryParser;
import LuceneDependency.DocRanking;

public class LuceneQuery {
    public void ProcQuery(String input) throws Exception {
        // Find directory stored index of documents
        Directory directory = FSDirectory.open(Paths.get(".\\temp\\index"));
        // Struct the Reader and Searcher
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        DocRanking docRanking = new DocRanking();

        // BooleanQueryBuilder for multi-keyword query
        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
        // QueryParser can parse the query into hits and keywords
        QueryParser queryParser = new QueryParser(input);
        int maxHit = queryParser.getHits();
        List<String> keyWords = queryParser.getKeyWords();
        for (String keyWord : keyWords) {
            // If keyword has " ", it means that this is a Doubly quoted phrase query, and we use PhraseQuery.
            if(keyWord.contains(" ")) {
                String[] words = keyWord.split(" ");
                PhraseQuery.Builder phraseQueryBuilder = new PhraseQuery.Builder();
                // setSlop(0) to make words adjoining
                phraseQueryBuilder.setSlop(0);
                for (String word : words) {
                    phraseQueryBuilder.add(new Term("docContent", word));
                    docRanking.addTerms(word);
                }
                PhraseQuery phraseQuery = phraseQueryBuilder.build();
                // the Document only can be selected when every keyword can be found in it
                booleanQueryBuilder.add(phraseQuery, BooleanClause.Occur.SHOULD);
            }
            else {
                Query query = new TermQuery(new Term("docContent", keyWord));
                booleanQueryBuilder.add(query, BooleanClause.Occur.SHOULD);
                docRanking.addTerms(keyWord);
            }
        }

        BooleanQuery booleanQuery = booleanQueryBuilder.build();
        // get the Documents with the Top-N highest scores
        TopDocs topDocs = indexSearcher.search(booleanQuery, 10000);
        ScoreDoc[] allDocs = topDocs.scoreDocs;
        // when maxHit larger than the length of scoreDocs, the real hits is equal to the length
        System.out.println("Total hits:" + Math.min(allDocs.length, maxHit));
        docRanking.setReader(indexReader);
        List<ScoreDoc> scoreDocs = docRanking.getMyScoreDocs(allDocs, maxHit);
        // rank for the documents
        int rank = 0;
        for (ScoreDoc scoreDoc : scoreDocs) {
            rank++;
            int doc = scoreDoc.doc;
            // get the score
            float score = scoreDoc.score;
            System.out.print(rank + " [" + score + "] ");
            Document document = indexSearcher.doc(doc);
            String fileDocNo = document.get("docNo");
            System.out.println(fileDocNo);
            String fileContent = document.get("docContent");
            // if the Document is too long, an abstract which length is 500 will be printed instead of itself
            if (fileContent.length() > 500) {
                System.out.println(fileContent.substring(0, 500) + "...");
            }
            else {
                System.out.println(fileContent);
            }
            // print a line to split two documents
            System.out.println("----------------------------------------------------------------------");
        }

        indexReader.close();
    }
}