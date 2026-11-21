import java.util.List;

public class TesteDeletarMotoTaxi {
    public static void main(String[] args) {
        try {
            List<MotoTaxi> mototaxistas = MotoTaxi.listarMotoTaxis();
            MotoTaxi carlos = null;

            for (MotoTaxi mototaxi : mototaxistas) {
                if (mototaxi.getLogin().equals("carlos_mototaxi_teste")) {
                    carlos = mototaxi;
                    break;
                }
            }

            if (carlos == null) {
                System.out.println("Mototaxista de teste nao encontrado.");
                return;
            }

            System.out.println("ANTES DA EXCLUSAO:");
            carlos.verMotoTaxi();

            MotoTaxi.deletarMotoTaxi(carlos);

            System.out.println("\nExclusao executada!");

            boolean encontrado = false;

            for (MotoTaxi mototaxi : MotoTaxi.listarMotoTaxis()) {
                if (mototaxi.getLogin().equals("carlos_mototaxi_teste")) {
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                System.out.println("TESTE APROVADO: mototaxista excluido do banco!");
            } else {
                System.out.println("TESTE REPROVADO: mototaxista ainda existe!");
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}