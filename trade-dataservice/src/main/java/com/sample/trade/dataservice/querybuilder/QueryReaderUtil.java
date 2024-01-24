package com.sample.trade.dataservice.querybuilder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class QueryReaderUtil {
    public String readQueryTemplate(Resource resource) {
        String query = StringUtils.EMPTY;

        try (InputStream inStream = resource.getInputStream();
                BufferedInputStream bufferedReader = new BufferedInputStream(inStream)) {
            query = new String(bufferedReader.readAllBytes());
            log.info("Query read from file | Query:[{}] FileName:[{}]", query, resource.getFilename());
        } catch (IOException e) {
            log.error("Exception occurred while reading query | Exception:[{}] Query:[{}] FileName:[{}]", e, query,
                    resource.getFilename());
            throw new IllegalArgumentException(
                    "Exception occurred while reading query from file" + resource.getFilename());
        }
        return query;
    }
}