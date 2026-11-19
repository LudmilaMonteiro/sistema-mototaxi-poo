public class TesteDeletarCliente {
    public static void main(String[] args) {
        try {
            Cliente cliente = Cliente.criarCliente(
                "Julia Teste",
                "11122233344",
                "cliente_exclusao",
                "Teste@789",
                false,
                "Nenhuma"
            );

            System.out.println("Cliente criado:");
            cliente.verCliente();

            Cliente.deletarCliente(cliente);

            System.out.println("Cliente excluido com sucesso!");

            System.out.println("Login depois da exclusao: " +
                Usuario.autenticar("cliente_exclusao", "Teste@789"));

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}
