import java.util.Arrays;

public class TesteCriarZona {
    public static void main(String[] args) {
        try {
            Zona zona = Zona.criarZona(
                Arrays.asList("Centro Teste", "Bairro Central Teste"),
                Tipo.CENTRAL
            );

            System.out.println("Zona cadastrada com sucesso!");
            zona.verZona();

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}