package com.sample.trade.dataservice.querybuilder;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.batch.process.dataservice.ContextMap;
import com.sample.common.TradeConstant;

@Component
public class ClientTempTableCreationQueryBuilder {

    @Value("classpath:create_code_temp_table.sql")
    private Resource tempTableScriptResource;

    private String query = null;

    @PostConstruct
    public void init() {
        query = QueryReaderUtil.readQueryTemplate(tempTableScriptResource);
    }

    public String buildTempTableCreationQuery(ContextMap contextMap) {
        String tempDb = "medium_demo";
        String tempTableName = contextMap.get(TradeConstant.CONTEXT_KEY_UCC_TEMP_TABLE);

        return DbUtility.getTableTokenReplacedQuery(tempTableName, query, tempDb);
    }
}