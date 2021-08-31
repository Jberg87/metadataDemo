package nl.ns.bi_automation.service;


import nl.ns.bi_automation.model.flow.Component;
import nl.ns.bi_automation.model.flow.Order;
import nl.ns.bi_automation.model.flow.Pipeline;
import nl.ns.bi_automation.model.metadata.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class JsonParser {

    private static final Logger logger = LogManager.getLogger(JsonParser.class);

    public static void loadReferenceData(String path) {

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(new File(path)));

            // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
            /*
            JSON Pipeline example:
            {
              "id": "1",
              "description": "RDV Load zonder delete detectie SQL Server",
              "components":[
              ...
              ]
             }
             */
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray pipelineArray = (JSONArray) jsonObject.get("pipelines");

            pipelineArray.forEach(jsonObj -> {
                Pipeline pipeline = new Pipeline();

                JSONObject jsonPipeline = (JSONObject) jsonObj;

                pipeline.setId(Integer.parseInt((String) jsonPipeline.get("id")));
                pipeline.setDescription((String) jsonPipeline.get("description"));

                // Get columns from JSON object
                /*
                Component example:
                {
                  "name": "STG DDL",
                  "template": "/templates/STG_DDL/template.vm",
                  "parameters": "/templates/STG_DDL/parameters.json"
                }
                */

                MetadataMonster.getInstance().addPipeline(pipeline);

                JSONArray columnArray = (JSONArray) jsonPipeline.get("components");
                columnArray.forEach(jsonInput -> {
                            Component com = new Component();
                            JSONObject jsonComponent = (JSONObject) jsonInput;
                            com.setPipeline(pipeline);
                            com.setTemplate((String) jsonComponent.get("template"));
                            com.setParameters((String) jsonComponent.get("parameters"));
                            MetadataMonster.getInstance().addComponent(com);
                        }
                );
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void loadMetadata(String filePath) {

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(new File(filePath)));

            // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
            /*
            JSON table example:
            {
              "database": "EDW",
              "schema": "RDV",
              "name": "Persoon_H",
              "type": "table",
              "owner": "Jesper's baas",
              "description": "alle personen",
              "columns":[
                {
                  "name": "voornaam",
                  "datatype": "varchar",
                  "precision": "200",
                  "scale": "",
                  "nullable": "false",
                  "description": ""
                },
             */
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray tableArray = (JSONArray) jsonObject.get("tables");

            tableArray.forEach(jsonObj -> {
                Table table = new Table();

                JSONObject jsonTable = (JSONObject) jsonObj;

                table.setDatabase((String) jsonTable.get("database"));
                table.setSchema((String) jsonTable.get("schema"));
                table.setName((String) jsonTable.get("name"));
                table.setType((String) jsonTable.get("type"));
                table.setOwner((String) jsonTable.get("owner"));
                table.setDescription((String) jsonTable.get("description"));

                // Get columns from JSON object
                /*
                Column example:
                {
                  "name": "voornaam",
                  "datatype": "varchar",
                  "precision": "200",
                  "scale": "",
                  "nullable": "false",
                  "description": ""
                }
                */

                MetadataMonster.getInstance().addTable(table);

                JSONArray columnArray = (JSONArray) jsonTable.get("columns");
                columnArray.forEach(jsonInput -> {
                            Column col = new Column();
                            JSONObject jsonColumn = (JSONObject) jsonInput;
                            col.setTable(table);
                            col.setName((String) jsonColumn.get("name"));
                            col.setDatatype(Datatype.valueOf(((String) jsonColumn.get("datatype")).toUpperCase()));
                            col.setPrecision(Integer.parseInt(((String) jsonColumn.get("precision")).isEmpty() ? "0" : (String) jsonColumn.get("precision")));
                            col.setScale(Integer.parseInt(((String) jsonColumn.get("scale")).isEmpty() ? "0" : (String) jsonColumn.get("scale")));
                            col.setNullable(Objects.equals(jsonColumn.get("nullable"), "true"));
                            col.setDescription((String) jsonColumn.get("description"));
                            MetadataMonster.getInstance().addColumn(col);
                        }
                );

                /*
                Order example:
                "orders": [
                   {
                     "pipeline_id": "1",
                     "generate": "true"
                   }
                 ]

                 */
                JSONArray orderArray = (JSONArray) jsonTable.get("orders");
                if (null != orderArray) {
                    orderArray.forEach(jsonInput -> {
                                JSONObject jsonOrder = (JSONObject) jsonInput;
                                Order order = new Order();
                                order.setTable(table);
                                order.setPipeline(MetadataMonster.getInstance().getPipelineById(Integer.parseInt((String) jsonOrder.get("pipeline_id"))));
                                order.setGenerate(Objects.equals(jsonOrder.get("generate"), "true"));

                                Generator.getInstance().addOrder(order);
                            }
                    );
                } else {
                    logger.info("No order for table: " + table.getName());
                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map loadTemplateParameters(String path) {
        Map<String, Object> parameters = new HashMap<>();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(new File(path)));

            // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
            /*
            JSON Pipeline example:
            {
              "parameters": [
                {
                  "database": "<table.database>",
                  "table": "<table.name>"
                }
              ],
              "loop_parameters": {
                "column_name": "<column.name>",
                "column_precision": "<column.precision>",
                "column_scale": "<column.scale>",
                "column_nullable": "<column.nullable>",
                "column_description": "<column.name>: <column.nullable>"
              }
            }
             */
            JSONObject jsonObject = (JSONObject) obj;
            Set<String> keys = jsonObject.keySet();
            keys.forEach(key -> {
                if (!key.equals("loop_parameters")) {
                    parameters.put(key, (String) jsonObject.get(key));
                } else {
                    Map<String, String> loopParameters = new HashMap<>();
                    JSONObject jsonLoopParameters = (JSONObject) jsonObject.get(key);
                    Set<String> loopKeys = jsonLoopParameters.keySet();
                    loopKeys.forEach(loopKey -> {
                        loopParameters.put(loopKey, (String) jsonLoopParameters.get(loopKey));
                    });
                    parameters.put("loop_parameters", loopParameters);
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parameters;
    }
}
