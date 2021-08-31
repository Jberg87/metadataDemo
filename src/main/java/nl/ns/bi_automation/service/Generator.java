package nl.ns.bi_automation.service;

import nl.ns.bi_automation.model.AppConstants;
import nl.ns.bi_automation.model.flow.Component;
import nl.ns.bi_automation.model.flow.Order;
import nl.ns.bi_automation.model.metadata.Column;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Generator {

    private ArrayList<Order> orders = new ArrayList<Order>();

    private static final Logger logger = LogManager.getLogger(Generator.class);

    public void generate() {
        for (Order order : orders) {
            if (order.isGenerate()) {
                logger.debug("Going to generate the following table: " + order.getTable().getName());
                logger.debug("  Going to generate the following pipeline: " + order.getPipeline().getId() + " - " + order.getPipeline().getDescription());

                ArrayList<Component> components = MetadataMonster.getInstance().getComponents();
                for (Component component : components) {
                    if (component.getPipeline() == order.getPipeline()) {
                        logger.debug("    Template           : " + component.getTemplate());
                        logger.debug("    Template parameters: " + component.getParameters());

                        // TODO get template_parameter keys+parameter from template parameters file
                        Map<String, Object> templateParameters = JsonParser.loadTemplateParameters(component.getParameters());

                        Map<String, Object> resolvedTemplateParameters = new HashMap<>();
                        ArrayList<Map<String, String>> resolvedTemplateLoopParameters = new ArrayList<>();
//                        Map<String, Map<String, String>> resolvedTemplateLoopParameters = new HashMap<>(); //Gebruik de kolom naam als key

                        // haal iedere parameter op via metadata monster
                        // voeg iedere template_key en resolved_template_value toe aan een template hashmap
                        for (String key : templateParameters.keySet()) {
                            logger.debug(" template parameter key: " + templateParameters.get(key) );

                            if (templateParameters.get(key) instanceof String) {
                                logger.trace(AppConstants.LOG_TRACE_PREFIX + "resolve reguliere parameter");
                                resolvedTemplateParameters.put(key, MetadataMonster.getInstance().getValueForParameter(order.getTable(), (String) templateParameters.get(key)));
                            } else if (templateParameters.get(key) instanceof Map) {
                                Map<String, String> templateLoopParameters = (Map<String, String>) templateParameters.get(key);
                                logger.trace(AppConstants.LOG_TRACE_PREFIX + "resolve loop parameter");

                                //Neem aan dat een loop parameter om een column gaat
                                ArrayList<Column> columns = MetadataMonster.getInstance().getColumnsByTable(order.getTable());
                                for (Column col : columns) {
                                    Map<String, String> colMap = new HashMap<>();
                                    for (String mapKey : templateLoopParameters.keySet()) {
                                        logger.debug(AppConstants.LOG_TRACE_PREFIX + "Loop parameter found: " + mapKey);
                                        String loopParameter = templateLoopParameters.get(mapKey);
                                        colMap.put(mapKey, MetadataMonster.getInstance().getValueForParameter(col, loopParameter));
                                    }
                                    resolvedTemplateLoopParameters.add(colMap);
                                }
                            }
                        }
                        resolvedTemplateParameters.put("loop_parameters", resolvedTemplateLoopParameters);
                        makeArtifact( component.getTemplate(), resolvedTemplateParameters );
                    }


                    // geef mee aan velocity
                    // print naar console
                    // schrijf weg naar disk

                }
            }
        }
        logger.info("tada!");
    }

    private void makeArtifact(String templatePath, Map<String, Object> parameters) {

        /*
        Met wat hulp van:
        https://stackoverflow.com/questions/9051413/unable-to-find-velocity-template-resources
         */
        Properties p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        VelocityEngine velocityEngine = new VelocityEngine(p );
        Template vmTemplate = velocityEngine.getTemplate( templatePath );

        VelocityContext vContext = new VelocityContext();
        parameters.forEach((key, value) -> {
            // Door het zo te doen hoef je geen omringende loop in iedere template te plaatsen
            vContext.put( key, value );
        });
        StringWriter writer = new StringWriter();
        vmTemplate.merge( vContext, writer );
        String result = writer.toString();
        logger.info("Nieuwe component gegenereerd\n\n" + result + "\n\n");
        logger.info("=========================================================================");
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }


    // Java code for Bill Pugh Singleton Implementation
    private Generator() {
    }

// Inner class to provide instance of class
private static class BillPughSingleton {
    private static final Generator INSTANCE = new Generator();
}

    public static Generator getInstance() {
        return Generator.BillPughSingleton.INSTANCE;
    }
}
