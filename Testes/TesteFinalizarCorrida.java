import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class TesteFinalizarCorrida {
    public static void main(String[] args) {
        try {
            String sql = """
                SELECT posicao
                FROM (
                    SELECT
                        rua_origem,
                        rua_destino,
                        finalizada,
                        ROW_NUMBER() OVER (
                            ORDER BY id_corrida
                        ) AS posicao
                    FROM corridas
                ) AS lista
                WHERE rua_origem = 'Rua Origem Editada'
                  AND rua_destino = 'Rua Destino Editada'
                  AND finalizada = FALSE
                """;

            int posicao = -1;
            int quantidade = 0;

            try (Connection conexao = ConexaoBanco.conectar(); PreparedStatement comando = conexao.prepareStatement(sql); ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    posicao = resultado.getInt("posicao") - 1;
                    quantidade++;
                }
            }

            if (quantidade != 1) {
                System.out.println("Esperada uma unica corrida de teste. Encontradas: " + quantidade);
                return;
            }

            List<Corrida> corridas = Corrida.listarCorridas();
            Corrida corridaTeste = corridas.get(posicao);

            System.out.println("ANTES DA FINALIZACAO:");
            corridaTeste.verCorrida();

            corridaTeste.finalizarCorrida();

            System.out.println("\nDEPOIS DA FINALIZACAO:");
            corridaTeste.verCorrida();

            System.out.println("\nPrimeira finalizacao executada com sucesso!");

            try {
                corridaTeste.finalizarCorrida();

                System.out.println("TESTE REPROVADO: permitiu finalizar novamente!");

            } catch (IllegalStateException erro) {
                if ("Corrida ja finalizada.".equals(erro.getMessage())) {
                    System.out.println("TESTE APROVADO: segunda finalizacao bloqueada!");
                } else {
                    System.out.println("TESTE REPROVADO: " + erro.getMessage());
                }
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}