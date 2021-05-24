package com.ols.ruslan.neo;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
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

    private Integer getPosition(String[] array, String destination) {
        for (int i = 0; i <= array.length; i++) {
            if (Objects.equals(array[i], destination)) {
                return i;
            }
        }
        return -1;
    }
    // Изменение полей
    private void refactorFields() {
        instance.deleteRecordType();

        if (!"".equals(instance.getVolume()) && !"".equals(instance.getNumber())) instance.deleteNumber();

        instance.getAuthor().ifPresent(author -> {
            String[] allAuthors = author.split("-");
            StringBuilder builder = new StringBuilder();
            Arrays.stream(allAuthors).forEach(fullName -> {
                String[] authors = fullName.trim().split(" ");
                //Arrays.stream(authors).forEach(s -> s = s.replaceAll(",", "").trim());
                String name = authors[0].trim() + ", ";
                builder.append(name);
                Arrays.stream(authors).skip(1).forEach(str -> builder.append(str.trim()).append(" "));
                if (allAuthors.length >= 2) {
                    Integer position = getPosition(allAuthors, fullName);
                    if (position != allAuthors.length - 2) {
                        builder.append(", ");
                    } else {
                        builder.append(" & ");
                    }
                } else {
                    builder.append(" & ");
                }
            });
            if (allAuthors.length == 1) {
                instance.setAuthor(builder.toString().replaceAll("&", "").trim());
            } else {
                String result = builder.toString().trim();
                if (result.endsWith(",")) {
                    result = result.substring(0, result.length() - 1);
                }
                instance.setAuthor(result);
            }
        });


       /* // Запись вида автор1,автор2, ... авторn & авторn+1
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
        }*/
        // Год должен быть указан в ()
        instance.setYear("(" + instance.getYear() + ")");
        instance.setTitleChapter("в " + instance.getTitleChapter());
        instance.setJournal(instance.getJournal() + ", ");
        instance.setVolume("(" + getDigits(instance.getVolume()) + "),");
        instance.setAddress(instance.getAddress() + ": ");
        instance.setEditor("В " + instance.getEditor() + "(Ред.), ");
        instance.setOldType("(" + instance.getOldType() + ")");
        instance.setPages(getDigits(instance.getPages()));

        instance.getFields().entrySet().forEach(entry -> {
            String value = entry.getValue();
            if (value != null
                    && value.length() > 1
                    && !PatternFactory.specialSymbolsPattern.matcher(String.valueOf(value.charAt(value.length() - 1))).find()
                    && PatternFactory.notEmptyFieldPattern.matcher(entry.getValue()).find()) {
                entry.setValue(entry.getValue() + ". ");
            }
        });

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
        if (instance.getAuthor().isPresent()) {
            builder.append(instance.getAuthor().get())
                    .append(instance.getYear())
                    .append(instance.getTitle())
                    .append(instance.getEditor());
        } else {
            builder.append(instance.getEditor())
                    .append(instance.getYear())
                    .append(instance.getTitle());
        }
        if ("ARTICLE".equals(recordType)) {
            builder.append(instance.getJournal())
                    .append(instance.getVolume())
                    .append(getDigits(instance.getPages()));
        } else if ("BOOK".equals(recordType)) {
            builder.append(instance.getAddress())
                    .append(instance.getPublisher())
                    .append(instance.getVolume())
                    .append(instance.getPages());
        } else if ("INBOOK".equals(recordType)) {
            builder.append(instance.getTitleChapter())
                    .append(getDigits(instance.getPages()))
                    .append(instance.getAddress())
                    .append(instance.getPublisher())
                    .append(instance.getPages());
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
                .replaceAll("\\.\\s*\\.", ".")
                .replace("..", ".")
                .replaceAll(",\\s*[,.]", ",")
                .replaceAll(":\\s*[,.]", ":");
        return result;
    }
}
