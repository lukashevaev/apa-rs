package com.ols.ruslan.neo;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ApaBuilder {
    private final String recordType;
    private final ApaInstance instance;

    public ApaBuilder(Map<String, String> fields) {
        this.instance = new ApaInstance(fields);
        TypeDefiner typeDefiner = new TypeDefiner(instance);
        this.recordType = typeDefiner.getRecordType();
        refactorFields();
    }

    // Метод для выделения цифр из поля
    public String getDigits(String field) {
        if (field == null) {
            return "";
        }
        return field.replaceAll("[^0-9-]", "");
    }
    // Изменение полей
    private void refactorFields() {
        instance.deleteRecordType();

        if (!"".equals(instance.getVolume()) && !"".equals(instance.getNumber())) instance.deleteNumber();

        // Запись вида автор1,автор2, ... авторn & авторn+1
        if (!instance.getAuthor().equals("")) {
            String[] authors = instance.getAuthor().split("-");
            switch (authors.length) {
                case 1: {
                    instance.setAuthor(authors[0]);
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
        instance.setTitleChapter("в " + instance.getTitleChapter());
        instance.setJournal(instance.getJournal() + ", ");
        instance.setVolume("(" + instance.getVolume() + "),");
        instance.setAddress(instance.getAddress() + ": ");
        instance.setEditor("В " + instance.getEditor() + "(Ред.), ");
        instance.setOldType("(" + instance.getOldType() + ")");


        instance.getFields().entrySet().forEach(entry -> entry.setValue(entry.getValue() + ". "));
        //Удаляем пустые поля
        instance.setFields(
                instance.getFields()
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() != null && !entry.getValue().equals("") && PatternFactory.notEmptyFieldPattern.matcher(entry.getValue()).find())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue , (a, b) -> a, LinkedHashMap::new)));
    }

    public String  buildApa() {
        StringBuilder builder = new StringBuilder();
//        // Для следующих типов название должно быть в английских кавычках до названия источника
        if (!instance.getAuthor().equals("")) {
            builder.append(instance.getAuthor())
                    .append(instance.getYear())
                    .append(instance.getTitle())
                    .append(instance.getEditor());
        } else {
            builder.append(instance.getEditor())
                    .append(instance.getTitle())
                    .append(instance.getYear());
        }
        if ("ARTICLE".equals(recordType)) {
            builder.append(instance.getJournal())
                    .append(instance.getVolume())
                    .append(getDigits(instance.getPages()));
        } else if ("BOOK".equals(recordType)) {
            builder.append(instance.getVolume())
                    .append(instance.getAddress())
                    .append(instance.getPublisher());
        } else if ("INBOOK".equals(recordType)) {
            builder.append(instance.getTitleChapter())
                    .append(getDigits(instance.getPages()))
                    .append(instance.getAddress())
                    .append(instance.getPublisher());
        } else if ("THESIS".equals(recordType)) {
            builder.append(instance.getOldType())
                    .append(instance.getUniversity())
                    .append(instance.getAddress())
                    .append(instance.getUniversity());
        } else if ("PROCEEDINGS".equals(recordType)) {
            builder.append(instance.getEditor())
                    .append(instance.getAddress())
                    .append(instance.getPublisher())
                    .append(instance.getPages());
            // -(series; number).
        } else if ("INPROCEEDINGS".equals(recordType)) {
            builder.append(instance.getEditor())
                    .append(instance.getTitleChapter())
                    .append(instance.getAddress())
                    .append(instance.getPublisher())
                    .append(instance.getPages());
            //  -(series; number).
        } else {
            builder = new StringBuilder();
            instance.getFields().values().forEach(builder::append);
        }
        // Убираем лишние символы из вывода
        builder.trimToSize();
        String[] words = builder.toString().split(" ");
        String field = null;
        for (int i = words.length - 1; i >= 0; i--) {
            field = words[i];
            if (PatternFactory.notEmptyFieldPattern.matcher(field).find() && field.length() > 1) {
                break;
            }
        }
        String result = builder.toString();
        if (field != null) return builder
                .substring(0, result.lastIndexOf(field) + field.length())
                .replaceAll("\\.\\s*[a-zA-Zа-яА-Я]?\\s*\\.", ".")
                .replaceAll(",\\s*[,.]", ",")
                .replaceAll(":\\s*[,.]", ":");
        return result;
    }
}
