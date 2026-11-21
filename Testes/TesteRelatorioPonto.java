import java.util.List;

public class TesteRelatorioPonto {
    public static void main(String[] args) {
        try {
            List<Ponto> pontos = Ponto.listarPontos();
            Ponto pontoTeste = null;

            for (Ponto ponto : pontos) {
                if (ponto.getNomeDoPonto().equals("Ponto Cadastro Editado")) {
                    pontoTeste = ponto;
                    break;
                }
            }

            if (pontoTeste == null) {
                System.out.println("Ponto de teste nao encontrado");
                return;
            }

            String relatorio = Ponto.gerarRelatorioPonto(pontoTeste);

            System.out.println(relatorio);
        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }    
}