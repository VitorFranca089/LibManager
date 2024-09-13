package io.github.vitorfranca089.libmanager.dao;

import io.github.vitorfranca089.libmanager.config.DatabaseConfig;
import io.github.vitorfranca089.libmanager.dto.UserDTO;
import io.github.vitorfranca089.libmanager.model.User;
import io.github.vitorfranca089.libmanager.model.enums.Role;
import io.github.vitorfranca089.libmanager.util.CredencialUtils;

import java.sql.*;

public class UserDAO {

    public UserDTO findUserById(int userId){
        String sql = "SELECT id, name, username, address, phone, role FROM users WHERE id = ?";
        try(PreparedStatement stmt = DatabaseConfig.getConnection().prepareStatement(sql)){
            stmt.setInt(1, userId);
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

    public UserDTO singUpUser(User user){
        String sqlInsert = "INSERT INTO users (name, password, address, phone, role) VALUES (?,?,?,?,?)";
        String sqlUpdate = "UPDATE users SET username = ? WHERE id = ?";

        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert, PreparedStatement.RETURN_GENERATED_KEYS);
            PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)){
            stmtInsert.setString(1, user.getName());
            stmtInsert.setString(2, user.getPassword());
            stmtInsert.setString(3, user.getAddress());
            stmtInsert.setString(4, user.getPhone());
            stmtInsert.setString(5, user.getRole().toString());
            stmtInsert.executeUpdate();

            ResultSet generatedKeys = stmtInsert.getGeneratedKeys();

            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                String username = CredencialUtils.makeUsername(user.getName(), user.getRole(), userId);

                stmtUpdate.setString(1, username);
                stmtUpdate.setInt(2, userId);
                stmtUpdate.executeUpdate();

                return new UserDTO(
                        userId,
                        user.getName(),
                        username,
                        user.getAddress(),
                        user.getPhone(),
                        user.getRole()
                );

            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

}
