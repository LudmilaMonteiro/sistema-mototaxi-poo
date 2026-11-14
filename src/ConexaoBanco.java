import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {
    private static final String URL =  "jdbc:postgresql://localhost:5432/sistema_mototaxi";
    private static final String USUARIO = "postgres";
    public static Connection conectar() throws SQLException {
        String senha = System.getenv("DB_PASSWORD");

        if (senha == null || senha.isBlank()) {
            throw new SQLException("Senha do banco nao configurada.");
        }

        return DriverManager.getConnection(URL, USUARIO, senha);
    }
}