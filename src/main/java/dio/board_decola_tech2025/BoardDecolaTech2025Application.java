package dio.board_decola_tech2025;

import dio.board_decola_tech2025.persistence.migration.MigrationStrategy;
import dio.board_decola_tech2025.ui.MainApplicationMenu;

import java.sql.SQLException;

import static dio.board_decola_tech2025.persistence.config.ConnectionConfig.getConnection;

public class BoardDecolaTech2025Application {

	public static void main(String[] args) throws SQLException {
		try(var connection = getConnection()){
			new MigrationStrategy(connection).executeMigration();
		}

		new MainApplicationMenu().execute();
	}

}
