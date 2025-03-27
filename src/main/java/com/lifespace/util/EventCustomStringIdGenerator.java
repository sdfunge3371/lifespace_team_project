package com.lifespace.util;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class EventCustomStringIdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        try {
            // Hibernate6.0以後 Connection 寫法
            Connection connection = session
                    .getJdbcCoordinator()
                    .getLogicalConnection()
                    .getPhysicalConnection();

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(event_id) FROM event");

            if (rs.next()) {
                String maxId = rs.getString(1);
                if (maxId != null) {
                    int num = Integer.parseInt(maxId.substring(1));
                    return "E" + String.format("%03d", num + 1);   // ?改成自己表格的開頭
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "E001"; // default 起始值，?改成自己表格的開頭
    }
}
