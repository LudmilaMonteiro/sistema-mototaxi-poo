import java.util.List;

public class TesteBuscarPorBairro {
    public static void main(String[] args) {
        try {
            List<Ponto> pontos = Ponto.buscarPorBairro("Bairro Protecao Teste");

            System.out.println("Total de pontos encontrados: " + pontos.size());

            for (Ponto ponto : pontos) {
                System.out.println("-------------------");
                ponto.verPonto();
            }

            if (pontos.isEmpty()) {
                System.out.println("Nenhum ponto encontrado.");
            }
        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }    
}
