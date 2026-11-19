public class TesteDesativarCliente {
    public static void main(String[] args) {
        try {
            Cliente cliente = null;

            for (Cliente c : Cliente.listarClientes()) {
                if (c.getLogin().equals("cliente_desativacao")) {
                    cliente = c;
                    break;
                }
            }

            if (cliente == null) {
                cliente = Cliente.criarCliente(
                    "Ana Teste",
                    "98765432100",
                    "cliente_desativacao",
                    "Teste@123",
                    false,
                    "Nenhuma"
                );
            }

            System.out.println("Antes da desativacao:");
            cliente.verCliente();

            System.out.println("Login antes: " +
                Usuario.autenticar("cliente_desativacao", "Teste@123"));

            Cliente.desativarCliente(cliente);

            System.out.println("Depois da desativacao:");
            cliente.verCliente();

            System.out.println("Login depois: " +
                Usuario.autenticar("cliente_desativacao", "Teste@123"));

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}
