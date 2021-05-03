package com.ols.ruslan.neo;

import java.util.*;
import java.util.regex.Pattern;

/**
 * This class is used to create patterns to find the required fields and to check them for the correct format.
 */
public class PatternFactory {
    private static final Map<RecordType, Pattern> patternsForType = new HashMap<>();

    private PatternFactory(){
        patternsForType.put(RecordType.BOOK,
                Pattern.compile("энциклопедия|encyclopa[e]?dia|сборник|собрание|сочинения|работы|книга|" +
                        "((в|in)\\s\\d+-?х?\\s(т|ч|vols)\\.?)$")); // Пример: сборник в 3 томах
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
        patternsForType.put(RecordType.MASTERSTHESIS,
                Pattern.compile(
                        "дис.*маг|выпускная квалификационная работа магистра|" +
                                "(master(s)?)?\\s*thesis\\s*((of)?\\smaster)?"));
        patternsForType.put(RecordType.PHDTHESIS,
                Pattern.compile("дис.*канд|выпускная квалификационная работа бакалавра"));

    }

    private static class PatternFactoryHolder {
        private static final PatternFactory instance = new PatternFactory();
    }

    public static PatternFactory getInstance(){
        return PatternFactoryHolder.instance;
    }
    /** For field "pages"
     * check if field matches pattern "digits-digits"
     * for example "10-20", "345-466"
     */
    public static final Pattern pagesPattern = Pattern.compile("\\D*\\d*-\\d*");
    /**
     * For field "volume"
     * check if field matches pattern like : "chapter 3", "#5", "№ 9", "том 8", "vol № 12"
     * in short it checks that field contains volume or chapter of smth
     */
    public static final Pattern volumePattern = Pattern.compile("^((том|vol|chapter|[nтpч№#]|part|часть)\\.?\\s*[нn№#]?\\s*\\d*)");
    /**
     * For field "number"
     * check if field matches pattern like : "N. 15", "number 8", "№ 9"
     * in short it checks that field is the number of journal
     */
    public static final Pattern numberPattern = Pattern.compile("^(([#№n]|number)\\.?\\s*\\d*)");

    public static final Pattern pagePattern = Pattern.compile("\\d*\\s*(pages|[pсc]|стр|страниц)\\.?");

    public Map<RecordType, Pattern> getPatternsForType() {
        return patternsForType;
    }


    /**
     * This methods creates patterns that define a type of current record in method "defineType()" of class TypeDefiner
     */
}