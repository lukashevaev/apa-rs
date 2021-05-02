package com.ols.ruslan.neo;

import java.util.Map;

public class ApaBuilder {
    private String recordType;
    private ApaInstance instance;

    public ApaBuilder(Map<String, String> fields) {
        this.instance = new ApaInstance(fields);
        TypeDefiner typeDefiner = new TypeDefiner(instance);
        this.recordType = typeDefiner.getRecordType();
        refactorFields();
    }

    private void refactorFields() {
        if (!instance.getAuthor().equals("")){
            String author = instance.getAuthor();
            instance.setAuthor(author.substring(0, author.length() - 3));
        }
    }

    public String  buildApa() {
        StringBuilder builder = new StringBuilder();
        Map<String, String> fields = instance.getFields();
        fields.entrySet().forEach(entry -> entry.setValue(entry.getValue() + ", "));
        builder.append(instance.getAuthor())
                .append(instance.getYear())
                .append(instance.getTitle());
        if ("ARTICLE".equals(recordType)) {
            builder.append(instance.getJournal());
            builder.append(instance.getVolume());
            builder.append(instance.getPages());
        } else if ("BOOK".equals(recordType)) {
            builder.append(instance.getPublisher());
            builder.append(instance.getAddress());
        } else if ("PHDTHESIS".equals(recordType)) {
            builder.append("Abstract of phdthesis dissertation");
            builder.append(instance.getUniversity());
            builder.append(instance.getAddress());
        } else if ("MASTERSTHESIS".equals(recordType)) {
            builder.append("Abstract of mastersthesis dissertation");
            builder.append(instance.getUniversity());
            builder.append(instance.getAddress());
        } else if ("PROCEEDINGS".equals(recordType)) {
            builder.append(instance.getConference());
            builder.append(instance.getAddress());
            builder.append(instance.getData());
            builder.append(instance.getPages());
        }
        builder.trimToSize();
        builder.deleteCharAt(builder.length() - 2);
        return builder.toString().replace(",,", ",");
    }
}
