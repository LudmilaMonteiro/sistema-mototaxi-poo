import java.util.List;

public class TesteListarMotoTaxisPorPonto {
    public static void main(String[] args) {
        try {
            Ponto pontoTeste = null;

            for (Ponto ponto : Ponto.listarPontos()) {
                if (ponto.getNomeDoPonto().equals("Ponto Protecao Teste")) {
                    pontoTeste = ponto;
                    break;
                }
            }

            if (pontoTeste == null) {
                System.out.println("Ponto de teste nao encontrado.");
                return;
            }

            System.out.println("PONTO SELECIONADO:");
            pontoTeste.verPonto();

            List<MotoTaxi> mototaxistas = MotoTaxi.listarMotoTaxisPorPonto(pontoTeste);

            System.out.println("\nTotal de mototaxistas: " + mototaxistas.size());

            for (MotoTaxi mototaxi : mototaxistas) {
                System.out.println("------------------------");
                mototaxi.verMotoTaxi();
            }

            if (mototaxistas.isEmpty()) {
                System.out.println("Nenhum mototaxista associado ao ponto.");
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}