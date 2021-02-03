package com.ols.record;


import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;


public class Main {

    public static void main(String[] args) throws Exception {
        //preparing rusmarc.xml for reading
        File file = new File("C:\\Users\\Daniel\\Desktop\\bibtex-rs\\bibtex-rs-ejb\\src\\main\\resources\\RUSMARC.xml");
        FileReader fileReader = new FileReader(file);
        InputSource inputSourceFile = new InputSource(fileReader);

        //creating document for transformed(by xsl) rusmarc.xml
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(inputSourceFile);

        ApaRecordSchema apaRecordSchema = new ApaRecordSchema();
        ApaRecordSchema.class.getDeclaredMethod("init",  new Class[]{}).invoke(apaRecordSchema);
        System.out.println(apaRecordSchema.transformSchema(document));
    }
}
