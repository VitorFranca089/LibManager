package io.github.vitorfranca089.libmanager.dao;

import io.github.vitorfranca089.libmanager.config.DatabaseConfig;
import io.github.vitorfranca089.libmanager.dto.UserDTO;
import io.github.vitorfranca089.libmanager.model.enums.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDAO {

    public UserDTO login(String username, String password){
        String sql = "SELECT id, name, username, address, phone, role FROM users WHERE username = ? AND password = ?";
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                return new UserDTO(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        Role.valueOf(rs.getString(6))
                );
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

}
