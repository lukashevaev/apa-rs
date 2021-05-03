package com.ols.ruslan.neo;

import java.util.Arrays;
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

    // Метод для выделения цифр из поля
    public String getDigits(String field) {
        return field.replaceAll("[^0-9]", "");
    }
    // Изменение полей
    private void refactorFields() {

        // Убираем разделитель после последнего упомянутого автора
        if (!instance.getAuthor().equals("")){
            String author = instance.getAuthor();
            instance.setAuthor(author.substring(0, author.length() - 1));
        }

        // Запись вида автор1,автор2, ... авторn & авторn+1
        if (!instance.getAuthor().equals("")) {
            String[] authors = instance.getAuthor().split("-");
            switch (authors.length) {
                case 1: {
                    instance.setAuthor(authors[0].substring(0, authors[0].length() - 1));
                    break;
                }
                case 2: {
                    instance.setAuthor(authors[0].substring(0, authors[0].length() - 1) + " & " + authors[1].substring(0, authors[1].length() - 1));
                    break;
                }
                default: {
                    StringBuilder author = new StringBuilder();
                    Arrays.stream(authors).forEach(author::append);
                    author.replace(author.lastIndexOf(","), author.lastIndexOf(",") + 1, "");
                    author.replace(author.lastIndexOf(","), author.lastIndexOf(",") + 1, " & ");
                    instance.setAuthor(author.toString());
                    break;
                }
            }
        }
        // Год должен быть указан в ()
        instance.setYear("(" + instance.getYear() + ")");
    }

    public String  buildApa() {
        StringBuilder builder = new StringBuilder();
        Map<String, String> fields = instance.getFields();
        fields.entrySet().forEach(entry -> entry.setValue(entry.getValue() + ". "));
        // Для следующих типов название должно быть в английских кавычках до названия источника
        if ("INPROCEEDINGS".equals(recordType)
                || "ARTICLE".equals(recordType)
                || "PHDTHESIS".equals(recordType)
                || "MASTERSTHESIS".equals(recordType)
        ) instance.setTitle("\"" + instance.getTitle() + "\"");
        if (!instance.getAuthor().equals("")) {
            builder.append(instance.getAuthor())
                    .append(instance.getYear())
                    .append(instance.getTitle());
        } else {
            builder.append(instance.getTitle())
                    .append(instance.getYear());
        }
        if ("ARTICLE".equals(recordType)) {
            builder.append(instance.getJournal());
            builder.append(instance.getVolume());
            builder.append(instance.getPages());
        } else if ("BOOK".equals(recordType)) {
            builder.append(instance.getPublisher());
            builder.append(instance.getAddress());
        } else if ("INBOOK".equals(recordType)) {
            instance.setTitleChapter("в " + instance.getTitleChapter());
            builder.append(instance.getTitleChapter());
            builder.append(instance.getPublisher());
            builder.append(instance.getAddress());
            builder.append(instance.getPages());
        }
        else if ("PHDTHESIS".equals(recordType)) {
            builder.append("Abstract of bachelor dissertation");
            builder.append(instance.getUniversity());
            builder.append(instance.getAddress());
        } else if ("MASTERSTHESIS".equals(recordType)) {
            builder.append("Abstract of master dissertation");
            builder.append(instance.getUniversity());
            builder.append(instance.getAddress());
        } else if ("PROCEEDINGS".equals(recordType)) {
            builder.append(instance.getConference());
            builder.append(instance.getAddress());
            builder.append(instance.getData());
            builder.append(instance.getPages());
        }
        // Убираем лишние символы из вывода
        builder.trimToSize();
        builder.deleteCharAt(builder.length() - 2);
        return builder.toString().replace("..", ".");
    }
}
