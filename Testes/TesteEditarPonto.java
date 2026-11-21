import java.util.List;

public class TesteEditarPonto {
    public static void main(String[] args) {
        try {
            List<Ponto> pontos = Ponto.listarPontos();
            Ponto pontoTeste = null;

            for (Ponto ponto : pontos) {
                if (ponto.getNomeDoPonto().equals("Ponto Cadastro Teste")) {
                    pontoTeste = ponto;
                    break;
                }
            }

            if (pontoTeste == null) {
                System.out.println("Ponto de teste nao encontrado");
                return;
            }

            System.out.println("ANTES DA EDICAO:");
            pontoTeste.verPonto();

            MotoTaxi administrador = pontoTeste.getAdministrador();
            Zona zona = pontoTeste.getZona();

            Ponto.editarPonto(
                pontoTeste,
                "Ponto Cadastro Editado",
                "Rua Nova Teste",
                administrador,
                null,
                zona,
                "Bairro Protecao Teste"
            );

            System.out.println("\nDEPOIS DA EDICAO:");
            pontoTeste.verPonto();

            System.out.println("\nPonto editado com sucesso!");
        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }    
}