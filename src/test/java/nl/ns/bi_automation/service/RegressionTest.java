package nl.ns.bi_automation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegressionTest {


    private boolean setUpIsDone = false;
    @BeforeEach
    void setUp() {
        JsonParser.loadReferenceData("referencedata.json");
        JsonParser.loadMetadata("metadata1.json");
        Generator.getInstance().generate();
        setUpIsDone = true;
    }

    @Test
    void getPipelineById() {
    }
}