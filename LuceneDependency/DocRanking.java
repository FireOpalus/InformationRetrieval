/**
 *
 * @author FireOpalus
 * @date 2024/5/23
 */
package LuceneDependency;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.*;

public class DocRanking {
    List<String> terms = new ArrayList<String>();
    private List<ScoreDoc> myScoreDocs = new ArrayList<ScoreDoc>();
    private IndexReader reader;
//    private Map<Integer, Float> docs = new HashMap<Integer, Float>();
    private float avgContentSize;

    public DocRanking() throws IOException {
    };

    // to get terms from search command
    public void addTerms(String term) {
        this.terms.add(term);
    }

    // get the reader
    public void setReader(IndexReader reader) {
        this.reader = reader;
    }

    // function to get sorted Docs with the top-N highest BM25 scores
    private void setMyScoreDocs(ScoreDoc[] allDocs) throws IOException {
        for (ScoreDoc oneDoc : allDocs) {
            oneDoc.score = getScore(oneDoc.doc);
            this.myScoreDocs.add(oneDoc);
//            this.docs.put(oneDoc.doc, oneDoc.score);
        }

//        for (Integer doc : docs.keySet()) {
//            this.myScoreDocs.add(new ScoreDoc(doc, docs.get(doc)));
//        }
        Collections.sort(this.myScoreDocs, (o1, o2) -> {
            if(o1.score < o2.score){
                return 1;
            }
            else if(o1.score > o2.score){
                return -1;
            }
            return 0;
        });
    }

    // function to calculate avgFileSize and get the docs returned
    public List<ScoreDoc> getMyScoreDocs(ScoreDoc[] allDocs, int hits) throws IOException {
        setAvgContentSize();
        setMyScoreDocs(allDocs);
        if(myScoreDocs.size() < hits){
            return this.myScoreDocs;
        }
        else{
            return this.myScoreDocs.subList(0, hits);
        }
    }

    // function to get the frequency of the doc
    private int getTermFreq(int doc, String term0) throws IOException {
        TermVectors termVectors = reader.termVectors();
        Terms terms = termVectors.get(doc, "docContent");
        if(terms != null) {
            TermsEnum termsEnum = terms.iterator();
            BytesRef term = null;
            while((term = termsEnum.next()) != null) {
                if(term.utf8ToString().matches(term0)) {
                    return (int)termsEnum.totalTermFreq();
                }
            }
        }
        return 0;
    }

    // function to get the frequency in all docs
    private int getDocFreq(String term0) throws IOException {
        Term term = new Term("docContent", term0);
        return reader.docFreq(term);
    }

    private int getDocNum(){
        return reader.numDocs();
    }

    // function to get the size of the file
    private int getContentSize(int doc) throws IOException {
        Document document = reader.document(doc);
        Field field = (Field) document.getField("docContent");
        return field.stringValue().split(" ").length;
    }

    // function to get the average size of files
    private void setAvgContentSize() throws IOException {
        int numDocs = getDocNum();
        int totalContentSize = 0;
        for (int i = 0; i < numDocs; i++) {
            totalContentSize += getContentSize(i);
        }
        this.avgContentSize = (float) totalContentSize / numDocs;
    }

    // function to get BM25 score of one term
    private float getBM25(int doc, String term) throws IOException {
        int termFreq = getTermFreq(doc, term);
        int docFreq = getDocFreq(term);
        float contentSize = getContentSize(doc);
        float k1 = (float) 1.2;
        float b = (float) 0.75;
        float K = (float)(k1 * (1 - b + b * (double) contentSize / avgContentSize));
        float tf = termFreq * (1 + k1) / (K + docFreq);
        float idf = (float)Math.log((float)(getDocNum() - docFreq + 0.5) / (docFreq + 0.5));
        return tf * idf;
    }

    // function to get score of the doc with all terms
    private float getScore(int doc) throws IOException {
        float Bm25 = 0;
        for (String term : terms) {
            Bm25 += getBM25(doc, term);
        }
        return Bm25;
    }
}