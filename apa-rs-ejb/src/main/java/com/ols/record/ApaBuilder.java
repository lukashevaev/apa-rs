package com.ols.record;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;

public class ApaBuilder {
    private String recordType;
    private final Map<String, String> fields;

    public ApaBuilder(Map<String, String> fields) {
        this.fields = fields;
        TypeDefiner typeDefiner = new TypeDefiner(fields);
        this.recordType = typeDefiner.getRecordType();
        refactorFields();
    }

    private void refactorFields() {

    }

    public Document buildApa() {
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
