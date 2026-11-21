import java.util.List;

public class TesteListarPontos {
    public static void main(String[] args) {
        try {
            List<Ponto> pontos = Ponto.listarPontos();

            System.out.println("Toda de pontos: " + pontos.size());

            for (Ponto ponto : pontos) {
                System.out.println("-------------------");
                ponto.verPonto();
            }
        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }    
}