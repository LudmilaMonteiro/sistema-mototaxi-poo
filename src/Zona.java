import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Zona {
    private int idZona;
    private List<String> bairros;
    private Tipo tipo;

    private Zona(int idZona, List<String> bairros, Tipo tipo) {
        this.idZona = idZona;
        this.bairros = bairros;
        this.tipo = tipo;
    }

    public static Zona criarZona(List<String> bairros, Tipo tipo) {
        List<String> bairrosValidos = validarDados(bairros, tipo);

        String sql = "INSERT INTO zonas (tipo) VALUES (?) RETURNING id_zona";

        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                int id;

                try (PreparedStatement comando = conexao.prepareStatement(sql)) {
                    comando.setString(1, tipo.name());

                    try (ResultSet resultado = comando.executeQuery()) {
                        resultado.next();
                        id = resultado.getInt("id_zona");
                    }
                }

                inserirBairros(conexao, id, bairrosValidos);
                conexao.commit();

                return new Zona(id, bairrosValidos, tipo);
            } catch (SQLException | RuntimeException erro) {
                conexao.rollback();
                throw erro;
            }
        } catch (SQLException erro) {
            if ("23505".equals(erro.getSQLState())) {
                throw new IllegalStateException("Um dos bairros ja pertence a outra zona.", erro);
            }

            throw new IllegalStateException("Erro ao criar zona.", erro);
        }
    }

    public void verZona() {
        System.out.println("Zona: " + tipo);
        System.out.println("Bairros: " + String.join(", ", bairros));
    }

    public static void editarZona(Zona zona, List<String> bairros, Tipo tipo) {
        if (zona == null) {
            throw new IllegalArgumentException("Zona invalida.");
        }

        List<String> bairrosValidos = validarDados(bairros, tipo);

        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                try (PreparedStatement comando = conexao.prepareStatement("UPDATE zonas SET tipo = ? WHERE id_zona = ?")) {
                    comando.setString(1, tipo.name());
                    comando.setInt(2, zona.idZona);

                    if (comando.executeUpdate() != 1) {
                        throw new IllegalStateException("Zona nao encontrada.");
                    }
                }

                try (PreparedStatement comando = conexao.prepareStatement("DELETE FROM zona_bairros WHERE id_zona = ?")) {
                    comando.setInt(1, zona.idZona);
                    comando.executeUpdate();
                }

                inserirBairros(conexao, zona.idZona, bairrosValidos);
                conexao.commit();

                zona.bairros = bairrosValidos;
                zona.tipo = tipo;
            } catch (SQLException | RuntimeException erro) {
                conexao.rollback();
                throw erro;
            }
        } catch (SQLException erro) {
            if ("23505".equals(erro.getSQLState())) {
                throw new IllegalStateException("Um dos bairros ja pertence a outra zona.", erro);
            }

            throw new IllegalStateException("Erro ao editar zona.", erro);
        }
    }

    public static List<Zona> listarZonas() {
        String sql = "SELECT z.id_zona, z.tipo, b.bairro FROM zonas z "
                + "LEFT JOIN zona_bairros b ON b.id_zona = z.id_zona "
                + "ORDER BY z.id_zona, b.bairro";

        try (Connection conexao = ConexaoBanco.conectar(); PreparedStatement comando = conexao.prepareStatement(sql); ResultSet resultado = comando.executeQuery()) {
            return montarZonas(resultado);
        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao listar zonas.", erro);
        }
    }

    public static void deletarZona(Zona zona) {
        if (zona == null) {
            throw new IllegalArgumentException("Zona invalida.");
        }

        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                try (PreparedStatement comando = conexao.prepareStatement("DELETE FROM zona_bairros WHERE id_zona = ?")) {
                    comando.setInt(1, zona.idZona);
                    comando.executeUpdate();
                }

                try (PreparedStatement comando = conexao.prepareStatement("DELETE FROM zonas WHERE id_zona = ?")) {
                    comando.setInt(1, zona.idZona);

                    if (comando.executeUpdate() != 1) {
                        throw new IllegalStateException("Zona nao encontrada.");
                    }
                }

                conexao.commit();
            } catch (SQLException | RuntimeException erro) {
                conexao.rollback();
                throw erro;
            }
        } catch (SQLException erro) {
            if ("23503".equals(erro.getSQLState())) {
                throw new IllegalStateException("Zona possui pontos cadastrados e nao pode ser excluida.", erro);
            }

            throw new IllegalStateException("Erro ao excluir zona.", erro);
        }
    }

    public static Zona buscarZonaPorBairro(String bairro) {
        if (bairro == null || bairro.isBlank()) {
            throw new IllegalArgumentException("Bairro invalido.");
        }

        String sql = "SELECT z.id_zona, z.tipo, b.bairro FROM zonas z "
                + "JOIN zona_bairros b ON b.id_zona = z.id_zona "
                + "WHERE z.id_zona = (SELECT id_zona FROM zona_bairros WHERE LOWER(bairro) = LOWER(?)) "
                + "ORDER BY b.bairro";

        try (Connection conexao = ConexaoBanco.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, bairro.trim());

            try (ResultSet resultado = comando.executeQuery()) {
                List<Zona> zonas = montarZonas(resultado);
                return zonas.isEmpty() ? null : zonas.get(0);
            }
        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao buscar zona.", erro);
        }
    }

    public static String gerarRelatorioZona(Zona zona) {
        if (zona == null) {
            throw new IllegalArgumentException("Zona invalida.");
        }

        int quantidadePontos;

        try (Connection conexao = ConexaoBanco.conectar(); PreparedStatement comando = conexao.prepareStatement("SELECT COUNT(*) FROM pontos WHERE id_zona = ?")) {
            comando.setInt(1, zona.idZona);

            try (ResultSet resultado = comando.executeQuery()) {
                resultado.next();
                quantidadePontos = resultado.getInt(1);
            }
        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao gerar relatorio da zona.", erro);
        }

        return "Relatorio da zona " + zona.tipo + "\n"
                + "Quantidade de bairros: " + zona.bairros.size() + "\n"
                + "Bairros: " + String.join(", ", zona.bairros) + "\n"
                + "Quantidade de pontos: " + quantidadePontos;
    }

    private static List<String> validarDados(List<String> bairros, Tipo tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo da zona invalido.");
        }

        if (bairros == null || bairros.isEmpty()) {
            throw new IllegalArgumentException("A zona deve ter pelo menos um bairro.");
        }

        Set<String> semRepetidos = new LinkedHashSet<>();

        for (String bairro : bairros) {
            if (bairro == null || bairro.isBlank()) {
                throw new IllegalArgumentException("Bairro invalido.");
            }

            semRepetidos.add(bairro.trim());
        }

        return new ArrayList<>(semRepetidos);
    }

    private static void inserirBairros(Connection conexao, int idZona, List<String> bairros) throws SQLException {
        try (PreparedStatement comando = conexao.prepareStatement("INSERT INTO zona_bairros (id_zona, bairro) VALUES (?, ?)")) {
            for (String bairro : bairros) {
                comando.setInt(1, idZona);
                comando.setString(2, bairro);
                comando.addBatch();
            }

            comando.executeBatch();
        }
    }

    private static List<Zona> montarZonas(ResultSet resultado) throws SQLException {
        Map<Integer, Zona> zonas = new LinkedHashMap<>();

        while (resultado.next()) {
            int id = resultado.getInt("id_zona");
            Zona zona = zonas.get(id);

            if (zona == null) {
                zona = new Zona(id, new ArrayList<>(), Tipo.valueOf(resultado.getString("tipo")));
                zonas.put(id, zona);
            }

            String bairro = resultado.getString("bairro");

            if (bairro != null) {
                zona.bairros.add(bairro);
            }
        }

        return new ArrayList<>(zonas.values());
    }

    public int getIdZona() {
        return idZona;
    }

    public List<String> getBairros() {
        return new ArrayList<>(bairros);
    }

    public Tipo getTipo() {
        return tipo;
    }
}
