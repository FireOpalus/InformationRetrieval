/**
 *
 * @author FireOpalus
 * @date 2024/5/22
 */
package LuceneDependency;

public class SGMLParser {
    private String docNo;
    private String docType;
    private String txtType;
    private String docContent;

    public String getDocNo() {
        return docNo;
    }
    public String getDocType() {
        return docType;
    }
    public String getTxtType() {
        return txtType;
    }
    public String getDocContent() {
        return docContent;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }
    public void setDocType(String docType) {
        this.docType = docType;
    }
    public void setTxtType(String txtType) {
        this.txtType = txtType;
    }
    public void setDocContent(String docContent) {
        this.docContent = docContent;
    }

    public SGMLParser(String fileContent) {
        int docNo1 = fileContent.indexOf("<DOCNO>");
        int docNo2 = fileContent.indexOf("</DOCNO>");
        int docType1 = fileContent.indexOf("<DOCTYPE>");
        int docType2 = fileContent.indexOf("</DOCTYPE>");
        int txtType1 = fileContent.indexOf("<TXTTYPE>");
        int txtType2 = fileContent.indexOf("</TXTTYPE>");
        int Text1 = fileContent.indexOf("<TEXT>");
        int Text2 = fileContent.indexOf("</TEXT>");

        this.docNo = fileContent.substring(docNo1 + 7, docNo2).trim();
        this.docType = fileContent.substring(docType1 + 9, docType2).trim();
        this.txtType = fileContent.substring(txtType1 + 9, txtType2).trim();
        this.docContent = fileContent.substring(Text1 + 6, Text2).trim();
    }
}
