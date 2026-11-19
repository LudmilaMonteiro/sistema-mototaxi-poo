import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MotoTaxi extends Usuario {

    private String cnh;
    private int horarioNoturno;
    private int horaFimTurno;
    private boolean eAdministrador;

    private MotoTaxi(String nome, String cpf, String login,
                     String senha, String cnh, int horarioNoturno,
                     int horaFimTurno, boolean eAdministrador) {

        super(nome, cpf, login, senha);
        this.cnh = cnh;
        this.horarioNoturno = horarioNoturno;
        this.horaFimTurno = horaFimTurno;
        this.eAdministrador = eAdministrador;
    }

    public void verMotoTaxi() {
        super.verUsuario();
        System.out.println("CNH: " + cnh);
        System.out.println("Inicio do turno: " + horarioNoturno + "h");
        System.out.println("Fim do turno: " + horaFimTurno + "h");
        System.out.println("Administrador: " + (eAdministrador ? "Sim" : "Nao"));
        System.out.println("Bonus salarial: R$ " + bonusSalarial());
    }

    public static MotoTaxi criarMotoTaxi(
            String nome, String cpf, String login, String senha,
            String cnh, int horarioInicioTurno, int horaFimTurno,
            boolean eAdministrador, Ponto ponto) {

        validarDados(cnh, horarioInicioTurno, horaFimTurno);

        String sqlUsuario = """
            INSERT INTO usuarios
            (nome, cpf, login, senha, ativo, tipo_usuario)
            VALUES (?, ?, ?, crypt(?, gen_salt('bf', 12)), TRUE, 'MOTOTAXI')
            RETURNING id_usuario
            """;

        String sqlMotoTaxi = """
            INSERT INTO mototaxistas
            (id_usuario, cnh, horario_noturno,
             hora_fim_turno, e_administrador)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                int idUsuario;

                try (PreparedStatement comando =
                             conexao.prepareStatement(sqlUsuario)) {

                    comando.setString(1, nome);
                    comando.setString(2, cpf);
                    comando.setString(3, login);
                    comando.setString(4, senha);

                    try (ResultSet resultado = comando.executeQuery()) {
                        resultado.next();
                        idUsuario = resultado.getInt("id_usuario");
                    }
                }

                try (PreparedStatement comando =
                             conexao.prepareStatement(sqlMotoTaxi)) {

                    comando.setInt(1, idUsuario);
                    comando.setString(2, cnh);
                    comando.setInt(3, horarioInicioTurno);
                    comando.setInt(4, horaFimTurno);
                    comando.setBoolean(5, eAdministrador);
                    comando.executeUpdate();
                }

                associarPonto(conexao, idUsuario, ponto);

                conexao.commit();

                return new MotoTaxi(
                    nome, cpf, login, senha, cnh,
                    horarioInicioTurno, horaFimTurno, eAdministrador
                );

            } catch (SQLException | RuntimeException erro) {
                conexao.rollback();
                throw erro;
            }

        } catch (SQLException erro) {
            throw new IllegalStateException(
                "Erro ao cadastrar mototaxista: " + erro.getMessage(), erro
            );
        }
    }

    public static void editarMotoTaxi(
            MotoTaxi mototaxi, String nome, String cpf,
            String login, String senha, String cnh,
            int horarioInicioTurno, int horaFimTurno,
            boolean eAdministrador, Ponto ponto) {

        if (mototaxi == null) {
            throw new IllegalArgumentException("Mototaxista invalido.");
        }

        validarDados(cnh, horarioInicioTurno, horaFimTurno);

        String sqlUsuario = """
            UPDATE usuarios
            SET nome = ?, cpf = ?, login = ?,
                senha = crypt(?, gen_salt('bf', 12))
            WHERE id_usuario = ?
            """;

        String sqlMotoTaxi = """
            UPDATE mototaxistas
            SET cnh = ?, horario_noturno = ?,
                hora_fim_turno = ?, e_administrador = ?
            WHERE id_usuario = ?
            """;

        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                int idUsuario = buscarId(conexao, mototaxi.getLogin());

                try (PreparedStatement comando =
                             conexao.prepareStatement(sqlUsuario)) {

                    comando.setString(1, nome);
                    comando.setString(2, cpf);
                    comando.setString(3, login);
                    comando.setString(4, senha);
                    comando.setInt(5, idUsuario);

                    if (comando.executeUpdate() != 1) {
                        throw new IllegalStateException("Usuario nao encontrado.");
                    }
                }

                try (PreparedStatement comando =
                             conexao.prepareStatement(sqlMotoTaxi)) {

                    comando.setString(1, cnh);
                    comando.setInt(2, horarioInicioTurno);
                    comando.setInt(3, horaFimTurno);
                    comando.setBoolean(4, eAdministrador);
                    comando.setInt(5, idUsuario);

                    if (comando.executeUpdate() != 1) {
                        throw new IllegalStateException("Mototaxista nao encontrado.");
                    }
                }

                if (ponto != null) {
                    String sqlRemover = """
                        DELETE FROM ponto_mototaxistas
                        WHERE id_mototaxista = ?
                        """;

                    try (PreparedStatement comando =
                                 conexao.prepareStatement(sqlRemover)) {

                        comando.setInt(1, idUsuario);
                        comando.executeUpdate();
                    }

                    associarPonto(conexao, idUsuario, ponto);
                }

                conexao.commit();

                mototaxi.atualizarDados(nome, cpf, login, senha);
                mototaxi.cnh = cnh;
                mototaxi.horarioNoturno = horarioInicioTurno;
                mototaxi.horaFimTurno = horaFimTurno;
                mototaxi.eAdministrador = eAdministrador;

            } catch (SQLException | RuntimeException erro) {
                conexao.rollback();
                throw erro;
            }

        } catch (SQLException erro) {
            throw new IllegalStateException(
                "Erro ao editar mototaxista: " + erro.getMessage(), erro
            );
        }
    }

    public static void desativarMotoTaxi(MotoTaxi mototaxi) {

        if (mototaxi == null) {
            throw new IllegalArgumentException("Mototaxista invalido.");
        }

        Usuario.desativarUsuario(mototaxi);
    }

    public static List<MotoTaxi> listarMotoTaxis() {

        List<MotoTaxi> lista = new ArrayList<>();

        String sql = """
            SELECT u.nome, u.cpf, u.login, u.senha, u.ativo,
                   m.cnh, m.horario_noturno,
                   m.hora_fim_turno, m.e_administrador
            FROM usuarios u
            JOIN mototaxistas m ON m.id_usuario = u.id_usuario
            ORDER BY u.nome
            """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {
                lista.add(montarMotoTaxi(resultado));
            }

        } catch (SQLException erro) {
            throw new IllegalStateException(
                "Erro ao listar mototaxistas: " + erro.getMessage(), erro
            );
        }

        return lista;
    }

    public static void deletarMotoTaxi(MotoTaxi mototaxi) {

        if (mototaxi == null) {
            throw new IllegalArgumentException("Mototaxista invalido.");
        }

        String sqlVerificar = """
            SELECT
                (SELECT COUNT(*) FROM corridas
                 WHERE id_mototaxista = ?) +
                (SELECT COUNT(*) FROM pontos
                 WHERE id_administrador = ?)
            """;

        String sqlAssociacoes = """
            DELETE FROM ponto_mototaxistas
            WHERE id_mototaxista = ?
            """;

        String sqlMotoTaxi = """
            DELETE FROM mototaxistas
            WHERE id_usuario = ?
            """;

        String sqlUsuario = """
            DELETE FROM usuarios
            WHERE id_usuario = ?
            """;

        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                int idUsuario = buscarId(conexao, mototaxi.getLogin());

                try (PreparedStatement comando =
                             conexao.prepareStatement(sqlVerificar)) {

                    comando.setInt(1, idUsuario);
                    comando.setInt(2, idUsuario);

                    try (ResultSet resultado = comando.executeQuery()) {
                        resultado.next();

                        if (resultado.getInt(1) > 0) {
                            throw new IllegalStateException(
                                "Mototaxista possui corridas ou administra pontos."
                            );
                        }
                    }
                }

                try (PreparedStatement comando =
                             conexao.prepareStatement(sqlAssociacoes)) {

                    comando.setInt(1, idUsuario);
                    comando.executeUpdate();
                }

                try (PreparedStatement comando =
                             conexao.prepareStatement(sqlMotoTaxi)) {

                    comando.setInt(1, idUsuario);
                    comando.executeUpdate();
                }

                try (PreparedStatement comando =
                             conexao.prepareStatement(sqlUsuario)) {

                    comando.setInt(1, idUsuario);
                    comando.executeUpdate();
                }

                conexao.commit();

            } catch (SQLException | RuntimeException erro) {
                conexao.rollback();
                throw erro;
            }

        } catch (SQLException erro) {
            throw new IllegalStateException(
                "Erro ao excluir mototaxista: " + erro.getMessage(), erro
            );
        }
    }

    public float bonusSalarial() {

        if (eAdministrador) {
            return 350.0f;
        }

        return 0.0f;
    }

    public static List<MotoTaxi> buscarMotoTaxiDisponivel(Ponto ponto) {

        List<MotoTaxi> disponiveis = new ArrayList<>();

        if (ponto == null) {
            return disponiveis;
        }

        int horaAtual = LocalTime.now().getHour();

        for (MotoTaxi mototaxi : listarMotoTaxisPorPonto(ponto)) {

            boolean dentroDoTurno;

            if (mototaxi.horarioNoturno < mototaxi.horaFimTurno) {
                dentroDoTurno =
                    horaAtual >= mototaxi.horarioNoturno &&
                    horaAtual < mototaxi.horaFimTurno;
            } else {
                dentroDoTurno =
                    horaAtual >= mototaxi.horarioNoturno ||
                    horaAtual < mototaxi.horaFimTurno;
            }

            if (!dentroDoTurno) {
                continue;
            }

            String sql = """
                SELECT COUNT(*)
                FROM corridas c
                JOIN usuarios u ON u.id_usuario = c.id_mototaxista
                WHERE u.login = ?
                  AND c.finalizada = FALSE
                """;

            try (Connection conexao = ConexaoBanco.conectar();
                 PreparedStatement comando = conexao.prepareStatement(sql)) {

                comando.setString(1, mototaxi.getLogin());

                try (ResultSet resultado = comando.executeQuery()) {
                    resultado.next();

                    if (resultado.getInt(1) == 0) {
                        disponiveis.add(mototaxi);
                    }
                }

            } catch (SQLException erro) {
                throw new IllegalStateException(
                    "Erro ao verificar disponibilidade: " +
                    erro.getMessage(), erro
                );
            }
        }

        return disponiveis;
    }

    public static List<MotoTaxi> listarMotoTaxisPorPonto(Ponto ponto) {

        List<MotoTaxi> lista = new ArrayList<>();

        if (ponto == null) {
            return lista;
        }

        String sql = """
            SELECT u.nome, u.cpf, u.login, u.senha, u.ativo,
                   m.cnh, m.horario_noturno,
                   m.hora_fim_turno, m.e_administrador
            FROM usuarios u
            JOIN mototaxistas m ON m.id_usuario = u.id_usuario
            JOIN ponto_mototaxistas pm
              ON pm.id_mototaxista = m.id_usuario
            WHERE pm.id_ponto = ?
              AND u.ativo = TRUE
            ORDER BY u.nome
            """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, ponto.getIdPonto());

            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    lista.add(montarMotoTaxi(resultado));
                }
            }

        } catch (SQLException erro) {
            throw new IllegalStateException(
                "Erro ao listar mototaxistas do ponto: " +
                erro.getMessage(), erro
            );
        }

        return lista;
    }

    public static String gerarRelatorioMotoTaxi(MotoTaxi mototaxi) {

        if (mototaxi == null) {
            throw new IllegalArgumentException("Mototaxista invalido.");
        }

        String sql = """
            SELECT
                COUNT(c.id_corrida) AS total,
                COUNT(c.id_corrida) FILTER
                    (WHERE c.finalizada = TRUE) AS finalizadas,
                COUNT(c.id_corrida) FILTER
                    (WHERE c.finalizada = FALSE) AS andamento
            FROM usuarios u
            JOIN mototaxistas m ON m.id_usuario = u.id_usuario
            LEFT JOIN corridas c ON c.id_mototaxista = m.id_usuario
            WHERE u.login = ?
            """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, mototaxi.getLogin());

            try (ResultSet resultado = comando.executeQuery()) {
                resultado.next();

                return "RELATORIO DO MOTOTAXISTA\n" +
                       "Login: " + mototaxi.getLogin() + "\n" +
                       "CNH: " + mototaxi.cnh + "\n" +
                       "Administrador: " +
                       (mototaxi.eAdministrador ? "Sim" : "Nao") + "\n" +
                       "Bonus salarial: R$ " +
                       mototaxi.bonusSalarial() + "\n" +
                       "Total de corridas: " +
                       resultado.getInt("total") + "\n" +
                       "Corridas finalizadas: " +
                       resultado.getInt("finalizadas") + "\n" +
                       "Corridas em andamento: " +
                       resultado.getInt("andamento");
            }

        } catch (SQLException erro) {
            throw new IllegalStateException(
                "Erro ao gerar relatorio: " + erro.getMessage(), erro
            );
        }
    }

    private static MotoTaxi montarMotoTaxi(ResultSet resultado)
            throws SQLException {

        MotoTaxi mototaxi = new MotoTaxi(
            resultado.getString("nome"),
            resultado.getString("cpf"),
            resultado.getString("login"),
            resultado.getString("senha"),
            resultado.getString("cnh"),
            resultado.getInt("horario_noturno"),
            resultado.getInt("hora_fim_turno"),
            resultado.getBoolean("e_administrador")
        );

        mototaxi.definirAtivo(resultado.getBoolean("ativo"));

        return mototaxi;
    }

    private static int buscarId(Connection conexao, String login)
            throws SQLException {

        String sql = """
            SELECT u.id_usuario
            FROM usuarios u
            JOIN mototaxistas m ON m.id_usuario = u.id_usuario
            WHERE u.login = ?
            """;

        try (PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, login);

            try (ResultSet resultado = comando.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getInt("id_usuario");
                }
            }
        }

        throw new IllegalArgumentException("Mototaxista nao encontrado.");
    }

    private static void associarPonto(
            Connection conexao, int idUsuario, Ponto ponto)
            throws SQLException {

        if (ponto == null) {
            return;
        }

        String sql = """
            INSERT INTO ponto_mototaxistas
            (id_ponto, id_mototaxista)
            VALUES (?, ?)
            """;

        try (PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, ponto.getIdPonto());
            comando.setInt(2, idUsuario);
            comando.executeUpdate();
        }
    }

    private static void validarDados(
            String cnh, int horarioInicioTurno, int horaFimTurno) {

        if (cnh == null || cnh.isBlank()) {
            throw new IllegalArgumentException("CNH obrigatoria.");
        }

        if (horarioInicioTurno < 0 || horarioInicioTurno > 23 ||
            horaFimTurno < 0 || horaFimTurno > 23 ||
            horarioInicioTurno == horaFimTurno) {

            throw new IllegalArgumentException("Horario de turno invalido.");
        }
    }
}