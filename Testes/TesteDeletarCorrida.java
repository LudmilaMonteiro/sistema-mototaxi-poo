import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class TesteDeletarCorrida {
    public static void main(String[] args) {
        try {
            String sql = """
                SELECT id_corrida, posicao
                FROM (
                    SELECT
                        id_corrida,
                        bairro_origem,
                        rua_origem,
                        bairro_destino,
                        rua_destino,
                        finalizada,
                        ROW_NUMBER() OVER (
                            ORDER BY id_corrida
                        ) AS posicao
                    FROM corridas
                ) AS lista
                WHERE bairro_origem = 'Bairro Protecao Teste'
                  AND rua_origem = 'Rua Origem Editada'
                  AND bairro_destino = 'Bairro Protecao Teste'
                  AND rua_destino = 'Rua Destino Editada'
                  AND finalizada = TRUE
                """;

            int idCorrida = 0;
            int posicao = -1;
            int quantidade = 0;

            try (Connection conexao = ConexaoBanco.conectar(); PreparedStatement comando = conexao.prepareStatement(sql); ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    idCorrida = resultado.getInt("id_corrida");
                    posicao = resultado.getInt("posicao") - 1;
                    quantidade++;
                }
            }

            if (quantidade != 1) {
                System.out.println("Esperada uma corrida de teste. Encontradas: " + quantidade);
                return;
            }

            List<Corrida> corridas = Corrida.listarCorridas();
            Corrida corridaTeste = corridas.get(posicao);

            System.out.println("ANTES DA EXCLUSAO:");
            corridaTeste.verCorrida();

            Corrida.deletarCorrida(corridaTeste);

            System.out.println("\nExclusao executada!");

            String consulta = """
                SELECT COUNT(*)
                FROM corridas
                WHERE id_corrida = ?
                """;

            try (Connection conexao = ConexaoBanco.conectar(); PreparedStatement comando = conexao.prepareStatement(consulta)) {
                comando.setInt(1, idCorrida);

                try (ResultSet resultado = comando.executeQuery()) {
                    resultado.next();

                    if (resultado.getInt(1) == 0) {
                        System.out.println("TESTE APROVADO: corrida excluida do banco!");
                    } else {
                        System.out.println("TESTE REPROVADO: corrida ainda existe!");
                    }
                }
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }
}