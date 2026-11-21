import java.util.List;

public class TesteBuscarPorProximidade {
    public static void main(String[] args) {
        try {
            Zona zona = Zona.buscarZonaPorBairro("Bairro Protecao Teste");

            if (zona == null) {
                System.out.println("Zona de teste nao encontrada");
                return;
            }

            List<Ponto> pontos = Ponto.buscarPorProximidade(
                "Rua Nova Teste",
                "Bairro Protecao Teste",
                zona
            );

            System.out.println("Total de pontos encontrados: " + pontos.size());

            for (Ponto ponto : pontos) {
                System.out.println("------------------");
                ponto.verPonto();
            }

            if (pontos.isEmpty()) {
                System.out.println("Nenhum ponto encontrado");
            }
        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }    
}