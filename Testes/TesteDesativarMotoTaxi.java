import java.util.List;

public class TesteDesativarMotoTaxi {
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

            System.out.println("ANTES DA DESATIVACAO:");
            carlos.verMotoTaxi();

            MotoTaxi.desativarMotoTaxi(carlos);

            System.out.println("\nDEPOIS DA DESATIVACAO:");

            for (MotoTaxi mototaxi : MotoTaxi.listarMotoTaxis()) {
                if (mototaxi.getLogin().equals("carlos_mototaxi_teste")) {
                    mototaxi.verMotoTaxi();
                }
            }

            boolean loginPermitido = Usuario.autenticar(
                "carlos_mototaxi_teste",
                "NovaSenha@123"
            );

            if (!loginPermitido) {
                System.out.println("\nTESTE APROVADO: mototaxista nao pode mais fazer login!");
            } else {
                System.out.println("\nTESTE REPROVADO: login ainda permitido!");
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}