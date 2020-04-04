package com.iMatch.etl.preprocessmethods;

import com.iMatch.etl.EtlDefinition;
import com.iMatch.etl.IPreProcessMethod;
import com.iMatch.etl.datauploader.ETLServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class CompositePreProcessor implements IPreProcessMethod {
    private IPreProcessMethod[] preProcessMethods;
    private static final Logger logger = LoggerFactory.getLogger(CompositePreProcessor.class);

    public CompositePreProcessor(IPreProcessMethod... methods) {
        this.preProcessMethods = methods;
    }

    @Override
    public void apply(EtlDefinition defn, File file, ETLServiceProvider etlService) {
        String preProcessInput = defn.getPreProcessInput();
        logger.debug("applying "+defn.getPreProcessMethod()+" methods for inputs {} to file {}", preProcessInput, file.getName());
        String[] inputs = preProcessInput.split(" ");
        if (inputs.length != preProcessMethods.length) {
            logger.error("not applying " + defn.getPreProcessMethod() + "; no of inputs not equal to number of methods");
        }

        for (int i = 0; i < preProcessMethods.length; i++) {
            IPreProcessMethod preProcessMethod = preProcessMethods[i];
            defn.setPreProcessInput(inputs[i]);
            preProcessMethod.apply(defn, file, etlService);
        }
        defn.setPreProcessInput(preProcessInput);
    }

    @Override
    public boolean shouldApplyDelay(ETLServiceProvider etlService, PreProcessTime... preProcessTime) {
        for (int i = 0; i < preProcessMethods.length; i++) {
            if (preProcessMethods[i].shouldApplyDelay(etlService, preProcessTime[i])) return true;
        }
        return false;
    }
}
