CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    login VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    tipo_usuario VARCHAR(20) NOT NULL
       CHECK (tipo_usuario IN ('CLIENTE', 'MOTOTAXI'))
);


CREATE TABLE IF NOT EXISTS clientes (
    id_usuario INTEGER PRIMARY KEY,
    acessibilidade BOOLEAN NOT NULL,
    tipo_de_dificuldade TEXT,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);


CREATE TABLE IF NOT EXISTS mototaxistas (
    id_usuario INTEGER PRIMARY KEY,
    cnh VARCHAR(20) NOT NULL UNIQUE,
    horario_noturno INTEGER NOT NULL,
    hora_fim_turno INTEGER NOT NULL,
    e_administrador BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);


CREATE TABLE IF NOT EXISTS zonas (
    id_zona INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL 
       CHECK (tipo IN ('CENTRAL', 'INTERMEDIARIA', 'PERIFERICA'))
);


CREATE TABLE IF NOT EXISTS zona_bairros (
    id_zona INTEGER NOT NULL,
    bairro VARCHAR(150) NOT NULL UNIQUE,
    PRIMARY KEY (id_zona, bairro),
    FOREIGN KEY (id_zona) REFERENCES zonas(id_zona)
);


CREATE TABLE IF NOT EXISTS pontos (
    id_ponto INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome_ponto VARCHAR(150) NOT NULL,
    rua VARCHAR(150) NOT NULL,
    bairro VARCHAR(150) NOT NULL,
    id_zona INTEGER NOT NULL,
    id_administrador INTEGER NOT NULL,
    FOREIGN KEY (id_zona) REFERENCES zonas(id_zona),
    FOREIGN KEY (id_administrador) REFERENCES mototaxistas(id_usuario)
);


CREATE TABLE IF NOT EXISTS ponto_mototaxistas (
    id_ponto INTEGER NOT NULL,
    id_mototaxista INTEGER NOT NULL,
    PRIMARY KEY (id_ponto, id_mototaxista),
    FOREIGN KEY (id_ponto) REFERENCES pontos(id_ponto),
    FOREIGN KEY (id_mototaxista) REFERENCES mototaxistas(id_usuario)
);


CREATE TABLE IF NOT EXISTS corridas (
    id_corrida INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    bairro_origem VARCHAR(150) NOT NULL,
    rua_origem VARCHAR(150) NOT NULL,
    rua_destino VARCHAR(150) NOT NULL,
    bairro_destino VARCHAR(150) NOT NULL,
    finalizada BOOLEAN NOT NULL DEFAULT FALSE,
    id_cliente INTEGER NOT NULL,
    id_mototaxista INTEGER NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_usuario),
    FOREIGN KEY (id_mototaxista) REFERENCES mototaxistas(id_usuario)
);