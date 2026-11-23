import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        int opcao;

        do {
            System.out.println("\n======================================");
            System.out.println("   SISTEMA DE GERENCIAMENTO MOTO-TAXI  ");
            System.out.println("========================================");
            System.out.println("1 - Gerenciar clientes");
            System.out.println("2 - Gerenciar moto-taxistas");
            System.out.println("3 - Gerenciar zonas");
            System.out.println("4 - Gerenciar pontos");
            System.out.println("5 - Gerenciar corridas");
            System.out.println("6 - Listar todos os usuarios");
            System.out.println("7 - Fazer login");
            System.out.println("0 - Sair");

            opcao = lerInteiro("Escolha uma opcao: ");

            try {
                switch (opcao) {
                    case 1 -> menuClientes();
                    case 2 -> menuMotoTaxis();
                    case 3 -> menuZonas();
                    case 4 -> menuPontos();
                    case 5 -> menuCorridas();
                    case 6 -> listarUsuarios();
                    case 7 -> fazerLogin();
                    case 0 -> System.out.println("Encerrando sistema...");
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (Exception erro) {
                System.out.println("Erro: " + erro.getMessage());
            }
        } while (opcao != 0);

        scanner.close();
    }

    private static String lerTexto(String mensagem) {
        System.out.println(mensagem);

        return scanner.nextLine().trim();
    }

    private static int lerInteiro(String mensagem) {
        while (true) {
            try {
                return Integer.parseInt(lerTexto(mensagem));
            } catch (NumberFormatException erro) {
                System.out.println("Digite um numero valido.");
            }
        }
    }

    private static boolean lerBoolean(String mensagem) {
        while (true) {
            String resposta = lerTexto(mensagem + " (s/n): ");

            if (resposta.equalsIgnoreCase("s")) {
                return true;
            }

            if (resposta.equalsIgnoreCase("n")) {
                return false;
            }

            System.out.println("Digite s ou n.");
        }
    }

    private static void menuClientes() {
        System.out.println("\n--- CLIENTES ---");
        System.out.println("1 - Cadastrar cliente");
        System.out.println("2 - Listar clientes");

        int opcao = lerInteiro("Opcao: ");

        switch (opcao) {
            case 1 -> cadastrarCliente();
            case 2 -> listarClientes();
            default -> System.out.println("Opcao invalida.");
        }
    }

    private static void cadastrarCliente() {
        String nome = lerTexto("Nome: ");
        String cpf = lerTexto("CPF: ");
        String login = lerTexto("Login: ");
        String senha = lerTexto("Senha: ");

        boolean acessibilidade = lerBoolean("Necessita de acessibilidade?");

        String dificuldade = "";

        if (acessibilidade) {
            dificuldade = lerTexto("Tipo de dificuldade: ");
        }

        Cliente cliente = Cliente.criarCliente(nome, cpf, login, senha, acessibilidade, dificuldade);

        System.out.println("\nCliente cadastrado com sucesso!");
    }

    private static void listarClientes() {
        List<Cliente> clientes = Cliente.listarClientes();

        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");

            return;
        }

        for (int i = 0; i < clientes.size(); i++) {
            System.out.println("\nCliente " + (i + 1));

            clientes.get(i).verCliente();
        }
    }

    private static void menuMotoTaxis() {
        System.out.println("\n--- MOTO-TAXISTAS ---");
        System.out.println("1 - Cadastrar moto-taxista");
        System.out.println("2 - Listar moto-taxistas");
        System.out.println("3 - Consultar disponibilidade por ponto");
        System.out.println("4 - Relatorio de moto-taxista");

        int opcao = lerInteiro("Opcao: ");

        switch (opcao) {
            case 1 -> cadastrarMotoTaxi();
            case 2 -> listarMotoTaxis();
            case 3 -> consultarDisponiveis();
            case 4 -> relatorioMotoTaxi();
            default -> System.out.println("Opcao invalida.");
        }
    }

    private static void cadastrarMotoTaxi() {
        String nome = lerTexto("Nome: ");
        String cpf = lerTexto("CPF: ");
        String login = lerTexto("Login: ");
        String senha = lerTexto("Senha: ");
        String cnh = lerTexto("CNH: ");

        int inicio = lerInteiro("Inicio do turno (0 a 23): ");
        int fim = lerInteiro("Fim do turno (0 a 23): ");

        boolean administrador = lerBoolean("Sera administrador de ponto?");

        Ponto ponto = null;

        if (lerBoolean("Associar a um ponto existente?")) {
            ponto = selecionarPonto();

            if (ponto == null) {
                System.out.println("Cadastro cancelado.");

                return;
            }
        }

        MotoTaxi mototaxi = MotoTaxi.criarMotoTaxi(nome, cpf, login, senha, cnh, inicio, fim, administrador, ponto);

        System.out.println("\nMoto-taxista cadastrado!");
        mototaxi.verMotoTaxi();
    }

    private static void listarMotoTaxis() {
        List<MotoTaxi> mototaxistas = MotoTaxi.listarMotoTaxis();

        if (mototaxistas.isEmpty()) {
            System.out.println("Nnehum moto-taxista cadastrado.");

            return;
        }

        for (int i = 0; i < mototaxistas.size(); i++) {
            System.out.println("\nMoto-taxista " + (i + 1));

            mototaxistas.get(i).verMotoTaxi();
        }
    }

    private static void consultarDisponiveis() {
        Ponto ponto = selecionarPonto();

        if (ponto == null) {
            return;
        }

        List<MotoTaxi> disponiveis = MotoTaxi.buscarMotoTaxiDisponivel(ponto);

        System.out.println("\nMoto-taxistas disponiveis: " + disponiveis.size());

        for (MotoTaxi mototaxi :  disponiveis) {
            System.out.println("-------------------------");
            mototaxi.verMotoTaxi();
        }

        if (disponiveis.isEmpty()) {
            System.out.println("Nenhum disponivel neste momento.");
        }
    }

    private static void relatorioMotoTaxi() {
        MotoTaxi mototaxi = selecionarMotoTaxi();

        if (mototaxi != null) {
            System.out.println(MotoTaxi.gerarRelatorioMotoTaxi(mototaxi));
        }
    }

    private static void menuZonas() {
        System.out.println("\n--- ZONAS ---");
        System.out.println("1 - Cadastrar zona");
        System.out.println("2 - Listar zonas");

        int opcao = lerInteiro("Opcao: ");

        switch (opcao) {
            case 1 -> cadastrarZona();
            case 2 -> listarZonas();
            default -> System.out.println("Opcao invalida.");
        }
    }

    private static void cadastrarZona() {
        System.out.println("1 - CENTRAL");
        System.out.println("2 - INTERMEDIARIA");
        System.out.println("3 - PERIFERICA");

        int escolha = lerInteiro("Tipo da zona: ");

        Tipo tipo;

        switch (escolha) {
            case 1 -> tipo = Tipo.CENTRAL;
            case 2 -> tipo = Tipo.INTERMEDIARIA;
            case 3 -> tipo = Tipo.PERIFERICA;
            default -> {
                System.out.println("Tipo invalido.");

                return;
            }
        }

        String entrada = lerTexto("Bairros separados por virgula: ");

        List<String> bairros = new ArrayList<>(Arrays.asList(entrada.split(",")));

        for (int i = 0; i < bairros.size(); i++) {
            bairros.set(i, bairros.get(i).trim());
        }

        Zona zona = Zona.criarZona(bairros, tipo);

        System.out.println("\nZona cadastrada!");
        zona.verZona();
    }

    private static void listarZonas() {
        List<Zona> zonas = Zona.listarZonas();

        if (zonas.isEmpty()) {
            System.out.println("Nenhuma zona cadastrada.");

            return;
        }

        for (int i = 0; i < zonas.size(); i++) {
            System.out.println("\nZona " + (i + 1));

            zonas.get(i).verZona();
        }
    }

    private static void menuPontos() {
        System.out.println("\n--- PONTOS ---");
        System.out.println("1 - Cadastrar ponto");
        System.out.println("2 - Listar pontos");
        System.out.println("3 - Listar moto-taxistas do ponto");

        int opcao = lerInteiro("Opcao: ");

        switch (opcao) {
            case 1 -> cadastrarPonto();
            case 2 -> listarPontos();
            case 3 -> listarMotoTaxisPorPonto();
            default -> System.out.println("Opcao invalida.");
        }
    }

    private static void cadastrarPonto() {
        String nome = lerTexto("Nome do ponto: ");
        String rua = lerTexto("Rua: ");
        String bairro = lerTexto("Bairro: ");

        System.out.println("\nEscolha a zona:");
        Zona zona = selecionarZona();

        if (zona == null) {
            return;
        }

        System.out.println("\nEscolha o administrador:");
        MotoTaxi administrador = selecionarMotoTaxi();

        if (administrador == null) {
            return;
        }

        Ponto ponto = Ponto.criarPonto(nome, rua, administrador, zona, bairro);

        System.out.println("\nPonto cadastrado!");
        ponto.verPonto();
    }

    private static void listarPontos() {
        List<Ponto> pontos = Ponto.listarPontos();

        if (pontos.isEmpty()) {
            System.out.println("Nnehum ponto cadastrado.");

            return;
        }

        for (int i = 0; i < pontos.size(); i++) {
            System.out.println("\nPonto " + (i + 1));
            pontos.get(i).verPonto();
        }
    }

    private static void listarMotoTaxisPorPonto() {
        Ponto ponto = selecionarPonto();

        if (ponto == null) {
            return;
        }

        List<MotoTaxi> mototaxistas = MotoTaxi.listarMotoTaxisPorPonto(ponto);

        System.out.println("\nAssociados ao ponto: " + mototaxistas.size());

        for (MotoTaxi mototaxi : mototaxistas) {
            mototaxi.verMotoTaxi();

            System.out.println("-----------------------");
        }
    }

    private static void menuCorridas() {
        System.out.println("\n--- CORRIDAS ---");
        System.out.println("1 - Criar corrida");
        System.out.println("2 - Listar corridas");
        System.out.println("3 - Finalizar corrida");
        System.out.println("4 - Relatorio de corridas");

        int opcao = lerInteiro("Opcao: ");

        switch (opcao) {
            case 1 -> cadastrarCorrida();
            case 2 -> listarCorridas();
            case 3 -> finalizarCorrida();
            case 4 -> System.out.println(Corrida.gerarRelatorioCorridas());
            default -> System.out.println("Opcao invalida.");
        }
    }

    private static void cadastrarCorrida() {
        System.out.println("\nEscolha o cliente:");
        Cliente cliente = selecionarCliente();

        if (cliente == null) {
            return;
        }

        System.out.println("\nEscolha o moto-taxista:");
        MotoTaxi mototaxi = selecionarMotoTaxi();

        if (mototaxi == null) {
            return;
        }

        String bairroOrigem = lerTexto("Bairro de origem: ");
        String ruaOrigem = lerTexto("Rua de origem: ");
        String bairroDestino = lerTexto("Bairro de destino: ");
        String ruaDestino = lerTexto("Rua de destino: ");

        Corrida corrida = Corrida.criarCorrida(bairroOrigem, ruaOrigem, bairroDestino, ruaDestino, cliente, mototaxi);

        System.out.println("\nCorrida cadastrada!");
        corrida.verCorrida();
    }

    private static void listarCorridas() {
        List<Corrida> corridas = Corrida.listarCorridas();

        if (corridas.isEmpty()) {
            System.out.println("Nenhuma corrida cadastrada.");

            return;
        }

        for (int i = 0; i < corridas.size(); i++) {
            System.out.println("\nCorrida " + (i + 1));
            corridas.get(i).verCorrida();
        }
    }

    private static void finalizarCorrida() {
        List<Corrida> corridas = Corrida.listarCorridas();

        if (corridas.isEmpty()) {
            System.out.println("Nenhuma corrida cadastrada.");

            return;
        }

        for (int i = 0; i < corridas.size(); i++) {
            System.out.println("\nCorrida " + (i + 1));
            corridas.get(i).verCorrida();
        }

        int escolha = lerInteiro("\nNumero da corrida para finalizar (0 cancela): ");

        if (escolha == 0) {
            return;
        }

        if (escolha < 1 || escolha > corridas.size()) {
            System.out.println("Corrida invalida.");

            return;
        }

        Corrida corrida = corridas.get(escolha - 1);

        if (!lerBoolean("Confirma a finalizacao?")) {
            return;
        }

        corrida.finalizarCorrida();

        System.out.println("\nCorrida finalizada com sucesso!")
        corrida.verCorrida();
    }

    private static void listarUsuarios() {
        List<Usuario> usuarios = Usuario.listarUsuarios();

        System.out.println("\nTotal de usuarios: " + usuarios.size());

        for (Usuario usuario : usuarios) {
            System.out.println("--------------------");
            usuario.verUsuario();
        }
    }

    private static void fazerLogin() {
        String login = lerTexto("Login: ");
        String senha = lerTexto("Senha: ");
        
        if (Usuario.autenticar(login, senha)) {
            System.out.println("Login realizado com sucesso!");
        } else {
            System.out.println("Login ou senha invalidos.");
        }
    }

    private static Cliente selecionarCliente() {
        List<Cliente> clientes = Cliente.listarClientes();

        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");

            return null;
        }

        for (int i = 0; i < clientes.size(); i++) {
            System.out.println("\n" + (i + 1));
            clientes.get(i).verCliente();
        }

        int escolha = lerInteiro("Numero do cliente (0 cancela): ");

        if (escolha < 1 || escolha > clientes.size()) {
            System.out.println("Selecao cancelada.");

            return null;
        }

        return clientes.get(escolha - 1);
    }

    private static MotoTaxi selecionarMotoTaxi() {
        List<MotoTaxi> mototaxistas = MotoTaxi.listarMotoTaxis();

        if (mototaxistas.isEmpty()) {
            System.out.println("Nenhum moto-taxista cadastrado.");

            return null;
        }

        for (int i = 0; i < mototaxistas.size(); i++) {
            System.out.println("\n" + (i + 1));

            mototaxistas.get(i).verMotoTaxi();
        }

        int escolha = lerInteiro("Numero do moto-taxista (0 cancela): ");

        if (escolha < 1 || escolha > mototaxistas.size()) {
            System.out.println("Selecao cancelada.");

            return null;
        }

        return mototaxistas.get(escolha - 1);
    }

    private static Zona selecionarZona() {
        List<Zona> zonas = Zona.listarZonas();

        if (zonas.isEmpty()) {
            System.out.println("Nenhuma zona cadastrada.");
            return null;
        }

        for (int i = 0; i < zonas.size(); i++) {
            System.out.println("\n" + (i + 1));
            zonas.get(i).verZona();
        }

        int escolha = lerInteiro("Numero da zona (0 cancela): ");

        if (escolha < 1 || escolha > zonas.size()) {
            System.out.println("Selecao cancelada.");
            return null;
        }

        return zonas.get(escolha - 1);
      }     
    }

    private static Ponto selecionarPonto() {

    }
}
