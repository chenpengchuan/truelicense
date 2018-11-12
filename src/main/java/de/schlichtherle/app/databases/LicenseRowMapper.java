package de.schlichtherle.app.databases;

import de.schlichtherle.app.entity.LicenseEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LicenseRowMapper implements RowMapper<LicenseEntity> {
    @Override
    public LicenseEntity mapRow(ResultSet resultSet, int i) throws SQLException {
        LicenseEntity entity = new LicenseEntity();
        String id = resultSet.getString("id");
        String user = resultSet.getString("user");
        String sid = resultSet.getString("sid");
        String info = resultSet.getString("info");
        String createTime = resultSet.getString("create_time");
        String notBefore = resultSet.getString("not_before");
        String notAfter = resultSet.getString("not_after");
        String license = resultSet.getString("license");
        entity.setId(id);
        entity.setUser(user);
        entity.setSid(sid);
        entity.setInfo(info);
        entity.setCreateTime(createTime);
        entity.setNotBefore(notBefore);
        entity.setNotAfter(notAfter);
        entity.setLicense(license);
        return entity;
    }
}
