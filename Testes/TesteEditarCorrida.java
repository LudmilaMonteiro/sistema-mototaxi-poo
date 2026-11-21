public class TesteEditarCorrida {
    public static void main(String[] args) {
        try {
            Corrida corridaTeste = null;
            Cliente maria = null;
            MotoTaxi pedro = null;

            for (Corrida corrida : Corrida.listarCorridas()) {
                if (corrida.getClass() == Corrida.class) {
                    for (Corrida teste : Corrida.listarCorridas()) {
                        if (teste.toString() != null) {
                            corridaTeste = corrida;
                            break;
                        }
                    }
                    break;
                }
            }

            for (Cliente cliente : Cliente.listarClientes()) {
                if (cliente.getLogin().equals("maria_teste")) {
                    maria = cliente;
                    break;
                }
            }

            for (MotoTaxi mototaxi : MotoTaxi.listarMotoTaxis()) {
                if (mototaxi.getLogin().equals("pedro_disponivel_teste")) {
                    pedro = mototaxi;
                    break;
                }
            }

            if (corridaTeste == null || maria == null || pedro == null) {
                System.out.println("Dados de teste nao encontrados.");
                return;
            }

            System.out.println("ANTES DA EDICAO:");
            corridaTeste.verCorrida();

            Corrida.editarCorrida(
                corridaTeste,
                "Bairro Protecao Teste",
                "Rua Origem Editada",
                "Bairro Protecao Teste",
                "Rua Destino Editada",
                maria,
                pedro
            );

            System.out.println("\nDEPOIS DA EDICAO:");
            corridaTeste.verCorrida();

            System.out.println("\nCorrida editada com sucesso!");

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}