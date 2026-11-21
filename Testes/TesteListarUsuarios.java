import java.util.List;

public class TesteListarUsuarios {
    public static void main(String[] args) {
        try {
            List<Usuario> usuarios = Usuario.listarUsuarios();

            System.out.println("Total de usuarios: " + usuarios.size());

            for (Usuario usuario : usuarios) {
                System.out.println("------------------------");
                usuario.verUsuario();
            }

            if (!usuarios.isEmpty()) {
                System.out.println("TESTE APROVADO: usuarios encontrados!");
            } else {
                System.out.println("TESTE REPROVADO: lista vazia!");
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}