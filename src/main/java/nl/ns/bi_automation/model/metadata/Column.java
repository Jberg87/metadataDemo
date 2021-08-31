package nl.ns.bi_automation.model.metadata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Column {

    private Table table;
    private String name;
    private Datatype datatype;
    private int precision;
    private int scale;
    private boolean nullable;
    private String description;

    private static final Logger logger = LogManager.getLogger(Column.class);

    //FIXME make unit test
    public String getValueForKey(String key) {

        switch (key.toUpperCase()) {
            case "NAME": //<column.name>
                return getName();
            case "DATATYPE": //<column.datatype>
                return getDatatype().toString();
            case "PRECISION": //<column.precision>
                return String.valueOf(getPrecision());
            case "SCALE": //<column.scale>
                return String.valueOf(getScale());
            case "NULLABLE": //<column.nullable>
                return nullable ? "NULL" : "NOT NULL";
            case "DESCRIPTION": //<column.description>
                return getDescription();
            default:
                logger.warn("I do not know what to do with this COLUMN key: " + key);
                // Bij foutmelding ga maar gewoon door. Console toont de string dat er iets fout is gegaan
                return "";
        }
    }


    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Datatype getDatatype() {
        return datatype;
    }

    public void setDatatype(Datatype datatype) {
        this.datatype = datatype;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
