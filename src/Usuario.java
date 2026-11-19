import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;


public abstract class Usuario {
    private String nome;
    private String cpf;
    private String login;
    private String senha;
    private boolean ativo;

    protected Usuario(String nome, String cpf, String login, String senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.login = login;
        this.senha = senha;
        this.ativo = true;
    }

    public void verUsuario() {
        System.out.println("Nome: " + nome);
        System.out.println("CPF: " + cpf);
        System.out.println("Login: " + login);
        System.out.println("Status: " + (ativo ? "Ativo" : "Inativo"));
    }

    protected void atualizarDados(String nome, String cpf, String login, String senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.login = login;
        this.senha = senha;
    }

    protected  String getLogin() {
        return login;
    }

    public static void desativarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario invalido.");
        }

        String sql = "UPDATE usuarios SET ativo = FALSE WHERE cpf = ? AND ativo = TRUE";

        try (Connection conexao = ConexaoBanco.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, usuario.cpf);

            int linhas = comando.executeUpdate();
            
            if (linhas == 0) {
                throw new IllegalStateException("Usuario nao encontrado ou ja desativado.");
            }

            usuario.ativo = false;
        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao desativar usuario.", erro);
        }
    }

    public static void editarUsuario(Usuario usuario, String nome, String cpf, String login, String senha) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario invalido.");
        }

        if (nome == null || nome.isBlank() || cpf == null || cpf.isBlank() || login == null || login.isBlank() || senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Todos os campos devem ser preenchidos.");
        }

        String sql = "UPDATE usuarios SET nome = ?, cpf = ?, login = ?, senha = crypt(?, gen_salt('bf', 12)) WHERE cpf = ? AND ativo = TRUE";

        try (Connection conexao = ConexaoBanco.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, nome);
            comando.setString(2, cpf);
            comando.setString(3, login);
            comando.setString(4, senha);
            comando.setString(5, usuario.cpf);

            int linhasAlteradas = comando.executeUpdate();

            if (linhasAlteradas != 1) {
                throw new IllegalStateException("Usuario nao encontrado ou desativado.");
            }

            usuario.nome = nome;
            usuario.cpf = cpf;
            usuario.login = login;
            usuario.senha = senha;
        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao editar usuario.", erro);
        }
    }

    public static boolean autenticar(String login, String senha) {
        if (login == null || login.isBlank() || senha == null || senha.isBlank()) {
            return false;
        }

        String sql = "SELECT 1 FROM usuarios WHERE login = ? AND ativo = TRUE AND senha = crypt(?, senha)";

        try (Connection conexao = ConexaoBanco.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, login);
            comando.setString(2, senha);

            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next();
            }
        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao autenticar usuario.", erro);
        }
    }

    public static List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();

        String sql = "SELECT id_usuario, nome, cpf, login, senha, ativo, tipo_usuario FROM usuarios ORDER BY nome";

        try (Connection conexao = ConexaoBanco.conectar(); PreparedStatement comando = conexao.prepareStatement(sql); ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                String nome = resultado.getString("nome");
                String cpf = resultado.getString("cpf");
                String login = resultado.getString("login");
                String tipoUsuario = resultado.getString("tipo_usuario");
            }
        } catch (SQLException erro) {
            throw new IllegalStateException("Erro ao listar usuarios.", erro);
        }

        return usuarios;
    }

    public static void deletarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario invalido.");
        }

        String buscar = "SELECT id_usuario, tipo_usuario FROM usuarios WHERE cpf = ? FOR UPDATE";

        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                int id;
                String tipo;

                try (PreparedStatement comando = conexao.prepareStatement(buscar)) {
                    comando.setString(1, usuario.cpf);

                    try (ResultSet resultado = comando.executeQuery()) {
                        if (!resultado.next()) {
                            throw new IllegalStateException("Usuario nao encontrado.");
                        }

                        id = resultado.getInt("id_usuario");
                        tipo = resultado.getString("tipo_usuario");
                    }
                }

                if (!tipo.equals("CLIENTE") && !tipo.equals("MOTOTAXI")) {
                    throw new IllegalStateException("Tipo de usuario invalido.");
                }

                if (tipo.equals("MOTOTAXI")) {
                    try (PreparedStatement comando = conexao.prepareStatement( "DELETE FROM ponto_mototaxistas WHERE id_mototaxista = ?")) {
                        comando.setInt(1, id);
                        comando.executeUpdate();
                    }
                }

                String tabela = tipo.equals("CLIENTE") ? "clientes" : "mototaxistas";

                try (PreparedStatement comando = conexao.prepareStatement( "DELETE FROM " + tabela + " WHERE id_usuario = ?")) {
                    comando.setInt(1, id);
                    comando.executeUpdate();
                }

                try (PreparedStatement comando = conexao.prepareStatement("DELETE FROM usuarios WHERE id_usuario = ?")) {
                    comando.setInt(1, id);
                    comando.executeUpdate();
                }

                conexao.commit();
            } catch (SQLException | RuntimeException erro) {
                conexao.rollback();
                throw erro;
            }
        } catch (SQLException erro) {
            if ("23503".equals(erro.getSQLState())) {
                throw new IllegalStateException("Usuario possui corridas ou administra um ponto e nao pode ser excluido.", erro);
            }

            throw new IllegalStateException("Erro ao excluir usuario.", erro);
        }
    }
} 