import java.util.Arrays;
import java.util.List;

public class TesteEditarZona {
    public static void main(String[] args) {
        try {
            List<Zona> zonas = Zona.listarZonas();
            Zona zonaTeste = null;

            for (Zona zona : zonas) {
                if (zona.getBairros().contains("Centro Teste")) {
                    zonaTeste = zona;
                    break;
                }
            }

            if (zonaTeste == null) {
                System.out.println("Zona de teste nao encontarda.");
                return;
            }

            System.out.println("ANTES DA EDICAO:");
            zonaTeste.verZona();

            Zona.editarZona(zonaTeste, Arrays.asList("Centro Teste", "Novo Bairro Central"), Tipo.CENTRAL);

            System.out.println("\nDEPOIS DA EDICAO:");
            zonaTeste.verZona();

            System.out.println("Zona editada com sucesso!");
        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}
