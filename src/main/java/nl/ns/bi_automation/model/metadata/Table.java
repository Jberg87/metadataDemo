package nl.ns.bi_automation.model.metadata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Table {

    private String database;
    private String schema;
    private String name;
    private String type; //view or table
    private String owner;
    private String description;

    private static final Logger logger = LogManager.getLogger(Table.class);

    public String getValueForKey(String key) {
        switch (key.toUpperCase()) {
            case "DATABASE": //<table.database>
                return getDatabase();
            case "SCHEMA": //<table.schema>
                return getSchema();
            case "NAME": //<table.name>
                return getName();
            case "TYPE": //<table.type>
                return getType();
            case "OWNER": //<table.owner>
                return getOwner();
            case "DESCRIPTION": //<table.description>
                return getDescription();
            default:
                logger.warn("I do not know what to do with this TABLE key: " + key);
                // Bij foutmelding ga maar gewoon door. Console toont de string dat er iets fout is gegaan
                return "";
        }
    }

    /**
     * @return String return the database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @param database the database to set
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * @return String return the schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * @param schema the schema to set
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * @return String return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return DatabaseObjectType return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return String return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return String return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }


}
