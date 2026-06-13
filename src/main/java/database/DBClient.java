package database;

import models.request.PostUpdateEventRequest;
import util.Configuration;

import java.sql.*;

public class DBClient {

    Connection conn;

    private void connect() throws SQLException {
        conn = DriverManager.getConnection(Configuration.DB_URL, Configuration.DB_USER, Configuration.DB_PASS);
    }

    public PostUpdateEventRequest getEventFromDB(String id) throws SQLException {
        connect();

        String sql = "SELECT * FROM events WHERE id='" + id + "'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        rs.next();

        PostUpdateEventRequest request = PostUpdateEventRequest.builder()
                .title(rs.getString("title"))
                .image(rs.getString("image"))
                .date(rs.getString("date"))
                .location(rs.getString("location"))
                .description(rs.getString("description"))
                .build();

        conn.close();

        return request;
    }


    public boolean isEventDeleted(String id) throws SQLException {
        connect();

        String sql = "DELETE FROM events WHERE id='" + id + "'";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        return pstmt.executeUpdate() == 1;
    }
}