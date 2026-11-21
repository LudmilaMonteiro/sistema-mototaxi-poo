import java.util.List;

public class TesteListarCorridas {
    public static void main(String[] args) {
        try {
            List<Corrida> corridas = Corrida.listarCorridas();

            System.out.println("Total de corridas: " + corridas.size());

            for (Corrida corrida : corridas) {
                System.out.println("------------------------");
                corrida.verCorrida();
            }

            if (corridas.isEmpty()) {
                System.out.println("Nenhuma corrida cadastrada.");
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}