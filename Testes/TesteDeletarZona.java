public class TesteDeletarZona {
    public static void main(String[] args) {
        try {
            Zona zona = Zona.buscarZonaPorBairro("Novo Bairro Central");

            if (zona == null) {
                System.out.println("Zona de teste nao encontrada.");
                return;
            }

            System.out.println("ANTES DA EXCLUSAO:");
            zona.verZona();

            Zona.deletarZona(zona);

            System.out.println("Zona excluida com sucesso!");

            Zona verificar = Zona.buscarZonaPorBairro("Novo Bairro Central");

            if (verificar == null) {
                System.out.println("Exclusao confirmada!");
            } else {
                System.out.println("A zona ainda existe.");
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}