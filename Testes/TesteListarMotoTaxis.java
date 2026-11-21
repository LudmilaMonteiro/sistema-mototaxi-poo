import java.util.List;

public class TesteListarMotoTaxis {
    public static void main(String[] args) {
        try {
            List<MotoTaxi> mototaxistas = MotoTaxi.listarMotoTaxis();

            System.out.println("Total de mototaxistas: " + mototaxistas.size());

            for (MotoTaxi mototaxi : mototaxistas) {
                System.out.println("------------------------");
                mototaxi.verMotoTaxi();

                System.out.println("Bonus calculado: R$ " + mototaxi.bonusSalarial());
            }

            if (mototaxistas.isEmpty()) {
                System.out.println("Nenhum mototaxista cadastrado.");
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}