package com.ols.record;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;

public class ApaBuilder {
    private String recordType;
    private final Map<String, String> fields;

    public ApaBuilder(Map<String, String> fields) throws Exception {
        this.fields = fields;
        //recordType = fields.get("recordType");
        TypeDefiner typeDefiner = new TypeDefiner(fields);

        setRecordType(typeDefiner.getRecordType());
        refactorFields();
    }

    private void setRecordType(String recordType){
        this.recordType = recordType;
    }

    private void refactorFields() {

    }

    public Document build() {
        String delimiter = ",";
        Document document = Jsoup.parse("<html></html>");
        document.body().appendText(fields.get("author")).appendText("(")
                .appendText(fields.get("year")).appendText(")")
                .appendElement("i").appendText(fields.get("title"));
        document.body()
                .appendText(fields.get("publisher")).appendText(delimiter)
                .appendText(fields.get("address")).appendText(delimiter)
                .appendText(fields.get("journal"));
        return  document;
    }
}
