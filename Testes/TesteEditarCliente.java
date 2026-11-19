import java.util.List;

public class TesteEditarCliente {
    public static void main(String[] args) {
        try {
            List<Cliente> clientes = Cliente.listarClientes();

            for (Cliente cliente : clientes) {
                if (cliente.getLogin().equals("maria_teste")) {
                    Cliente.editarCliente(cliente, "Maria Oliveira", "12345678901", "maria_teste", "Teste@456", false, "Nenhuma");

                    System.out.println("Cliente editado com sucesso");
                    cliente.verCliente();
                }
            }
        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
        }
    }
}