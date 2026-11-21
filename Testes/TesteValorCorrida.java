import java.util.List;

public class TesteValorCorrida {
    public static void main(String[] args) {
        try {
            List<Corrida> corridas = Corrida.listarCorridas();

            if (corridas.isEmpty()) {
                System.out.println("Nenhuma corrida encontrada.");
                return;
            }

            int valor7 = 0;
            int valor10 = 0;
            int valor15 = 0;

            for (Corrida corrida : corridas) {
                System.out.println("------------------------");

                corrida.verCorrida();

                float valor = corrida.valorCorrida();

                if (valor == 7.0f) {
                    valor7++;
                } else if (valor == 10.0f) {
                    valor10++;
                } else if (valor == 15.0f) {
                    valor15++;
                } else {
                    System.out.println(
                        "TESTE REPROVADO: valor inesperado!"
                    );
                }
            }

            System.out.println("\nRESUMO:");
            System.out.println("Corridas de R$ 7: " + valor7);
            System.out.println("Corridas de R$ 10: " + valor10);
            System.out.println("Corridas de R$ 15: " + valor15);

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}