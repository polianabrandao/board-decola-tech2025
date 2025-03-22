package dio.board_decola_tech2025.persistence.config;

import lombok.NoArgsConstructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ConnectionConfig {

    //conexão com o banco de dados
    public static Connection getConnection() throws SQLException {
        var connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/board_decolatech2025", "root", "92416056");
        connection.setAutoCommit(false); //deixar como false para poder gerenciar as trasações.
        return connection;

    }

}
