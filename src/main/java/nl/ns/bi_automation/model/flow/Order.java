package nl.ns.bi_automation.model.flow;

import nl.ns.bi_automation.model.metadata.Table;

public class Order {
    private Table table;
    private Pipeline pipeline;
    private boolean generate;

    public void setTable(Table table) {
        this.table = table;
    }

    public Table getTable() {
        return table;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setGenerate(boolean generate) {
        this.generate = generate;
    }

    public boolean isGenerate() {
        return generate;
    }
}
