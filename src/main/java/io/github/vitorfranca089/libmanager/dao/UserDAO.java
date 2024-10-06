package io.github.vitorfranca089.libmanager.dao;

import io.github.vitorfranca089.libmanager.config.DatabaseConfig;
import io.github.vitorfranca089.libmanager.dto.UserDTO;
import io.github.vitorfranca089.libmanager.model.User;
import io.github.vitorfranca089.libmanager.model.enums.Role;

import java.sql.*;

public class UserDAO {

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
                String username = makeUsername(user.getName(), user.getRole(), userId);

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

    public UserDTO findUserByID(int id) {
        String sql = "SELECT id, name, username, address, phone, role FROM users WHERE id = ?";
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
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

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET name = ?, address = ?, phone = ? WHERE id = ?";
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getAddress());
            stmt.setString(3, user.getPhone());
            stmt.setInt(4, user.getId());

            return (stmt.executeUpdate()) > 0;

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean toDefaultPassword(int id, String defaultPass) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, defaultPass);
            stmt.setInt(2, id);

            return (stmt.executeUpdate()) > 0;

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    private String makeUsername(String name, Role role, int id){
        String firstName = name.split(" ")[0].toLowerCase();
        String roleType = role.equals(Role.LIBRARIAN) ? "B" : "U";
        return firstName + roleType + id;
    }

}
