public class TesteExcluirZonaComPonto {
    public static void main(String[] args) {
        try {
            Zona zona = Zona.buscarZonaPorBairro("Bairro Protecao Teste");

            if (zona == null) {
                System.out.println("Zona de teste nao encontrada.");
                return;
            }

            System.out.println("ANTES DA TENTATIVA:");
            zona.verZona();

            try {
                Zona.deletarZona(zona);
                System.out.println("ERRO NO TESTE: a exclusao foi permitida!");
            } catch (Exception erro) {
                System.out.println("Exclusao bloqueada: " + erro.getMessage());
            }

            Zona verificar = Zona.buscarZonaPorBairro("Bairro Protecao Teste");

            if (verificar != null) {
                System.out.println("TESTE APROVADO: a zona continua cadastrada!");
            } else {
                System.out.println("TESTE REPROVADO: a zona foi excluida!");
            }

        } catch (Exception erro) {
            System.out.println("Erro inesperado: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}
