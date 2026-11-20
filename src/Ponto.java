import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ponto {
    private int idPonto;
    private String nomeDoPonto;
    private String rua;
    private String bairro;
    private MotoTaxi administrador;
    private Zona zona;

    private Ponto(int idPonto, String nomeDoPonto, String rua, String bairro, MotoTaxi administrador, Zona zona) {
        this.idPonto = idPonto;
        this.nomeDoPonto = nomeDoPonto;
        this.rua = rua;
        this.bairro = bairro;
        this.administrador = administrador;
        this.zona = zona;
    }

    public static Ponto criarPonto(String nomeDoPonto, String rua, MotoTaxi administrador, Zona zona, String bairro) {
        validarDados(nomeDoPonto, rua, administrador, zona, bairro);

        String sql = "INSERT INTO pontos (nome_ponto, rua, bairro, id_zona, id_administrador) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING id_ponto";

        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                int idAdministrador = buscarIdAdministrador(conexao, administrador);
                int id;

                try (PreparedStatement comando = conexao.prepareStatement(sql)) {
                    comando.setString(1, nomeDoPonto.trim());
                    comando.setString(2, rua.trim());
                    comando.setString(3, bairro.trim());
                    comando.setInt(4, zona.getIdZona());
                    comando.setInt(5, idAdministrador);

                    try (ResultSet resultado = comando.executeQuery()) {
                        resultado.next();
                        id = resultado.getInt("id_ponto");
                    }
                }

                associarMotoTaxi(conexao, id, idAdministrador);
                conexao.commit();

                return new Ponto(id, nomeDoPonto.trim(), rua.trim(), bairro.trim(), administrador, zona);
            } catch (SQLException | RuntimeException erro) {
                conexao.rollback();
                throw erro;
            }
        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao criar ponto.", erro);
        }
    }

    public void verPonto() {
        System.out.println("Ponto: " + nomeDoPonto);
        System.out.println("Rua: " + rua);
        System.out.println("Bairro: " + bairro);
        System.out.println("Zona: " + zona.getTipo());
        System.out.println("Administrador: " + administrador.getLogin());
    }

    public static void editarPonto(Ponto ponto, String nomeDoPonto, String rua, MotoTaxi administrador,
                                   List<MotoTaxi> mototaxistas, Zona zona, String bairro) {
        if (ponto == null) {
            throw new IllegalArgumentException("Ponto invalido.");
        }

        validarDados(nomeDoPonto, rua, administrador, zona, bairro);

        String sql = "UPDATE pontos SET nome_ponto = ?, rua = ?, bairro = ?, id_zona = ?, id_administrador = ? "
                + "WHERE id_ponto = ?";

        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                int idAdministrador = buscarIdAdministrador(conexao, administrador);

                try (PreparedStatement comando = conexao.prepareStatement(sql)) {
                    comando.setString(1, nomeDoPonto.trim());
                    comando.setString(2, rua.trim());
                    comando.setString(3, bairro.trim());
                    comando.setInt(4, zona.getIdZona());
                    comando.setInt(5, idAdministrador);
                    comando.setInt(6, ponto.idPonto);

                    if (comando.executeUpdate() != 1) {
                        throw new IllegalStateException("Ponto nao encontrado.");
                    }
                }

                if (mototaxistas != null) {
                    try (PreparedStatement comando = conexao.prepareStatement("DELETE FROM ponto_mototaxistas WHERE id_ponto = ?")) {
                        comando.setInt(1, ponto.idPonto);
                        comando.executeUpdate();
                    }

                    for (MotoTaxi mototaxi : mototaxistas) {
                        if (mototaxi == null) {
                            throw new IllegalArgumentException("Mototaxista invalido.");
                        }

                        associarMotoTaxi(conexao, ponto.idPonto, buscarIdMotoTaxi(conexao, mototaxi));
                    }
                }

                associarMotoTaxi(conexao, ponto.idPonto, idAdministrador);
                conexao.commit();

                ponto.nomeDoPonto = nomeDoPonto.trim();
                ponto.rua = rua.trim();
                ponto.bairro = bairro.trim();
                ponto.administrador = administrador;
                ponto.zona = zona;
            } catch (SQLException | RuntimeException erro) {
                conexao.rollback();
                throw erro;
            }
        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao editar ponto.", erro);
        }
    }

    public static List<Ponto> listarPontos() {
        return consultar("", null);
    }

    public static void deletarPonto(Ponto ponto) {
        if (ponto == null) {
            throw new IllegalArgumentException("Ponto invalido.");
        }

        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                try (PreparedStatement comando = conexao.prepareStatement("DELETE FROM ponto_mototaxistas WHERE id_ponto = ?")) {
                    comando.setInt(1, ponto.idPonto);
                    comando.executeUpdate();
                }

                try (PreparedStatement comando = conexao.prepareStatement("DELETE FROM pontos WHERE id_ponto = ?")) {
                    comando.setInt(1, ponto.idPonto);

                    if (comando.executeUpdate() != 1) {
                        throw new IllegalStateException("Ponto nao encontrado.");
                    }
                }

                conexao.commit();
            } catch (SQLException | RuntimeException erro) {
                conexao.rollback();
                throw erro;
            }
        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao excluir ponto.", erro);
        }
    }

    public static List<Ponto> buscarPorBairro(String bairro) {
        if (bairro == null || bairro.isBlank()) {
            throw new IllegalArgumentException("Bairro invalido.");
        }

        return consultar("WHERE LOWER(p.bairro) = LOWER(?)", new Object[]{bairro.trim()});
    }

    public static List<Ponto> buscarPorProximidade(String rua, String bairro, Zona zona) {
        if (rua == null || rua.isBlank() || bairro == null || bairro.isBlank() || zona == null) {
            throw new IllegalArgumentException("Rua, bairro e zona sao obrigatorios.");
        }

        return consultar("WHERE p.id_zona = ? AND (LOWER(p.rua) = LOWER(?) OR LOWER(p.bairro) = LOWER(?))",
                new Object[]{zona.getIdZona(), rua.trim(), bairro.trim()});
    }

    public static List<Ponto> buscarPorRua(String rua) {
        if (rua == null || rua.isBlank()) {
            throw new IllegalArgumentException("Rua invalida.");
        }

        return consultar("WHERE LOWER(p.rua) = LOWER(?)", new Object[]{rua.trim()});
    }

    public static String gerarRelatorioPonto(Ponto ponto) {
        if (ponto == null) {
            throw new IllegalArgumentException("Ponto invalido.");
        }

        int quantidadeMototaxistas;

        String sql = "SELECT COUNT(*) FROM ponto_mototaxistas WHERE id_ponto = ?";

        try (Connection conexao = ConexaoBanco.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, ponto.idPonto);

            try (ResultSet resultado = comando.executeQuery()) {
                resultado.next();
                quantidadeMototaxistas = resultado.getInt(1);
            }
        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao gerar relatorio do ponto.", erro);
        }

        return "Relatorio do ponto " + ponto.nomeDoPonto + "\n"
                + "Rua: " + ponto.rua + "\n"
                + "Bairro: " + ponto.bairro + "\n"
                + "Zona: " + ponto.zona.getTipo() + "\n"
                + "Administrador: " + ponto.administrador.getLogin() + "\n"
                + "Quantidade de mototaxistas: " + quantidadeMototaxistas;
    }

    private static void validarDados(String nomeDoPonto, String rua, MotoTaxi administrador, Zona zona, String bairro) {
        if (nomeDoPonto == null || nomeDoPonto.isBlank() || rua == null || rua.isBlank()
                || bairro == null || bairro.isBlank()) {
            throw new IllegalArgumentException("Nome, rua e bairro sao obrigatorios.");
        }

        if (administrador == null) {
            throw new IllegalArgumentException("O ponto deve ter um administrador.");
        }

        if (zona == null) {
            throw new IllegalArgumentException("O ponto deve pertencer a uma zona.");
        }

        boolean bairroDaZona = false;

        for (String bairroZona : zona.getBairros()) {
            if (bairroZona.equalsIgnoreCase(bairro.trim())) {
                bairroDaZona = true;
            }
        }

        if (!bairroDaZona) {
            throw new IllegalArgumentException("O bairro informado nao pertence a zona.");
        }
    }

    private static int buscarIdAdministrador(Connection conexao, MotoTaxi administrador) throws SQLException {
        String sql = "SELECT u.id_usuario FROM usuarios u "
                + "JOIN mototaxistas m ON m.id_usuario = u.id_usuario "
                + "WHERE u.login = ? AND u.ativo = TRUE AND m.e_administrador = TRUE";

        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, administrador.getLogin());

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt("id_usuario");
                }
            }
        }

        throw new IllegalArgumentException("O administrador deve ser um mototaxista ativo e marcado como administrador.");
    }

    private static int buscarIdMotoTaxi(Connection conexao, MotoTaxi mototaxi) throws SQLException {
        String sql = "SELECT u.id_usuario FROM usuarios u "
                + "JOIN mototaxistas m ON m.id_usuario = u.id_usuario WHERE u.login = ?";

        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, mototaxi.getLogin());

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt("id_usuario");
                }
            }
        }

        throw new IllegalArgumentException("Mototaxista nao encontrado.");
    }

    // Um mototaxista pertence a um unico ponto (mesma regra de MotoTaxi.editarMotoTaxi).
    private static void associarMotoTaxi(Connection conexao, int idPonto, int idMotoTaxi) throws SQLException {
        try (PreparedStatement comando = conexao.prepareStatement("DELETE FROM ponto_mototaxistas WHERE id_mototaxista = ?")) {
            comando.setInt(1, idMotoTaxi);
            comando.executeUpdate();
        }

        try (PreparedStatement comando = conexao.prepareStatement(
                "INSERT INTO ponto_mototaxistas (id_ponto, id_mototaxista) VALUES (?, ?)")) {
            comando.setInt(1, idPonto);
            comando.setInt(2, idMotoTaxi);
            comando.executeUpdate();
        }
    }

    private static List<Ponto> consultar(String filtro, Object[] parametros) {
        String sql = "SELECT p.id_ponto, p.nome_ponto, p.rua, p.bairro, p.id_zona, u.login AS login_administrador "
                + "FROM pontos p JOIN usuarios u ON u.id_usuario = p.id_administrador "
                + filtro + " ORDER BY p.nome_ponto, p.id_ponto";

        Map<Integer, Zona> zonas = new HashMap<>();

        for (Zona zona : Zona.listarZonas()) {
            zonas.put(zona.getIdZona(), zona);
        }

        Map<String, MotoTaxi> mototaxistas = new HashMap<>();

        for (MotoTaxi mototaxi : MotoTaxi.listarMotoTaxis()) {
            mototaxistas.put(mototaxi.getLogin(), mototaxi);
        }

        List<Ponto> pontos = new ArrayList<>();

        try (Connection conexao = ConexaoBanco.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)) {
            if (parametros != null) {
                for (int i = 0; i < parametros.length; i++) {
                    comando.setObject(i + 1, parametros[i]);
                }
            }

            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    pontos.add(new Ponto(
                            resultado.getInt("id_ponto"),
                            resultado.getString("nome_ponto"),
                            resultado.getString("rua"),
                            resultado.getString("bairro"),
                            mototaxistas.get(resultado.getString("login_administrador")),
                            zonas.get(resultado.getInt("id_zona"))));
                }
            }
        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao buscar pontos.", erro);
        }

        return pontos;
    }

    public int getIdPonto() {
        return idPonto;
    }

    public String getNomeDoPonto() {
        return nomeDoPonto;
    }

    public String getRua() {
        return rua;
    }

    public String getBairro() {
        return bairro;
    }

    public MotoTaxi getAdministrador() {
        return administrador;
    }

    public Zona getZona() {
        return zona;
    }

    public List<MotoTaxi> getMototaxistas() {
        return MotoTaxi.listarMotoTaxisPorPonto(this);
    }
}
