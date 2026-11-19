public class TesteCliente {
    public static void main(String[] args) {
        try {
            Cliente cliente = Cliente.criarCliente("Maria Silva", "12345678901", "maria_teste", "Teste@123", true, "Mobilidade reduzida");

            System.out.println("Cliente cadastrado com sucesso!");
            cliente.verCliente();

            System.out.println("Login correto: " + Usuario.autenticar("maria_teste", "Teste@123"));
            System.out.println("Senha incorreta: " + Usuario.autenticar("maria_teste", "senha_errada"));
        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
        }
    }
}