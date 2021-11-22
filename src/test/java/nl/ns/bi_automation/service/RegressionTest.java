package nl.ns.bi_automation.service;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.ns.bi_automation.Utils.Utils;

// import static org.junit.jupiter.api.Assertions.*;

class RegressionTest {

    Utils utils = new Utils();

    private boolean setUpIsDone = false;

    @BeforeEach
    void setUp() {
        if (!setUpIsDone) {
            JsonParser.loadReferenceData("referencedata.json");
            JsonParser.loadMetadata("metadata1.json");
            Generator.getInstance().generate();
            setUpIsDone = true;
        }
    }

    @Test
    void getPipelineById() {
    }

    @Test
    void assertTestReferenceFileNotPresent() throws IOException {
        utils.assertFileExistAndEqualsReference("C:\\dev\\Git\\metadataDemo\\src\\test\\resources\\output\\",
                "C:\\dev\\Git\\metadataDemo\\zz_results\\", "RDV mapping Persoon_H_DD.xml");
    }

    @Test
    void assertTestGeneratedFileNotPresent() throws IOException {
        utils.assertFileExistAndEqualsReference("C:\\dev\\Git\\metadataDemo\\src\\test\\resources\\output\\",
                "C:\\dev\\Git\\metadataDemo\\zz_results\\", "RDV mapping Persoon_H_666.xml");
    }

    @Test
    void assertTestXmlAreSimilar() throws IOException {
        utils.assertFileExistAndEqualsReference("C:\\dev\\Git\\metadataDemo\\src\\test\\resources\\output\\",
                "C:\\dev\\Git\\metadataDemo\\zz_results\\", "RDV mapping Persoon_H.xml");
    }

    @Test
    void assertTestXmlAreNotSimilar() throws IOException {
        utils.assertFileExistAndEqualsReference("C:\\dev\\Git\\metadataDemo\\src\\test\\resources\\output\\",
                "C:\\dev\\Git\\metadataDemo\\zz_results\\", "RDV mapping Persoon_S_DD.xml");
    }
}