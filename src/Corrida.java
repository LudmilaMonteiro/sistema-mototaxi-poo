
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Corrida {
    private String bairroOrigem;
    private String ruaOrigem;
    private String ruaDestino;
    private String bairroDestino;
    private boolean finalizada;

    private Corrida(String bairroOrigem, String ruaOrigem, String bairroDestino, String ruaDestino, boolean finalizada) {
        this.bairroOrigem = bairroOrigem;
        this.ruaOrigem = ruaOrigem;
        this.bairroDestino = bairroDestino;
        this.ruaDestino = ruaDestino;
        this.finalizada = finalizada;
    }

    public static Corrida criarCorrida(String bairroOrigem, String ruaOrigem, String bairroDestino, String ruaDestino, Cliente cliente, MotoTaxi mototaxi) {
        validarEnderecos(bairroOrigem, ruaOrigem, bairroDestino, ruaDestino);
        validarZonas(bairroOrigem, bairroDestino);

        if (cliente == null || mototaxi == null) {
            throw new IllegalArgumentException("Cliente e mototaxista sao obrigatorios.");
        }

        String sql = """
            INSERT INTO corridas
            (bairro_origem, rua_origem, bairro_destino,
             rua_destino, finalizada, id_cliente, id_mototaxista)
            VALUES (?, ?, ?, ?, FALSE, ?, ?)
            """;

        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                int clienteBanco = buscarUsuario(conexao, cliente.getLogin(), "CLIENTE");
                int mototaxistaBanco = buscarUsuario(conexao, mototaxi.getLogin(), "MOTOTAXI");

                bloquearMototaxista(conexao, mototaxistaBanco);
                verificarDisponibilidade(conexao, mototaxistaBanco, 0);

                try (PreparedStatement comando = conexao.prepareStatement(sql)) {
                    comando.setString(1, bairroOrigem.trim());
                    comando.setString(2, ruaOrigem.trim());
                    comando.setString(3, bairroDestino.trim());
                    comando.setString(4, ruaDestino.trim());
                    comando.setInt(5, clienteBanco);
                    comando.setInt(6, mototaxistaBanco);

                    comando.executeUpdate();
                }

                conexao.commit();

                return new Corrida(bairroOrigem.trim(), ruaOrigem.trim(), bairroDestino.trim(), ruaDestino.trim(), false);

            } catch (SQLException | RuntimeException erro) {
                conexao.rollback();
                throw erro;
            }

        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao criar corrida: " + erro.getMessage(), erro);
        }
    }

    public void verCorrida() {
        System.out.println("Origem: " + ruaOrigem + ", " + bairroOrigem);
        System.out.println("Destino: " + ruaDestino + ", " + bairroDestino);
        System.out.println("Valor: R$ " + valorCorrida());
        System.out.println("Finalizada: " + (finalizada ? "Sim" : "Nao"));
    }

    public static void editarCorrida(Corrida corrida, String bairroOrigem, String ruaOrigem, String bairroDestino, String ruaDestino, Cliente cliente, MotoTaxi mototaxi) {
        if (corrida == null || cliente == null || mototaxi == null) {
            throw new IllegalArgumentException("Dados invalidos.");
        }

        validarEnderecos(bairroOrigem, ruaOrigem, bairroDestino, ruaDestino);
        validarZonas(bairroOrigem, bairroDestino);

        String sql = """
            UPDATE corridas
            SET bairro_origem = ?, rua_origem = ?,
                bairro_destino = ?, rua_destino = ?,
                id_cliente = ?, id_mototaxista = ?
            WHERE id_corrida = ? AND finalizada = FALSE
            """;

        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                int corridaBanco = localizarCorrida(conexao, corrida);

                if (corridaBanco == 0 || corrida.finalizada) {
                    throw new IllegalStateException("Corrida inexistente ou finalizada.");
                }

                int clienteBanco = buscarUsuario(conexao, cliente.getLogin(), "CLIENTE");
                int mototaxistaBanco = buscarUsuario(conexao, mototaxi.getLogin(), "MOTOTAXI");

                bloquearMototaxista(conexao, mototaxistaBanco);

                verificarDisponibilidade(conexao, mototaxistaBanco, corridaBanco);

                try (PreparedStatement comando = conexao.prepareStatement(sql)) {
                    comando.setString(1, bairroOrigem.trim());
                    comando.setString(2, ruaOrigem.trim());
                    comando.setString(3, bairroDestino.trim());
                    comando.setString(4, ruaDestino.trim());
                    comando.setInt(5, clienteBanco);
                    comando.setInt(6, mototaxistaBanco);
                    comando.setInt(7, corridaBanco);

                    if (comando.executeUpdate() != 1) {
                        throw new IllegalStateException("Nao foi possivel editar a corrida.");
                    }
                }

                conexao.commit();

                corrida.bairroOrigem = bairroOrigem.trim();
                corrida.ruaOrigem = ruaOrigem.trim();
                corrida.bairroDestino = bairroDestino.trim();
                corrida.ruaDestino = ruaDestino.trim();

            } catch (SQLException | RuntimeException erro) {
                conexao.rollback();
                throw erro;
            }

        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao editar corrida: " + erro.getMessage(), erro);
        }
    }

    public float valorCorrida() {

        Zona origem = Zona.buscarZonaPorBairro(bairroOrigem);
        Zona destino = Zona.buscarZonaPorBairro(bairroDestino);

        if (origem == null || destino == null) {
            throw new IllegalStateException("Nao foi possivel identificar as zonas.");
        }

        int diferenca = Math.abs(origem.getTipo().ordinal() - destino.getTipo().ordinal());

        if (diferenca == 0) {
            return 7.0f;
        }

        if (diferenca == 1) {
            return 10.0f;
        }

        return 15.0f;
    }

    public static List<Corrida> listarCorridas() {
        List<Corrida> corridas = new ArrayList<>();

        String sql = """
            SELECT bairro_origem, rua_origem, bairro_destino,
                   rua_destino, finalizada
            FROM corridas
            ORDER BY id_corrida
            """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {

                Corrida corrida = new Corrida(
                    resultado.getString("bairro_origem"),
                    resultado.getString("rua_origem"),
                    resultado.getString("bairro_destino"),
                    resultado.getString("rua_destino"),
                    resultado.getBoolean("finalizada")
                );

                corridas.add(corrida);
            }

        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao listar corridas: " + erro.getMessage(), erro);
        }
        return corridas;
    }

    public void finalizarCorrida() {
        if (finalizada) {
            throw new IllegalStateException("Corrida ja finalizada.");
        }

        String sql = """
            UPDATE corridas
            SET finalizada = TRUE
            WHERE id_corrida = ? AND finalizada = FALSE
            """;

        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                int corridaBanco = localizarCorrida(conexao, this);

                if (corridaBanco == 0) {
                    throw new IllegalStateException("Corrida nao encontrada.");
                }

                try (PreparedStatement comando = conexao.prepareStatement(sql)) {
                    comando.setInt(1, corridaBanco);

                    if (comando.executeUpdate() != 1) {
                        throw new IllegalStateException("Nao foi possivel finalizar a corrida.");
                    }
                }

                conexao.commit();
                finalizada = true;

            } catch (SQLException | RuntimeException erro) {
                conexao.rollback();
                throw erro;
            }

        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao finalizar corrida: " + erro.getMessage(), erro);
        }
    }

    public static void deletarCorrida(Corrida corrida) {
        if (corrida == null) {
            throw new IllegalArgumentException("Corrida invalida.");
        }

        String sql = "DELETE FROM corridas WHERE id_corrida = ?";

        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                int corridaBanco = localizarCorrida(conexao, corrida);

                if (corridaBanco == 0) {
                    throw new IllegalStateException("Corrida nao encontrada.");
                }

                try (PreparedStatement comando = conexao.prepareStatement(sql)) {
                    comando.setInt(1, corridaBanco);

                    if (comando.executeUpdate() != 1) {
                        throw new IllegalStateException("Nao foi possivel excluir a corrida.");
                    }
                }

                conexao.commit();

            } catch (SQLException | RuntimeException erro) {
                conexao.rollback();
                throw erro;
            }

        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao excluir corrida: " + erro.getMessage(), erro);
        }
    }

    public static String gerarRelatorioCorridas() {
        List<Corrida> corridas = listarCorridas();

        int finalizadas = 0;
        int emAndamento = 0;
        float valorTotal = 0;

        for (Corrida corrida : corridas) {

            if (corrida.finalizada) {
                finalizadas++;
                valorTotal += corrida.valorCorrida();
            } else {
                emAndamento++;
            }
        }

        return "RELATORIO DE CORRIDAS\n" +
               "Total de corridas: " + corridas.size() + "\n" +
               "Corridas finalizadas: " + finalizadas + "\n" +
               "Corridas em andamento: " + emAndamento + "\n" +
               "Valor das corridas finalizadas: R$ " + valorTotal;
    }

    private static int buscarUsuario(Connection conexao, String login, String tipo) throws SQLException {
        String sql = """
            SELECT id_usuario
            FROM usuarios
            WHERE login = ? AND tipo_usuario = ?
              AND ativo = TRUE
            """;

        try (PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, login);
            comando.setString(2, tipo);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt("id_usuario");
                }
            }
        }

        throw new IllegalArgumentException("Usuario nao encontrado ou inativo: " + login);
    }

    private static void bloquearMototaxista(Connection conexao, int mototaxistaBanco) throws SQLException {
        String sql = """
            SELECT id_usuario FROM mototaxistas
            WHERE id_usuario = ? FOR UPDATE
            """;

        try (PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, mototaxistaBanco);

            try (ResultSet resultado = comando.executeQuery()) {
                if (!resultado.next()) {
                    throw new IllegalStateException("Mototaxista nao encontrado.");
                }
            }
        }
    }

    private static void verificarDisponibilidade(Connection conexao, int mototaxistaBanco, int corridaAtual) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM corridas
            WHERE id_mototaxista = ?
              AND finalizada = FALSE
              AND id_corrida <> ?
            """;

        try (PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, mototaxistaBanco);
            comando.setInt(2, corridaAtual);

            try (ResultSet resultado = comando.executeQuery()) {

                resultado.next();

                if (resultado.getInt(1) > 0) {
                    throw new IllegalStateException("Mototaxista ja possui uma corrida em andamento.");
                }
            }
        }
    }

    private static int localizarCorrida(Connection conexao, Corrida corrida) throws SQLException {
        String sql = """
            SELECT id_corrida
            FROM corridas
            WHERE bairro_origem = ?
              AND rua_origem = ?
              AND bairro_destino = ?
              AND rua_destino = ?
              AND finalizada = ?
            FOR UPDATE
            """;

        int encontrado = 0;
        int quantidade = 0;

        try (PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, corrida.bairroOrigem);
            comando.setString(2, corrida.ruaOrigem);
            comando.setString(3, corrida.bairroDestino);
            comando.setString(4, corrida.ruaDestino);
            comando.setBoolean(5, corrida.finalizada);

            try (ResultSet resultado = comando.executeQuery()) {

                while (resultado.next()) {
                    encontrado = resultado.getInt("id_corrida");
                    quantidade++;
                }
            }
        }

        if (quantidade > 1) {
            throw new IllegalStateException("Existem corridas iguais. Nao e seguro alterar.");
        }

        return encontrado;
    }

    private static void validarEnderecos(String bairroOrigem, String ruaOrigem, String bairroDestino, String ruaDestino) {
        if (bairroOrigem == null || bairroOrigem.isBlank() ||
            ruaOrigem == null || ruaOrigem.isBlank() ||
            bairroDestino == null || bairroDestino.isBlank() ||
            ruaDestino == null || ruaDestino.isBlank()) {

            throw new IllegalArgumentException("Todos os enderecos devem ser preenchidos.");
        }
    }

    private static void validarZonas(
            String bairroOrigem, String bairroDestino) {

        if (Zona.buscarZonaPorBairro(bairroOrigem) == null ||
            Zona.buscarZonaPorBairro(bairroDestino) == null) {

            throw new IllegalArgumentException("Os bairros precisam estar cadastrados em zonas.");
        }
    }
}
