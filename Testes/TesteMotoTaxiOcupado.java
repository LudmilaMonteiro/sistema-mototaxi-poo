import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TesteMotoTaxiOcupado {
    public static void main(String[] args) {
        int idCorrida = 0;

        try {
            Ponto pontoTeste = null;

            for (Ponto ponto : Ponto.listarPontos()) {
                if (ponto.getNomeDoPonto().equals("Ponto Protecao Teste")) {
                    pontoTeste = ponto;
                    break;
                }
            }

            if (pontoTeste == null) {
                System.out.println("Ponto nao encontrado.");
                return;
            }

            String sql = """
                INSERT INTO corridas
                (bairro_origem, rua_origem, bairro_destino,
                 rua_destino, finalizada, id_cliente, id_mototaxista)
                SELECT
                    'Bairro Protecao Teste',
                    'Rua Origem Ocupado',
                    'Bairro Protecao Teste',
                    'Rua Destino Ocupado',
                    FALSE,
                    c.id_usuario,
                    m.id_usuario
                FROM usuarios c, usuarios m
                WHERE c.login = 'maria_teste'
                  AND m.login = 'pedro_disponivel_teste'
                RETURNING id_corrida
                """;

            try (Connection conexao = ConexaoBanco.conectar(); PreparedStatement comando = conexao.prepareStatement(sql); ResultSet resultado = comando.executeQuery()) {
                if (!resultado.next()) {
                    System.out.println("Cliente ou Pedro nao encontrado.");
                    return;
                }

                idCorrida = resultado.getInt("id_corrida");
            }

            System.out.println("Corrida de teste criada.");

            boolean pedroDisponivel = false;

            for (MotoTaxi mototaxi :
                    MotoTaxi.buscarMotoTaxiDisponivel(pontoTeste)) {

                if (mototaxi.getLogin().equals("pedro_disponivel_teste")) {
                    pedroDisponivel = true;
                }
            }

            if (!pedroDisponivel) {
                System.out.println("TESTE APROVADO: Pedro nao esta disponivel!");
            } else {
                System.out.println("TESTE REPROVADO: Pedro apareceu como disponivel!");
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();

        } finally {
            if (idCorrida != 0) {
                try (Connection conexao = ConexaoBanco.conectar(); PreparedStatement comando = conexao.prepareStatement("DELETE FROM corridas WHERE id_corrida = ?")) {
                    comando.setInt(1, idCorrida);
                    comando.executeUpdate();

                    System.out.println("Corrida temporaria removida.");

                } catch (Exception erro) {
                    System.out.println("Erro ao remover corrida: " + erro.getMessage());
                }
            }
        }
    }
}