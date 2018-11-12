package de.schlichtherle.app.databases;

import de.schlichtherle.app.entity.LicenseEntity;
import de.schlichtherle.app.service.CheckTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class LicenseSave {

    private static final String  INSERT_INTO_SQL = "insert into "+CheckTableService.TABLE_NAME +" value(?,?,?,?,?,?,?,?)";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int insertIntoDatabases(LicenseEntity entity){
        String id = entity.getId();
        String user = entity.getUser();
        String sid = entity.getSid();
        String info = entity.getInfo();
        String createTime = entity.getCreateTime();
        String notBefore = entity.getNotBefore();
        String notAfter = entity.getNotAfter();
        String license = entity.getLicense();
       return jdbcTemplate.update(INSERT_INTO_SQL,id,user,sid,info,createTime,notBefore,notAfter,license);
    }
}
