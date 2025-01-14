package org.unece.uncefact.vocab;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;

import javax.json.*;
import java.io.*;
import java.util.*;

public abstract class Transformer {

    public static String ID = "@id";
    public static String TYPE = "@type";
    public static String VALUE = "@value";
    public static String LANGUAGE = "@language";
    public static String OWL_NS = "owl";
    public static String RDFS_NS = "rdfs";
    public static String RDF_NS = "rdf";
    public static final String UNECE_NS = "unece";
    public static String XSD_NS = "xsd";
    public static String RDFS_CLASS = RDFS_NS+":Class";
    public static String RDF_PROPERTY = RDF_NS+":Property";
    public static String RDF_VALUE = RDF_NS+":value";
    public static String RDFS_COMMENT = RDFS_NS+":comment";
    public static String RDFS_LABEL = RDFS_NS+":label";
    public static String SCHEMA_NS = "schema";
    public static String SCHEMA_DOMAIN_INCLUDES = SCHEMA_NS+":domainIncludes";
    public static String SCHEMA_RANGE_INCLUDES = SCHEMA_NS+":rangeIncludes";
    protected String inputFile;
    protected String defaultFile;
    protected Set<String> inputFiles;
    protected Set<String> defaultInputFiles;

    protected List<JSONLDVocabulary> vocabularies = new ArrayList<>();
    protected List<JSONLDContext> contexts = new ArrayList<>();

    public static final String UNLOCODE_NS = "unlcd";
    public static final String UNLOCODE_SUBDIVISIONS_NS = "unlcds";
    public static final String UNLOCODE_COUNTRIES_NS = "unlcdc";
    public static final String UNLOCODE_VOCAB_NS = "unlcdv";
    protected static final String UNLOCODE_FUNC_NS = "unlcdf";
    public static final String GEO_NS = "geo";
    public static final String DOMAIN = "vocabulary.uncefact.org";


    protected static Map<String, String> NS_MAP = new HashMap<>();

    {
        NS_MAP.put(GEO_NS, "http://www.w3.org/2003/01/geo/wgs84_pos#");
        NS_MAP.put(XSD_NS, "http://www.w3.org/2001/XMLSchema#");
        NS_MAP.put(SCHEMA_NS, "http://schema.org/");
        NS_MAP.put(UNLOCODE_NS, String.format("https://%s/unlocode#", DOMAIN));
        NS_MAP.put(UNLOCODE_COUNTRIES_NS, String.format("https://%s/unlocode-countries#", DOMAIN));
        NS_MAP.put(UNLOCODE_SUBDIVISIONS_NS, String.format("https://%s/unlocode-subdivisions#", DOMAIN));
        NS_MAP.put(UNLOCODE_FUNC_NS, String.format("https://%s/unlocode-functions#", DOMAIN));
        NS_MAP.put(UNLOCODE_VOCAB_NS, String.format("https://%s/unlocode-vocab/", DOMAIN));
        NS_MAP.put(RDF_NS, "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        NS_MAP.put(RDFS_NS, "http://www.w3.org/2000/01/rdf-schema#");
        NS_MAP.put(OWL_NS, "http://www.w3.org/2002/07/owl#");
    }


    protected Transformer(String inputFile) {
        this.inputFile = inputFile;

    }

    public Transformer(String inputFile, String defaultFile) {
        this.inputFile = inputFile;
        this.defaultFile = defaultFile;
    }


    protected JsonObjectBuilder getContext (){
        JsonObjectBuilder contextObjectBuilder = Json.createObjectBuilder();
        //common context for all vocabularies
        for (String ns : Arrays.asList(UNECE_NS, RDF_NS, RDFS_NS)){
            contextObjectBuilder.add(ns, NS_MAP.get(ns));
        }
        return contextObjectBuilder;
    }

    protected JsonObjectBuilder getMinimalContext (){
        //common context for all vocabularies
        return Json.createObjectBuilder().add(RDFS_NS, NS_MAP.get(RDFS_NS));
    }

    public void transform() throws IOException, InvalidFormatException {
        FileGenerator fileGenerator = new FileGenerator();
        for (JSONLDVocabulary JSONLDVocabulary : vocabularies) {
            fileGenerator.generateFile(JSONLDVocabulary.getContextObjectBuilder(), JSONLDVocabulary.getGraphJsonArrayBuilder(), JSONLDVocabulary.isPrettyPrint(), JSONLDVocabulary.getOutputFile());
        }

        for (JSONLDContext  jsonldContext: contexts) {
            fileGenerator.generateFile(jsonldContext.getContextObjectBuilder(), null, jsonldContext.isPrettyPrint(), jsonldContext.getOutputFile());
        }
    }

    protected abstract void readInputFileToGraphArray(Object object);

    protected void setInputFiles(Set<String> inputFiles){
        this.inputFiles = inputFiles;
    }

    public void setDefaultInputFiles(Set<String> defaultInputFiles) {
        this.defaultInputFiles = defaultInputFiles;
    }
}
