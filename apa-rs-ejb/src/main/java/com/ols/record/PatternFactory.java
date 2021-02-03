package com.ols.record;

import java.util.*;
import java.util.regex.Pattern;

/**
 * This class is used to create patterns to find the required fields and to check them for the correct format.
 */
public class PatternFactory {
    private static final Map<String, Set<Pattern>> patternsForType = new HashMap<>();

    private PatternFactory(){
        patternsForType.put("@book",
                new HashSet<>(Arrays.asList(
                        Pattern.compile("энциклопедия"),
                        Pattern.compile("encyclopaedia")
                )));
        patternsForType.put("@mvbook",
                new HashSet<>(Arrays.asList(
                        Pattern.compile("сборник"),
                        Pattern.compile("собрание"),
                        Pattern.compile("сочинения"),
                        Pattern.compile("работы"),
                        Pattern.compile("((в|in)\\s\\d+-?х?\\s(т|ч|vols)\\.?)$")
                )));
        patternsForType.put("@proceedings",
                new HashSet<>(Arrays.asList(
                        Pattern.compile("proceedings"),
                        Pattern.compile("of\\s*(a|the)\\s*conference"),
                        Pattern.compile("conference"),
                        Pattern.compile("proceedings\\s*of"),
                        Pattern.compile("of\\s*(a|the).*\\s*colloquium"),
                        Pattern.compile("of\\s*symposia"),
                        Pattern.compile("symposium"),
                        Pattern.compile("of\\s*(a|the)\\s*congress")
                )));
        patternsForType.put("@article",
                new HashSet<>(Arrays.asList(
                        Pattern.compile("журнал"),
                        Pattern.compile("journal"),
                        Pattern.compile("статья"),
                        Pattern.compile("article")
                )));
        patternsForType.put("@mastersthesis",
                new HashSet<>(Arrays.asList(
                        Pattern.compile("дис.*маг")
                )));
        patternsForType.put("@phdthesis",
                new HashSet<>(Arrays.asList(
                        Pattern.compile("дис.*канд")
                )));
        patternsForType.put("@techreport",
                new HashSet<>(Arrays.asList(
                        Pattern.compile("technical report")
                )));
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

    public Map<String, Set<Pattern>> getPatternsForType() {
        return patternsForType;
    }


    /**
     * This methods creates patterns that define a type of current record in method "defineType()" of class TypeDefiner
     */
}