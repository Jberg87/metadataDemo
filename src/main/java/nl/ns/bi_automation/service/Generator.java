package nl.ns.bi_automation.service;

import nl.ns.bi_automation.model.AppConstants;
import nl.ns.bi_automation.model.flow.Component;
import nl.ns.bi_automation.model.flow.Order;
import nl.ns.bi_automation.model.metadata.Column;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Generator {

    private ArrayList<Order> orders = new ArrayList<Order>();

    public void generate() {
        for (Order order : orders) {
            if (order.isGenerate()) {
                System.out.println("Going to generate the following table: " + order.getTable().getName());
                System.out.println("  Going to generate the following pipeline: " + order.getPipeline().getId() + " - " + order.getPipeline().getDescription());

                ArrayList<Component> components = MetadataMonster.getInstance().getComponents();
                for (Component component : components) {
                    if (component.getPipeline() == order.getPipeline()) {
                        System.out.println("    Template           : " + component.getTemplate());
                        System.out.println("    Template parameters: " + component.getParameters());

                        // TODO get template_parameter keys+parameter from template parameters file
                        Map<String, Object> templateParameters = JsonParser.loadTemplateParameters(component.getParameters());

                        // TODO zoek in elke template_value naar paramaters (staan tussen < en >)
                        Map<String, Object> resolvedTemplateParameters = new HashMap<>();
                        ArrayList<Map<String, String>> resolvedTemplateLoopParameters = new ArrayList<>();
//                        Map<String, Map<String, String>> resolvedTemplateLoopParameters = new HashMap<>(); //Gebruik de kolom naam als key

                        // haal iedere parameter op via metadata monster
                        // voeg iedere template_key en resolved_template_value toe aan een template hashmap
                        for (String key : templateParameters.keySet()) {

                            System.out.println(" template parameter key: " + templateParameters.get(key) );
                            // TODO  bepalen of het gaat om TABLE of COLUMN?

                            if (templateParameters.get(key) instanceof String) {
                                System.out.println(AppConstants.LOG_TRACE_PREFIX + "resolve reguliere parameter");
                                resolvedTemplateParameters.put(key, MetadataMonster.getInstance().getValueForParameter(order.getTable(), (String) templateParameters.get(key)));
                            } else if (templateParameters.get(key) instanceof Map) {
                                Map<String, String> templateLoopParameters = (Map<String, String>) templateParameters.get(key);
                                System.out.println(AppConstants.LOG_TRACE_PREFIX + "resolve loop parameter");
                                //FIXME neem aan dat een loop parameter om een column gaat
                                ArrayList<Column> columns = MetadataMonster.getInstance().getColumnsByTable(order.getTable());
                                for (Column col : columns) {
                                    Map<String, String> colMap = new HashMap<>();
                                    for (String mapKey : templateLoopParameters.keySet()) {
                                        System.out.println(AppConstants.LOG_TRACE_PREFIX + "Loop parameter found: " + mapKey);
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
        System.out.println("tada!");
    }

    private void makeArtifact(String templatePath, Map<String, Object> parameters) {
        VelocityEngine velocityEngine = new VelocityEngine();
        Template vmTemplate = velocityEngine.getTemplate( templatePath );

        VelocityContext vContext = new VelocityContext();
        parameters.forEach((key, value) -> {
            // Door het zo te doen hoef je geen omringende loop in iedere template te plaatsen
            vContext.put( key, value );
        });
        StringWriter writer = new StringWriter();
        vmTemplate.merge( vContext, writer );
        String result = writer.toString();
        System.out.println("======================");
        System.out.println(result);
        System.out.println("======================");
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
