import java.util.List;

public class TesteRelatorioCorridas {
    public static void main(String[] args) {
        try {
            List<Corrida> corridas = Corrida.listarCorridas();

            int total = corridas.size();
            int finalizadas = 0;
            int emAndamento = 0;

            System.out.println("CORRIDAS CADASTRADAS:");

            for (Corrida corrida : corridas) {
                System.out.println("------------------------");
                corrida.verCorrida();
            }

            System.out.println("\nRELATORIO GERADO:");
            System.out.println(Corrida.gerarRelatorioCorridas());

            System.out.println("\nCONFERENCIA:");
            System.out.println("Total listado: " + total);

            for (Corrida corrida : corridas) {
                if (corridaFinalizada(corrida)) {
                    finalizadas++;
                } else {
                    emAndamento++;
                }
            }

            System.out.println("Finalizadas: " + finalizadas);
            System.out.println("Em andamento: " + emAndamento);

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
            erro.printStackTrace();
        }
    }

    private static boolean corridaFinalizada(Corrida corrida) {
        java.io.ByteArrayOutputStream saida =
            new java.io.ByteArrayOutputStream();

        java.io.PrintStream original = System.out;

        try {
            System.setOut(new java.io.PrintStream(saida));
            corrida.verCorrida();
        } finally {
            System.setOut(original);
        }

        return saida.toString().contains("Finalizada: Sim");
    }
}