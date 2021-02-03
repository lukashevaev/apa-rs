package com.ols.record;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class TypeDefiner {
    private final Map<String, String> fields;
    private String recordType;
    private final Map<String, Set<Pattern>> patternsForType;

    public TypeDefiner(Map<String, String> fields){
        PatternFactory patternFactory = PatternFactory.getInstance();
        patternsForType = patternFactory.getPatternsForType();
        this.fields = fields;
        recordType = fields.get("recordType").toLowerCase();
        defineType();
    }

    private void defineType() {


    }
    public String getRecordType(){
        return recordType;
    }
}
