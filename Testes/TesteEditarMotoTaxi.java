import java.util.List;

public class TesteEditarMotoTaxi {
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

            System.out.println("ANTES DA EDICAO:");
            carlos.verMotoTaxi();

            MotoTaxi.editarMotoTaxi(
                carlos,
                "Carlos Silva Teste",
                "77766655544",
                "carlos_mototaxi_teste",
                "NovaSenha@123",
                "CNH-TESTE-003",
                9,
                19,
                false,
                null
            );

            System.out.println("\nDEPOIS DA EDICAO:");
            carlos.verMotoTaxi();

            System.out.println("\nMototaxista editado com sucesso!");

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}