# Sistema de Gerenciamento de Contatos e Pontos de Moto-Táxi

## Descrição do projeto

Projeto desenvolvido para a disciplina de **Programação Orientada a Objetos (POO)**, com o objetivo de implementar um sistema de gerenciamento de clientes, moto-taxistas, pontos de moto-táxi, zonas e corridas.

O sistema permite cadastrar e gerenciar usuários, organizar os pontos de moto-táxi por localização, associar profissionais aos pontos e registrar corridas entre diferentes regiões da cidade.

A aplicação foi desenvolvida em **Java**, utilizando **PostgreSQL** para persistência dos dados e uma interface de interação pelo terminal.

## Tecnologias utilizadas

- Java
- PostgreSQL
- JDBC
- Maven
- Visual Studio Code
- Git e GitHub
- pgAdmin 4

## Funcionalidades

### Usuários
- Visualização, edição, listagem, desativação e exclusão de usuários.
- Autenticação por login e senha.
- Controle de contas ativas e inativas.
- Preservação do histórico de corridas por meio das restrições do banco de dados.

### Clientes
- Cadastro de clientes.
- Edição e visualização dos dados.
- Registro de necessidades de acessibilidade.
- Listagem, desativação e exclusão de contas.
- Geração de relatório com a quantidade de corridas.

### Moto-taxistas
- Cadastro, edição e visualização de profissionais.
- Definição de horários de trabalho.
- Identificação de administradores de pontos.
- Cálculo de bônus salarial de R$ 350,00 para administradores.
- Consulta de moto-taxistas disponíveis por ponto, considerando o horário de trabalho e as corridas em andamento.
- Listagem, desativação e exclusão de contas.
- Geração de relatórios.

### Zonas
- Cadastro de zonas e seus respectivos bairros.
- Classificação em CENTRAL, INTERMEDIARIA e PERIFERICA.
- Edição, listagem e exclusão de zonas.
- Busca de zonas por bairro.
- Geração de relatórios com quantidade de bairros e pontos.

### Pontos de moto-táxi
- Cadastro e gerenciamento de pontos.
- Associação de moto-taxistas aos pontos.
- Definição de um administrador para cada ponto.
- Busca de pontos por rua, bairro ou critérios de localização dentro de uma zona.
- Listagem e exclusão de pontos.
- Geração de relatórios.

### Corridas
- Cadastro de corridas com origem e destino.
- Associação de clientes e moto-taxistas.
- Verificação de corridas em andamento para evitar atribuir duas viagens simultâneas ao mesmo profissional.
- Edição e finalização de corridas.
- Cálculo do valor conforme as zonas percorridas.
- Listagem, exclusão e geração de relatórios.

## Valores das corridas

Os valores são calculados com base na diferença entre as classificações das zonas de origem e destino.

| Diferença entre as zonas | Valor |
|---|---|
| Mesma zona | R$ 7,00 |
| Zonas adjacentes | R$ 10,00 |
| Zonas mais distantes | R$ 15,00 |

## Estrutura do projeto

As principais classes são:

| Classe | Responsabilidade |
|---|---|
| `Usuario.java` | Classe abstrata com atributos e operações comuns aos usuários |
| `Cliente.java` | Gerenciamento de clientes e acessibilidade |
| `MotoTaxi.java` | Gerenciamento de profissionais, turnos e disponibilidade |
| `Zona.java` | Organização de zonas e bairros |
| `Ponto.java` | Gerenciamento de pontos e associação de profissionais |
| `Corrida.java` | Gerenciamento das corridas e cálculo de valores |
| `Tipo.java` | Enumeração dos tipos de zona |
| `ConexaoBanco.java` | Conexão com o PostgreSQL |
| `Main.java` | Menu de interação pelo terminal |

## Banco de dados

O sistema utiliza PostgreSQL para armazenar os dados de forma persistente.

As principais tabelas são:

- `usuarios`
- `clientes`
- `mototaxistas`
- `zonas`
- `zona_bairros`
- `pontos`
- `ponto_mototaxistas`
- `corridas`

As tabelas possuem relacionamentos por chaves estrangeiras, permitindo manter a integridade dos registros.

As senhas são armazenadas utilizando hash bcrypt por meio da extensão `pgcrypto` do PostgreSQL.

## Como executar o projeto

### 1. Pré-requisitos

É necessário possuir:

- Java JDK compatível com o projeto.
- Apache Maven.
- PostgreSQL.
- Banco de dados criado e configurado.
- Driver JDBC do PostgreSQL, disponibilizado pelas dependências Maven do projeto.

### 2. Clonar o repositório

```bash
git clone https://github.com/LudmilaMonteiro/sistema-mototaxi-poo.git
cd sistema-mototaxi-poo
```

### 3. Configurar o banco de dados

No PostgreSQL, crie o banco de dados utilizado pelo projeto e execute o script SQL disponível no repositório.

Verifique também as configurações de conexão presentes na classe `ConexaoBanco.java`.

O banco deve possuir a extensão `pgcrypto`, necessária para os comandos de hash de senha.

### 4. Configurar a senha do PostgreSQL

O projeto utiliza a variável de ambiente `DB_PASSWORD` para receber a senha do banco de dados.

No PowerShell do Windows, execute:

```powershell
$env:DB_PASSWORD = Read-Host "Senha do PostgreSQL" -MaskInput
```

A senha não deve ser adicionada diretamente ao código-fonte nem enviada ao GitHub.

### 5. Compilar

```bash
mvn clean compile
```

### 6. Executar

```bash
mvn exec:java "-Dexec.mainClass=Main"
```

Também é possível executar pelo VS Code utilizando uma configuração de execução Java que forneça a variável de ambiente `DB_PASSWORD`.

## Conceitos de Programação Orientada a Objetos

O projeto utiliza conceitos como:

- **Abstração:** por meio da classe abstrata `Usuario`.
- **Herança:** as classes `Cliente` e `MotoTaxi` herdam de `Usuario`.
- **Encapsulamento:** atributos privados e acesso por métodos.
- **Polimorfismo:** utilização de objetos das subclasses em coleções do tipo `Usuario`.
- **Associação entre classes:** relações entre pontos, zonas, moto-taxistas e corridas.

## Considerações sobre a implementação

A aplicação utiliza interface pelo terminal e persistência no PostgreSQL.

A busca por proximidade considera a zona informada e a correspondência de rua ou bairro. A priorização completa dos pontos por níveis de proximidade ainda não está implementada.

A consulta de disponibilidade de moto-taxistas considera os horários de trabalho e as corridas em andamento. No cadastro direto de corridas, a verificação de turno ainda não é aplicada.