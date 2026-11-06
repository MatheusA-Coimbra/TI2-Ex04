-- Banco de dados do projeto Aluminá ENEM
-- Banco: projetoenem

-- 1. Plano
CREATE TABLE plano (
    id SERIAL NOT NULL,
    nome VARCHAR(32) NOT NULL,

    CONSTRAINT pk_id_plano PRIMARY KEY (id)
);

-- Plano gratuito inicial
INSERT INTO plano (nome)
VALUES ('Gratuito');


-- 2. Instituição
CREATE TABLE instituicao (
    id SERIAL NOT NULL,
    nome_instituicao VARCHAR(100) NOT NULL,

    CONSTRAINT pk_id_instituicao PRIMARY KEY (id)
);


-- 3. Área
CREATE TABLE area (
    sigla VARCHAR(4) NOT NULL,
    nome_area VARCHAR(32) NOT NULL,

    CONSTRAINT pk_sigla_area PRIMARY KEY (sigla)
);



-- 4. Estudante
CREATE TABLE estudante (
    id SERIAL NOT NULL,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha_hash VARCHAR(256) NOT NULL,
    data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    instituicao_id INT,
    plano_id INT NOT NULL DEFAULT 1,

    CONSTRAINT pk_id_estudante PRIMARY KEY (id),

    CONSTRAINT fk_estudante_instituicao
        FOREIGN KEY (instituicao_id)
        REFERENCES instituicao (id),
    
    CONSTRAINT fk_estudante_plano
        FOREIGN KEY (plano_id)
        REFERENCES plano (id)
);


-- 5. Tema
CREATE TABLE tema (
    sigla CHAR(8) NOT NULL,
    nome_tema VARCHAR(40) NOT NULL,
    area_sigla VARCHAR(4) NOT NULL

    CONSTRAINT pk_sigla_tema PRIMARY KEY (sigla),

    CONSTRAINT fk_tema_area
        FOREIGN KEY (area_sigla)
        REFERENCES area (sigla)
);


-- 6. Questão
CREATE TABLE questao (
    codigo SERIAL NOT NULL,
    enunciado TEXT NOT NULL,
    gabarito CHAR(1) NOT NULL,
    nivel_dificuldade SMALLINT NOT NULL
        CHECK (nivel_dificuldade BETWEEN 1 AND 5),
    tema_sigla VARCHAR(8) NOT NULL,

    CONSTRAINT pk_codigo_quetao PRIMARY KEY (codigo),

    CONSTRAINT fk_questao_tema
        FOREIGN KEY (tema_id)
        REFERENCES tema (id)
);


-- 7. Redação
CREATE TABLE redacao (
    codigo SERIAL NOT NULL,
    enunciado TEXT NOT NULL,
    tema_sigla VARCHAR(8) NOT NULL,

    CONSTRAINT pk_codigo_questao_redacao PRIMARY KEY (codigo),

    CONSTRAINT fk_questao_redacao_tema
        FOREIGN KEY (tema_sigla)
        REFERENCES tema (sigla)
);

-- 8. Interação Questão
CREATE TABLE interacao_questao (
    estudante_id INT NOT NULL,
    questao_codigo INT NOT NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    alternativa_escolhida CHAR(1) NOT NULL,
    correta BOOLEAN NOT NULL,

    CONSTRAINT pk_interacao_questao PRIMARY KEY (estudante_id, questao_codigo, data_hora),

    CONSTRAINT fk_interacao_questao_estudante
        FOREIGN KEY (estudante_id)
        REFERENCES estudante (id),

    CONSTRAINT fk_interacao_questao_fechada
        FOREIGN KEY (questao_codigo)
        REFERENCES questao (codigo)
);


-- 9. Interação Redação
CREATE TABLE interacao_redacao (
    estudante_id INT NOT NULL,
    redacao_codigo INT NOT NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    redacao_escrita TEXT NOT NULL,
    nota SMALLINT NOT NULL CHECK (nota BETWEEN 0 AND 1000),

    CONSTRAINT pk_interacao_redacao PRIMARY KEY (estudante_id, redacao_codigo, data_hora),

    CONSTRAINT fk_interacao_redacao_estudante
        FOREIGN KEY (estudante_id)
        REFERENCES estudante (id),
    
    CONSTRAINT fk_interacao_questao_redacao
        FOREIGN KEY (redacao_codigo)
        REFERENCES redacao (codigo)
);


-- 10. Monitor
CREATE TABLE monitor (
    id SERIAL NOT NULL,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha_hash VARCHAR(256) NOT NULL,
    area_sigla VARCHAR(4) NOT NULL,

    CONSTRAINT pk_id_monitor PRIMARY KEY (id),

    CONSTRAINT fk_monitor_area
        FOREIGN KEY (area_sigla)
        REFERENCES area (sigla)
);


-- 11. Solicitação de monitoria
CREATE TABLE solicitacao_monitoria (
    estudante_id INT NOT NULL,
    monitor_id INT NOT NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status CHAR(8) NOT NULL DEFAULT 'pendente', -- 'pendente', 'deferido', 'aprovado'

    CONSTRAINT pk_solicitacao_monitoria PRIMARY KEY (estudante_id, monitor_id, data_hora),

    CONSTRAINT fk_solicitacao_estudante
        FOREIGN KEY (estudante_id)
        REFERENCES estudante (id),

    CONSTRAINT fk_solicitacao_monitor
        FOREIGN KEY (monitor_id)
        REFERENCES monitor (id)
);



-- 12. Log de uso de IA
CREATE TABLE uso_ia (
    id SERIAL NOT NULL,
    estudante_id INT NOT NULL,
    tipo_recurso VARCHAR(50) NOT NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_id_uso_ia PRIMARY KEY (id),

    CONSTRAINT fk_uso_ia_estudante
        FOREIGN KEY (estudante_id)
        REFERENCES estudante (id)
);


-- Índices
CREATE INDEX idx_questao_tema
    ON questao (tema_sigla);

CREATE INDEX idx_interacao_questao
    ON interacao_questao (estudante_id, questao_codigo, data_hora);

CREATE INDEX idx_interacao_questao_codigo
    ON interacao_questao (questao_codigo);

CREATE INDEX idx_interacao_redacao
    ON interacao_redacao (estudante_id, redacao_codigo, data_hora);

CREATE INDEX idx_interacao_redacao_codigo
    ON interacao_redacao (redacao_codigo);

CREATE INDEX idx_estudante_instituicao
    ON estudante (instituicao_id);

CREATE INDEX idx_uso_ia_estudante_periodo -- remover
    ON uso_ia (estudante_id, data_hora);

CREATE INDEX idx_solicitacao_estudante
    ON solicitacao_monitoria (estudante_id, monitor_id, data_hora);
