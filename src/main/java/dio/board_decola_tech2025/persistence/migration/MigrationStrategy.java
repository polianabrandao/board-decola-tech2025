package dio.board_decola_tech2025.persistence.migration;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.AllArgsConstructor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

import static dio.board_decola_tech2025.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class MigrationStrategy {

    //construtor gerado pelo lombok
    private final Connection connection;

    public void executeMigration(){
        var originalOut = System.out;
        var originalErr = System.err;
        //para direcionar para outro lugar as logs que o liquibase gera.
        try (var fos = new FileOutputStream("liquibase.log")) {  //fos = fileOutputStream
            System.setOut(new PrintStream(fos));
            System.setErr(new PrintStream(fos));

            //para fazer a conexão com o JDBC
            try (
                    var connection = getConnection();
                    var jdbcConnection = new JdbcConnection(connection);
            ) {

                var liquibase = new Liquibase("/db/changelog/db.changelog-master.yml", new ClassLoaderResourceAccessor(), jdbcConnection);
                liquibase.update();
            } catch (SQLException | LiquibaseException e) {
                e.printStackTrace();
                System.setErr(originalErr);
            }

        } catch (IOException ex){
            ex.printStackTrace();
        }finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
}
