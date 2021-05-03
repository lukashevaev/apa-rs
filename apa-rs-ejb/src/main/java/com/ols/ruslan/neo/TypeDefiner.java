package com.ols.ruslan.neo;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Данный класс используется для того, чтобы определить тип записи на основании паттернов
 */
public class TypeDefiner {
    private ApaInstance instance;
    private String recordType;
    private final Map<RecordType, Pattern> patternsForType;

    public TypeDefiner(ApaInstance instance){
        PatternFactory patternFactory = PatternFactory.getInstance();
        patternsForType = patternFactory.getPatternsForType();
        this.instance = instance;
        recordType = instance.getRecordType().toLowerCase();
        defineType();
    }

    private void defineType() {
        String oldType = recordType;
        //patternsLookup
        for (Map.Entry<RecordType, Pattern> entry : patternsForType.entrySet()) {
            if (entry.getValue().matcher(oldType).find() || entry.getValue().matcher(instance.getTitle().toLowerCase()).find()) {
                recordType = "BOOK";
                break;
            }
        }

    }
    public String getRecordType(){
        return recordType;
    }
}
