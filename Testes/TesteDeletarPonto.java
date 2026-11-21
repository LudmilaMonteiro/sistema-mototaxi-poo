import java.util.List;

public class TesteDeletarPonto {
    public static void main(String[] args) {
        try {
            List<Ponto> pontos = Ponto.listarPontos();
            Ponto pontoTeste = null;

            for (Ponto ponto : pontos) {
                if (ponto.getIdPonto() == 2 && ponto.getNomeDoPonto().equals("Ponto Cadastro Editado")) {
                    pontoTeste = ponto;
                    break;
                }
            }

            if (pontoTeste == null) {
                System.out.println("Ponto de teste nao encontrado.");
                return;
            }

            System.out.println("ANTES DA EXCLUSAO:");
            pontoTeste.verPonto();

            Ponto.deletarPonto(pontoTeste);

            System.out.println("\nExclusao executada!");

            boolean encontrado = false;

            for (Ponto ponto : Ponto.listarPontos()) {
                if (ponto.getIdPonto() == 2) {
                    encontrado = true;
                }
            }

            if (!encontrado) {
                System.out.println("TESTE APROVADO: ponto excluido do banco!");
            } else {
                System.out.println("TESTE REPROVADO: ponto ainda existe!");
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}