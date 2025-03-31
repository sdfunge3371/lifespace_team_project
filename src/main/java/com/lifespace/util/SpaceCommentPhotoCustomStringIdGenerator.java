package com.lifespace.util;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SpaceCommentPhotoCustomStringIdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        try {
            // Hibernate6.0以後 Connection 寫法
            Connection connection = session
                    .getJdbcCoordinator()
                    .getLogicalConnection()
                    .getPhysicalConnection();

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(space_comment_photo_id) FROM space_comment_photo");

            if (rs.next()) {
                String maxId = rs.getString(1);
                if (maxId != null) {
                    int num = Integer.parseInt(maxId.substring(2));
                    return "SC" + String.format("%03d", num + 1);   // ?改成自己表格的開頭
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "SC001"; // default 起始值，?改成自己表格的開頭
    }
}
