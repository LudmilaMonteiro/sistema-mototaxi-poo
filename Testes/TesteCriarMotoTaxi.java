public class TesteCriarMotoTaxi {
    public static void main(String[] args) {
        try {
            MotoTaxi mototaxi = MotoTaxi.criarMotoTaxi(
                "Carlos Teste",
                "77766655544",
                "carlos_mototaxi_teste",
                "Teste@123",
                "CNH-TESTE-003",
                8,
                18,
                false,
                null
            );

            System.out.println("Mototaxista cadastrado com sucesso!");

            mototaxi.verMotoTaxi();

            System.out.println("Bonus calculado: R$ " + mototaxi.bonusSalarial());

            if (mototaxi.bonusSalarial() == 0.0f) {
                System.out.println("TESTE APROVADO: bonus correto!");
            } else {
                System.out.println("TESTE REPROVADO: bonus incorreto!");
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}