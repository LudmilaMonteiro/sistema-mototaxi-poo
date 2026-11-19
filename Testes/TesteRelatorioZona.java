public class TesteRelatorioZona {
    public static void main(String[] args) {
        try {
            Zona zona = Zona.buscarZonaPorBairro("Novo Bairro Central");

            if (zona != null) {
                String relatorio = Zona.gerarRelatorioZona(zona);
                System.out.println(relatorio);
            } else {
                System.out.println("Zona de teste nao encontrada.");
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}
