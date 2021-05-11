package com.ols.ruslan.neo;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Данный класс используется для создания паттернов для того,
 * чтобы найти тип записи, если он указан явно, а также для того,
 * чтобы найти случаи нетипичной записи полей.
 */
public class PatternFactory {
    private static final Map<RecordType, Pattern> patternsForType = new HashMap<>();

    public PatternFactory(){
        patternsForType.put(RecordType.BOOK,
                Pattern.compile("энциклопедия|encyclopa[e]?dia|сборник|собрание|сочинения|работы|книга|" +
                        "(в|in)\\s\\d+-?х?\\s(т|ч|vols)\\.?$")); // Пример: сборник в 3 томах
        patternsForType.put(RecordType.PROCEEDINGS,
                Pattern.compile(
                        "proceedings|" +
                                "of\\s*(a|the)\\s*conference|конференци" +
                                "conference|proceedings\\s*of|" +
                                "of\\s*(a|the).*\\s*colloquium|колоквиум" +
                                "of\\s*symposia|symposium|" +
                                "of\\s*(a|the)\\s*congress"));
        patternsForType.put(RecordType.ARTICLE,
                Pattern.compile("журнал|journal|статья|article"));
        patternsForType.put(RecordType.THESIS,
                Pattern.compile("дис.*канд|выпускная квалификационная работа|дис.*маг|дис.*док|диссертац|" +
                        "((master(s)|bachelor)?)?\\s*thesis\\s*((of)?\\smaster(s)|bachelor)?"));
    }

    /** Для поля "pages"
     * Если поле совпадает с паттерном "digits-digits"
     * Например "10-20", "345-466"
     */
    public static final Pattern pagesPattern = Pattern.compile("\\D*\\d*-\\d*");

    /**
     * Для поля "volume"
     * Если поле совпадает с паттерном : "chapter 3", "#5", "№ 9", "том 8", "vol № 12"
     * Проверка, что поле является томом или главой
     */
    public static final Pattern volumePattern = Pattern.compile("^((том|vol|chapter|[nтpч№#]|part|часть)\\.?\\s*[нn№#]?\\s*\\d*)");

    /**
     * Для поля "number"
     * Если поле совпадает с паттерном : "N. 15", "number 8", "№ 9"
     * Проверка, что поле является номером журнала
     */
    public static final Pattern numberPattern = Pattern.compile("^(([#№n]|number)\\.?\\s*\\d*)");

    /** Для поля "pages"
     * Если поле совпадает с паттерном "digits"
     * Например "10 стр", "345 pages"
     */
    public static final Pattern pagePattern = Pattern.compile("\\d*\\s*(pages|[pсc]|стр|страниц)\\.?");

    public Map<RecordType, Pattern> getPatternsForType() {
        return patternsForType;
    }

    public static final Pattern journalPattern = Pattern.compile("журнал|journal");

    public static final Pattern specialSymbolsPattern = Pattern.compile("[:;.,\\-/\\s]");

    public static final Pattern notEmptyFieldPattern = Pattern.compile("[a-zA-Zа-яА-Я0-9]{2,}");

}