package xyz.mahmoudahmed.dsl;

import xyz.mahmoudahmed.adapter.DataNode;
import xyz.mahmoudahmed.adapter.InputAdapterService;
import xyz.mahmoudahmed.dsl.Format;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class Mapping {
    private final String sourceName;
    private final Format sourceFormat;
    private final String targetName;
    private final Format targetFormat;
    private final List<MappingRule> rules;

    Mapping(String sourceName, Format sourceFormat, String targetName, Format targetFormat, List<MappingRule> rules) {
        this.sourceName = sourceName;
        this.sourceFormat = sourceFormat;
        this.targetName = targetName;
        this.targetFormat = targetFormat;
        this.rules = new ArrayList<>(rules);
    }

    public Object execute(Object sourceData) {
        InputAdapterService adapterService = Platymap.getAdapterService();

        // Parse source data if it's a string, file, or other supported input
        DataNode sourceNode;
        if (sourceData instanceof String) {
            sourceNode = adapterService.parseData((String) sourceData);
        } else if (sourceData instanceof File) {
            sourceNode = adapterService.parseData((File) sourceData);
        } else if (sourceData instanceof byte[]) {
            sourceNode = adapterService.parseData((byte[]) sourceData);
        } else if (sourceData instanceof DataNode) {
            sourceNode = (DataNode) sourceData;
        } else {
            throw new MappingExecutionException("Unsupported source data type: " + sourceData.getClass().getName());
        }

        // Create the mapping context
        MappingContext context = new MappingContext(sourceNode);

        // Create the target object
        DataNode.ObjectNode targetNode = new DataNode.ObjectNode();

        // Apply all the mapping rules
        for (MappingRule rule : rules) {
            rule.apply(context, targetNode);
        }

        return targetNode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getTargetName() {
        return targetName;
    }

    public String executeToFormat(Object sourceData, Format outputFormat) {
        DataNode result = (DataNode) execute(sourceData);
        return Platymap.getAdapterService().serializeData(
                result,
                Platymap.toKotlinFormat(outputFormat)
        );
    }

    public void executeToFormat(Object sourceData, Format outputFormat, Writer writer) {
        DataNode result = (DataNode) execute(sourceData);
        Platymap.getAdapterService().serializeData(
                result,
                Platymap.toKotlinFormat(outputFormat),
                writer
        );
    }

    public void executeToFormat(Object sourceData, Format outputFormat, OutputStream outputStream) {
        DataNode result = (DataNode) execute(sourceData);
        Platymap.getAdapterService().serializeData(
                result,
                Platymap.toKotlinFormat(outputFormat),
                outputStream
        );
    }



    public String executeToJson(Object sourceData) {
        return executeToFormat(sourceData, Format.JSON);
    }


    public String executeToXml(Object sourceData) {
        return executeToFormat(sourceData, Format.XML);
    }
}