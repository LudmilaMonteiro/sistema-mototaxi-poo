public class TesteDeletarUsuarioComCorrida {
    public static void main(String[] args) {
        try {
            Cliente maria = null;

            for (Cliente cliente : Cliente.listarClientes()) {
                if (cliente.getLogin().equals("maria_teste")) {
                    maria = cliente;
                    break;
                }
            }

            if (maria == null) {
                System.out.println("Cliente de teste nao encontrada.");
                return;
            }

            try {
                Usuario.deletarUsuario(maria);

                System.out.println("TESTE REPROVADO: usuario foi excluido!");

            } catch (IllegalStateException erro) {
                System.out.println("Exclusao bloqueada: " + erro.getMessage());
                System.out.println("TESTE APROVADO: historico protegido!");
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}