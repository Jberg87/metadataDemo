package nl.ns.bi_automation;

import nl.ns.bi_automation.model.AppConstants;
import nl.ns.bi_automation.service.Generator;
import nl.ns.bi_automation.service.JsonParser;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Hello world!
 */

public class App {
    public static void main(String[] args) throws GeneralSecurityException, IOException {


        // Start with where all data and configuration resides
//        PathSelector pathSelector = new PathSelector();
//        AppConstants.FILES_PATH = pathSelector.selectPath();
        AppConstants.FILES_PATH = "C:\\dev\\Git\\metadataDemo\\files";

        JsonParser.loadReferenceData(AppConstants.FILES_PATH + "\\referencedata.json");
        JsonParser.loadMetadata(AppConstants.FILES_PATH + "\\metadata1.json");

        Generator.getInstance().generate();
    }
}