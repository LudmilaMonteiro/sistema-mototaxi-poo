import java.util.List;

public class TesteListarZonas {
    public static void main(String[] args) {
        try {
            List<Zona> zonas = Zona.listarZonas();

            System.out.println("Total de zonas: " + zonas.size());

            for (Zona zona : zonas) {
                System.out.println("--------------------");
                zona.verZona();
            } 
        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}
