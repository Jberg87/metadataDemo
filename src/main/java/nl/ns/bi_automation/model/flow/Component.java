package nl.ns.bi_automation.model.flow;

public class Component {
    private Pipeline pipeline;
    private String template;
    private String parameters;

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getParameters() {
        return parameters;
    }
}
