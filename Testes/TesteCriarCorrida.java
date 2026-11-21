import java.util.List;

public class TesteCriarCorrida {
    public static void main(String[] args) {
        try {
            Cliente maria = null;
            MotoTaxi pedro = null;

            List<Cliente> clientes = Cliente.listarClientes();

            for (Cliente cliente : clientes) {
                if (cliente.getLogin().equals("maria_teste")) {
                    maria = cliente;
                    break;
                }
            }

            List<MotoTaxi> mototaxistas = MotoTaxi.listarMotoTaxis();

            for (MotoTaxi mototaxi : mototaxistas) {
                if (mototaxi.getLogin().equals("pedro_disponivel_teste")) {
                    pedro = mototaxi;
                    break;
                }
            }

            if (maria == null || pedro == null) {
                System.out.println("Cliente ou mototaxista nao encontrado.");
                return;
            }

            Corrida corrida = Corrida.criarCorrida(
                "Bairro Protecao Teste",
                "Rua Origem Corrida Teste",
                "Bairro Protecao Teste",
                "Rua Destino Corrida Teste",
                maria,
                pedro
            );

            System.out.println("Corrida cadastrada com sucesso!");
            System.out.println("------------------------");

            corrida.verCorrida();

            System.out.println("------------------------");

            if (corrida.valorCorrida() == 7.0f) {
                System.out.println("TESTE APROVADO: valor correto!");
            } else {
                System.out.println("TESTE REPROVADO: valor incorreto!");
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}