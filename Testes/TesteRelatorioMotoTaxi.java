import java.util.List;

public class TesteRelatorioMotoTaxi {
    public static void main(String[] args) {
        try {
            MotoTaxi pedro = null;

            List<MotoTaxi> mototaxistas = MotoTaxi.listarMotoTaxis();

            for (MotoTaxi mototaxi : mototaxistas) {
                if (mototaxi.getLogin().equals("pedro_disponivel_teste")) {
                    pedro = mototaxi;
                    break;
                }
            }

            if (pedro == null) {
                System.out.println("Mototaxista de teste nao encontrado.");
                return;
            }

            String relatorio = MotoTaxi.gerarRelatorioMotoTaxi(pedro);

            System.out.println(relatorio);

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}