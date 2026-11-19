public class TesteRelatorioCliente {
    public static void main(String[] args) {
        try {
            for (Cliente cliente : Cliente.listarClientes()) {
                if (cliente.getLogin().equals("maria_teste")) {
                    System.out.println(
                        Cliente.gerarRelatorioCliente(cliente)
                    );
                }
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
        }
    }
}
