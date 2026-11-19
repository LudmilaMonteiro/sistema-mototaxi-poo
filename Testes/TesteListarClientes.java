import java.util.List;

public class TesteListarClientes {
    public static void main(String[] args) {
        try {
            List<Cliente> clientes = Cliente.listarClientes();

            System.out.println("Total de clientes: " + clientes.size());

            for (Cliente cliente : clientes) {
                System.out.println("------------------------");
                cliente.verCliente();
            }
        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}
