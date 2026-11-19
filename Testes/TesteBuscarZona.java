public class TesteBuscarZona {
    public static void main(String[] args) {
        try {
            Zona zona = Zona.buscarZonaPorBairro("Bairro Inexistente Teste");

            if (zona != null) {
                System.out.println("Zona encontrada com sucesso!");
                zona.verZona();
            } else {
                System.out.println("Nenhuma zona encontrada para esse bairro.");
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}
