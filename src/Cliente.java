
import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario {
    
    private boolean acessibilidade;
    private String tipoDeDificuldade;
    
    private List<String> historicoCorridas;

    public Cliente(String nome, String cpf, String login, String senha, boolean acessibilidade, String tipoDeDificuldade) {
        super(nome, cpf, login, senha); 
        this.acessibilidade = acessibilidade;
        this.tipoDeDificuldade = tipoDeDificuldade;
        this.historicoCorridas = new ArrayList<>();
    }

    public boolean isAcessibilidade() {
        return acessibilidade;
    }

    public void setAcessibilidade(boolean acessibilidade) {
        this.acessibilidade = acessibilidade;
    }

    public String getTipoDeDificuldade() {
        return tipoDeDificuldade;
    }

    public void setTipoDeDificuldade(String tipoDeDificuldade) {
        this.tipoDeDificuldade = tipoDeDificuldade;
    }

    public List<String> getHistoricoCorridas() {
        return historicoCorridas;
    }

    public void adicionarCorridaAoHistorico(String corrida) {
        this.historicoCorridas.add(corrida);
    }

    public boolean podeSerExcluido() {
        return this.historicoCorridas.isEmpty();
    }
}