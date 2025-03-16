/**
 *
 * @author FireOpalus
 * @date 2024/5/22
 */
package LuceneMain;

import LuceneDependency.SGMLParser;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class LuceneBuild {
    public static void BuildIndex() throws IOException {
        // get the directory of index of Documents
        Directory directory = FSDirectory.open(Paths.get(".\\temp\\index"));
        // create analyzer and indexwriter
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        // get the Documents path
        File total_path = new File("D:\\UniversityStudy\\Grade3Term2\\信息检索\\tdt3");
        File[] list_paths = total_path.listFiles();
        if (list_paths != null) {
            for (File list_path : list_paths) {
                File[] paths = list_path.listFiles();
                if(paths != null) {
                    for (File file : paths) {
                        if(file.isFile()){
                            // create index for the document
                            Document document = new Document();
                            String file_name = file.getName();
                            Field fileNameField = new TextField("fileName", file_name, Field.Store.YES);

                            String file_path = file.getPath();
                            Field filePathField = new StoredField("filePath", file_path);

                            long file_size = FileUtils.sizeOf(file);
                            Field fileSizeField1 = new LongPoint("fileSize", file_size);
                            Field fileSizeField2 = new LongPoint("fileSize", file_size);

                            String file_content = FileUtils.readFileToString(file, "UTF-8");
                            SGMLParser sgmlParser = new SGMLParser(file_content);
                            Field fileDocNoField = new StoredField("docNo", sgmlParser.getDocNo());
                            Field fileDocTypeField = new StringField("docType", sgmlParser.getDocType(), Field.Store.YES);
                            Field fileTxtTypeField = new StringField("txtType", sgmlParser.getTxtType(), Field.Store.YES);


                            FieldType ft = new FieldType();
                            ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);// 存储
                            ft.setStored(true);
                            ft.setStoreTermVectors(true);
                            ft.setTokenized(true);
                            ft.setStoreTermVectorPositions(true);
                            ft.setStoreTermVectorOffsets(true);
                            Field fileTextField = new Field("docContent", sgmlParser.getDocContent(), ft);

                            document.add(fileNameField);
                            document.add(fileSizeField1);
                            document.add(fileSizeField2);
                            document.add(filePathField);
                            document.add(fileDocNoField);
                            document.add(fileDocTypeField);
                            document.add(fileTxtTypeField);
                            document.add(fileTextField);

                            indexWriter.addDocument(document);
                        }
                    }
                }
            }
        }

        indexWriter.close();
    }

    public static void main(String[] args) throws Exception {
        BuildIndex();
    }
}
