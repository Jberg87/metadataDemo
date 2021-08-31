package nl.ns.bi_automation.service;

import nl.ns.bi_automation.model.AppConstants;
import nl.ns.bi_automation.model.flow.Component;
import nl.ns.bi_automation.model.flow.Pipeline;
import nl.ns.bi_automation.model.metadata.Column;
import nl.ns.bi_automation.model.metadata.Mapping;
import nl.ns.bi_automation.model.metadata.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class MetadataMonster {

    private ArrayList<Mapping> mappings = new ArrayList<>();
    private ArrayList<Table> tables = new ArrayList<>();
    private ArrayList<Column> columns = new ArrayList<>();
    private ArrayList<Pipeline> pipelines = new ArrayList<>();
    private ArrayList<Component> components = new ArrayList<>();

    private static final Logger logger = LogManager.getLogger(MetadataMonster.class);

    /*
    Custom methods
     */
    public String getValueForParameter(Object metadataObject, String parameter) {
        if (null == parameter)
            logger.error("MetadataMonster.getValueForParameter received null for 'parameter' argument");
        logger.trace("MetadataMonster.getValueForParameter.parameter: " + parameter);
        while (parameter.contains("<") && parameter.contains(">")) {
            String sp = parameter.substring(parameter.indexOf("<"), parameter.indexOf(">") + 1); // sp = subParameter
            String spPrefix = sp.substring(1, sp.indexOf(".")); //start met 1, want begint met "<"
            String spKey = sp.replace(spPrefix + ".", "").replace("<", "").replace(">", ""); //trim alle rommel
            logger.trace("MetadataMonster.spPrefix: " + spPrefix);
            logger.trace("MetadataMonster.spKey: " + spKey);
            if (metadataObject instanceof Column) {
                switch (spPrefix.toUpperCase()) {
                    case AppConstants.KEY_PREFIX_COLUMN:
                        parameter = parameter.replace(sp, ((Column) metadataObject).getValueForKey(spKey));
                        break;
                    case AppConstants.KEY_PREFIX_TABLE:
                        parameter = parameter.replace(sp, ((Column) metadataObject).getTable().getValueForKey(spKey));
                        break;
                    default:
                        getWronglyRoutedMessage(metadataObject, parameter);
                }

            } else if (metadataObject instanceof Table) {
                switch (spPrefix.toUpperCase()) {
                    case AppConstants.KEY_PREFIX_TABLE:
                        parameter = parameter.replace(sp, ((Table) metadataObject).getValueForKey(spKey));
                        break;
                    default:
                        getWronglyRoutedMessage(metadataObject, parameter);
                }
            } else {
                getWronglyRoutedMessage(metadataObject, parameter);
            }
        }
        // Zou nu helemaal resolved moeten zijn...
        return parameter;
    }

    public Pipeline getPipelineById(int pipeline_id) {
        for (Pipeline pipeline : this.pipelines) {
            if (pipeline.getId() == pipeline_id)
                return pipeline;
        }
        return null;
    }

    public ArrayList<Column> getColumnsByTable(Table table) {
        ArrayList<Column> result = new ArrayList<>();
        for (Column col : columns) {
            if (table == col.getTable()) {
                result.add(col);
            }
        }
        return result;
    }

    public void addTable(Table t) {
        this.tables.add(t);
    }

    public void addColumn(Column t) {
        this.columns.add(t);
    }

    public void addPipeline(Pipeline p) {
        this.pipelines.add(p);
    }

    public void addComponent(Component c) {
        this.components.add(c);
    }

    private void getWronglyRoutedMessage(Object metadataObject, String parameter) {
        logger.warn("MetadataMonster does not understand combination of class '" + metadataObject.getClass() + "' and parameter '" + parameter + "'");
    }



    /*
  Default getters and setters
   */

    public ArrayList<Mapping> getMappings() {
        return mappings;
    }

    public ArrayList<Table> getTables() {
        return tables;
    }

    public ArrayList<Column> getColumns() {
        return columns;
    }

    public ArrayList<Pipeline> getPipelines() {
        return pipelines;
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    /*
        "Constructor" code
        */
    // Java code for Bill Pugh Singleton Implementation
    private MetadataMonster() {
    }

    // Inner class to provide instance of class
    private static class BillPughSingleton {

        private static final MetadataMonster INSTANCE = new MetadataMonster();
    }

    public static MetadataMonster getInstance() {
        return BillPughSingleton.INSTANCE;
    }

}
