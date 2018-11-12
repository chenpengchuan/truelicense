package de.schlichtherle.app.databases;

import de.schlichtherle.app.entity.LicenseEntity;
import de.schlichtherle.app.service.CheckTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LicenseQuery {

    private static final String QUERY_LIST_SQL="SELECT * FROM "
            +CheckTableService.TABLE_NAME+" ORDER BY not_after DESC";

    private static final String QUERY_ONE_SQL = "SELECT * FROM "+CheckTableService.TABLE_NAME+" WHERE id =?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<LicenseEntity> queryLicenseList(){
        RowMapper<LicenseEntity> rowMapper = new BeanPropertyRowMapper<LicenseEntity>(LicenseEntity.class);
        List<LicenseEntity> result = (List<LicenseEntity>) jdbcTemplate.query(QUERY_LIST_SQL, rowMapper);

        return result;
    }

    public LicenseEntity findOneById(String id){
//        LicenseRowMapper<LicenseEntity> rowMapper = new BeanPropertyRowMapper<>(LicenseEntity.class);
        LicenseEntity result = jdbcTemplate.queryForObject(QUERY_ONE_SQL,new LicenseRowMapper(),id);

        return result;
    }
}
