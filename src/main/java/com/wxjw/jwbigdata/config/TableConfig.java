package com.wxjw.jwbigdata.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author panhao
 * @version 1.0.0
 * @className TableConfig
 * @description TODO
 * @date 2024-06-16 0:09
 **/
@Configuration
public class TableConfig {
    @Value("${table.human.humanTable}")
    private String humanTable;

    @Value("${table.human.humanId}")
    private String humanId;

    @Value("${table.human.humanName}")
    private String humanName;

    @Value("${table.human.humanPk}")
    private String humanPk;

    @Value("${table.human.humanFk}")
    private String humanFk;

    @Value("${table.company.companyTable}")
    private String companyTable;

    @Value("${table.company.companyId}")
    private String companyId;

    @Value("${table.company.companyName}")
    private String companyName;

    @Value("${table.company.companyPk}")
    private String companyPk;

    @Value("${table.company.companyFk}")
    private String companyFk;

    public String gethumanTable() {
        return humanTable;
    }

    public String gethumanId() {
        return humanId;
    }

    public String gethumanName() {
        return humanName;
    }

    public String gethumanPk() {
        return humanPk;
    }

    public String getcompanyTable() {
        return companyTable;
    }

    public String getcompanyId() {
        return companyId;
    }

    public String getcompanyName() {
        return companyName;
    }

    public String getcompanyPk() {
        return companyPk;
    }

    public String getHumanFk() { return humanFk; }

    public String getCompanyFk() {return companyFk;}

}
