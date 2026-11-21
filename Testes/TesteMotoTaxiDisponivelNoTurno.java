import java.time.LocalTime;
import java.util.List;

public class TesteMotoTaxiDisponivelNoTurno {
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

            int horaAtual = LocalTime.now().getHour();
            int horaFim = (horaAtual + 2) % 24;

            MotoTaxi mototaxi = MotoTaxi.criarMotoTaxi(
                "Pedro Disponivel Teste",
                "66655544433",
                "pedro_disponivel_teste",
                "Teste@123",
                "CNH-TESTE-004",
                horaAtual,
                horaFim,
                false,
                pontoTeste
            );

            System.out.println("Mototaxista criado com sucesso!");
            System.out.println("Inicio do turno: " + horaAtual + "h");
            System.out.println("Fim do turno: " + horaFim + "h");

            List<MotoTaxi> disponiveis =MotoTaxi.buscarMotoTaxiDisponivel(pontoTeste);

            boolean encontrado = false;

            for (MotoTaxi moto : disponiveis) {
                if (moto.getLogin().equals(mototaxi.getLogin())) {
                    encontrado = true;
                }
            }

            if (encontrado) {
                System.out.println("TESTE APROVADO: mototaxista disponivel!");
            } else {
                System.out.println("TESTE REPROVADO: mototaxista nao apareceu!");
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}