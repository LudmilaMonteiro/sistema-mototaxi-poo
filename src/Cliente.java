import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList; 

public class Cliente extends Usuario {
    
    private boolean acessibilidade;
    private String tipoDeDificuldade; 

    public Cliente(String nome, String cpf, String login, String senha, boolean acessibilidade, String tipoDeDificuldade) {
        super(nome, cpf, login, senha); 
        this.acessibilidade = acessibilidade;
        this.tipoDeDificuldade = tipoDeDificuldade;
    }

    public static Cliente criarCliente(String nome, String cpf, String login, String senha, boolean acessibilidade, String tipoDeDificuldade) {
        if (nome == null || nome.isBlank() || cpf == null || cpf.isBlank() || login == null || login.isBlank() || senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Preencha os campos obrigatórios.");
        }

        String sqlUsuario = "INSERT INTO usuarios (nome, cpf, login, senha, ativo, tipo_usuario) " + "VALUES (?, ?, ?, crypt(?, gen_salt('bf', 12)), TRUE, 'CLIENTE') RETURNING id_usuario";
        String sqlCliente = "INSERT INTO clientes (id_usuario, acessibilidade, tipo_de_dificuldade) VALUES (?, ?, ?)";
        
        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                int id;

                try (PreparedStatement comando = conexao.prepareStatement(sqlUsuario)) {
                    comando.setString(1, nome);
                    comando.setString(2, cpf);
                    comando.setString(3, login);
                    comando.setString(4, senha);

                    try (ResultSet resultado = comando.executeQuery()) {
                        resultado.next();
                        id = resultado.getInt(1);
                    }
                }

                try (PreparedStatement comando = conexao.prepareStatement(sqlCliente)) {
                    comando.setInt(1, id);
                    comando.setBoolean(2, acessibilidade);
                    comando.setString(3, tipoDeDificuldade);
                    comando.executeUpdate();
                }

                conexao.commit();

                return new Cliente(nome, cpf, login, senha, acessibilidade, tipoDeDificuldade);
            } catch (SQLException | RuntimeException erro) {
                conexao.rollback();
                throw erro;
            } 
        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao cadastrar cliente.", erro);
        }
    }

    public static void editarCliente(Cliente cliente, String nome, String cpf, String login, String senha, boolean acessibilidade, String tipoDeDificuldade) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente invalido.");
        }

        if (nome == null || nome.isBlank() || cpf == null || cpf.isBlank() || login == null || login.isBlank() || senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Preencha os campos obrigatorios.");
        }

        String sqlUsuario = "UPDATE usuarios SET nome = ?, cpf = ?, login = ?, " + "senha = crypt(?, gen_salt('bf', 12)) " + "WHERE login = ? AND ativo = TRUE RETURNING id_usuario";
        String sqlCliente = "UPDATE clientes SET acessibilidade = ?, "  + "tipo_de_dificuldade = ? WHERE id_usuario = ?";

        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                int id;

                try (PreparedStatement comando = conexao.prepareStatement(sqlUsuario)) {
                    comando.setString(1, nome);
                    comando.setString(2, cpf);
                    comando.setString(3, login);
                    comando.setString(4, senha);
                    comando.setString(5, cliente.getLogin());

                    try (ResultSet resultado = comando.executeQuery()) {
                        if (!resultado.next()) {
                            throw new IllegalStateException("Cliente nao encontrado ou desativado.");
                        }

                        id = resultado.getInt(1);
                    }
                }

                try (PreparedStatement comando = conexao.prepareStatement(sqlCliente)) {
                    comando.setBoolean(1, acessibilidade);
                    comando.setString(2, tipoDeDificuldade);
                    comando.setInt(3, id);

                    if (comando.executeUpdate() != 1) {
                        throw new IllegalStateException("Cadastro do cliente nao encontrado.");
                    }
                }

                conexao.commit();

                cliente.atualizarDados(nome, cpf, login, senha);
                cliente.acessibilidade = acessibilidade;
                cliente.tipoDeDificuldade = tipoDeDificuldade;
            } catch (SQLException | RuntimeException erro) {
                conexao.rollback();
                throw erro;
            }
        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao editar cliente.", erro);
        }
    }

    public static List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();

        String sql = "SELECT u.nome, u.cpf, u.login, u.senha, u.ativo, "
                + "c.acessibilidade, c.tipo_de_dificuldade "
                + "FROM usuarios u JOIN clientes c ON u.id_usuario = c.id_usuario "
                + "WHERE u.tipo_usuario = 'CLIENTE' ORDER BY u.nome";

        try (Connection conexao = ConexaoBanco.conectar(); PreparedStatement comando = conexao.prepareStatement(sql); ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                Cliente cliente = new Cliente(
                    resultado.getString("nome"),
                    resultado.getString("cpf"),
                    resultado.getString("login"),
                    resultado.getString("senha"),
                    resultado.getBoolean("acessibilidade"),
                    resultado.getString("tipo_de_dificuldade")
                );

                clientes.add(cliente);
            }
        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao listar clientes.", erro);
        }

        return clientes;
    }

    public static void desativarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente inválido.");
        }

        Usuario.desativarUsuario(cliente);
    }

    public void verCliente() {
        super.verUsuario();
        System.out.println("Acessibilidade: " + (acessibilidade ? "Sim" : "Nao"));
        System.out.println("Tipo de dificuldade: " + tipoDeDificuldade);
    }

    public static void deletarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente invalido.");
        }

        Usuario.deletarUsuario(cliente);
    }

    public static String gerarRelatorioCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente invalido.");
        }

        String sql = "SELECT COUNT(*) FROM corridas c " + "JOIN usuarios u ON c.id_cliente = u.id_usuario " + "WHERE u.login = ?";

        int totalCorridas = 0;

        try (Connection conexao = ConexaoBanco.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, cliente.getLogin());

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    totalCorridas = resultado.getInt(1);
                }
            }
        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao gerar relatorio do cliente.", erro);
        }

        return "RELATORIO DO CLIENTE\n"
               + "Login: " + cliente.getLogin() + "\n"
               + "Acessibilidade: " + (cliente.acessibilidade ? "Sim" : "Nao") + "\n"
               + "Tipo de dificuldade: " + cliente.tipoDeDificuldade + "\n"
               + "Total de corridas: " + totalCorridas;
    }
}