public class TesteCriarPonto {
    public static void main(String[] args) {
        try {
            Zona zona = Zona.buscarZonaPorBairro("Bairro Protecao Teste");

            if (zona == null) {
                System.out.println("Zona de teste nao encontrada");
                return;
            }

            MotoTaxi administrador = MotoTaxi.criarMotoTaxi(
                "Administrador Cadastro",
                "88877766655",
                "admin_cadstro_ponto",
                "Teste@123",
                "CNH-TESTE-002",
                8,
                18,
                true,
                null
            );

            Ponto ponto = Ponto.criarPonto(
                 "Ponto Cadastro Teste",
                 "Rua Cadastro Teste",
                 administrador,
                 zona,
                 "Bairro Protecao Teste"    
            );

            System.out.println("Ponto cadastrado com sucesso!");
            ponto.verPonto();
        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}
