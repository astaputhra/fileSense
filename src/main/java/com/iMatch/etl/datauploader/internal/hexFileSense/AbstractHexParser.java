package com.iMatch.etl.datauploader.internal.hexFileSense;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHexParser  implements IHexParser {
    private static Logger _logger = LoggerFactory.getLogger(AbstractHexParser.class);

    protected boolean checkFileNamePat(String fileNamePattern, String filename, boolean isDebug, String expectedFlowname) {
        String basename = FilenameUtils.getBaseName(filename);
        if(StringUtils.startsWith(basename,fileNamePattern))
        {
            _logger.debug("MATCH:: FileNafilenamemePattern = {} and Filename is {}", fileNamePattern, basename);
            return true;
        }
        _logger.debug("NO - MATCH:: FileNamePattern = {} and Filename is {}", fileNamePattern, basename);
        if(isDebug){
            _logger.error("ETL_DEBUG: Flow {} has a filename pattern of {} but this did not match the filename of the input file which is {}", new String[]{expectedFlowname, fileNamePattern, filename});
        }
        return false;
    }
}
