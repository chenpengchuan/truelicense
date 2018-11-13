//package de.schlichtherle.app.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.sql.Connection;
//import java.sql.DatabaseMetaData;
//import java.sql.ResultSet;
//
//@Component
//public class CheckTableService {
//
//    public static final String TABLE_NAME = "license";
//    private final String CREATE_TABLE_SQL = "CREATE TABLE "+TABLE_NAME+" (\n" +
//            "  `id` varchar(255) NOT NULL,\n" +
//            "  `user` varchar(255) DEFAULT NULL,\n" +
//            "  `sid` varchar(255) DEFAULT NULL,\n" +
//            "  `info` varchar(255) DEFAULT NULL,\n" +
//            "  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,\n" +
//            "  `not_before` date DEFAULT NULL,\n" +
//            "  `not_after` date DEFAULT NULL,\n" +
//            "  `license` text\n" +
//            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @PostConstruct
//    public void initTable() {
//        try {
//            if (!checkTableName(jdbcTemplate, TABLE_NAME)) {
//                jdbcTemplate.update(CREATE_TABLE_SQL);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * 查询数据库是否有某表
//     *
//     * @param tableName
//     * @return
//     * @throws Exception
//     */
//    @SuppressWarnings("unchecked")
//    public boolean checkTableName(JdbcTemplate jt, String tableName) throws Exception {
//        Connection conn = jt.getDataSource().getConnection();
//        ResultSet tabs = null;
//        try {
//            DatabaseMetaData dbMetaData = conn.getMetaData();
//            String[] types = {"TABLE"};
//            tabs = dbMetaData.getTables(null, null, tableName, types);
//            if (tabs.next()) {
//                return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            tabs.close();
//            conn.close();
//        }
//        return false;
//    }
//}
