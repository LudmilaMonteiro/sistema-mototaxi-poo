import java.time.LocalTime;
import java.util.List;

public class TesteMotoTaxiDisponivel {
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

            System.out.println("Horario atual: " + LocalTime.now());
            System.out.println("Ponto: " + pontoTeste.getNomeDoPonto());

            List<MotoTaxi> associados = MotoTaxi.listarMotoTaxisPorPonto(pontoTeste);

            List<MotoTaxi> disponiveis = MotoTaxi.buscarMotoTaxiDisponivel(pontoTeste);

            System.out.println("Mototaxistas associados: " + associados.size());

            System.out.println("Mototaxistas disponiveis: " + disponiveis.size());

            for (MotoTaxi mototaxi : disponiveis) {
                System.out.println("------------------------");
                mototaxi.verMotoTaxi();
            }

            if (disponiveis.isEmpty()) {
                System.out.println("Nenhum mototaxista disponivel neste horario.");
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}