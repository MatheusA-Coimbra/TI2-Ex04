-- Banco de dados do projeto Aluminá ENEM
-- Banco: projetoenem

-- 1. Plano
CREATE TABLE plano (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    limite_uso_ia_mensal INT NOT NULL DEFAULT 0
);

-- Plano gratuito inicial
INSERT INTO plano (nome, limite_uso_ia_mensal)
VALUES ('Gratuito', 0);


-- 2. Instituição
CREATE TABLE instituicao (
    id SERIAL PRIMARY KEY,
    nome_instituicao VARCHAR(150) NOT NULL,
    logo_url VARCHAR(255),
    cor_primaria VARCHAR(7),
    data_contratacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- 3. Estudante
CREATE TABLE estudante (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(256) NOT NULL,
    data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    instituicao_id INT,
    plano_id INT NOT NULL DEFAULT 1,

    CONSTRAINT fk_estudante_instituicao
        FOREIGN KEY (instituicao_id)
        REFERENCES instituicao (id),

    CONSTRAINT fk_estudante_plano
        FOREIGN KEY (plano_id)
        REFERENCES plano (id)
);


-- 4. Tema
CREATE TABLE tema (
    id SERIAL PRIMARY KEY,
    nome_tema VARCHAR(150) NOT NULL,
    area_conhecimento VARCHAR(100) NOT NULL
);


-- 5. Questão
CREATE TABLE questao (
    id SERIAL PRIMARY KEY,
    enunciado TEXT NOT NULL,
    alternativas TEXT NOT NULL,
    gabarito VARCHAR(5) NOT NULL,
    ano_edicao INT NOT NULL,
    nivel_dificuldade INT NOT NULL
        CHECK (nivel_dificuldade BETWEEN 1 AND 5),
    frequencia_historica NUMERIC(5,2) NOT NULL DEFAULT 0,
    tema_id INT NOT NULL,

    CONSTRAINT fk_questao_tema
        FOREIGN KEY (tema_id)
        REFERENCES tema (id)
);


-- 6. Interação
CREATE TABLE interacao (
    id SERIAL PRIMARY KEY,
    estudante_id INT NOT NULL,
    questao_id INT NOT NULL,
    alternativa_escolhida VARCHAR(5) NOT NULL,
    correta BOOLEAN NOT NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_interacao_estudante
        FOREIGN KEY (estudante_id)
        REFERENCES estudante (id),

    CONSTRAINT fk_interacao_questao
        FOREIGN KEY (questao_id)
        REFERENCES questao (id)
);


-- 7. Monitor
CREATE TABLE monitor (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(256) NOT NULL,
    area_atuacao VARCHAR(100) NOT NULL,
    valor_hora NUMERIC(8,2) NOT NULL
);


-- 8. Solicitação de monitoria
CREATE TABLE solicitacao_monitoria (
    id SERIAL PRIMARY KEY,
    estudante_id INT NOT NULL,
    monitor_id INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pendente',
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor_cobrado NUMERIC(8,2) NOT NULL,
    comissao_plataforma NUMERIC(8,2) NOT NULL,

    CONSTRAINT fk_solicitacao_estudante
        FOREIGN KEY (estudante_id)
        REFERENCES estudante (id),

    CONSTRAINT fk_solicitacao_monitor
        FOREIGN KEY (monitor_id)
        REFERENCES monitor (id)
);


-- 9. Uso de IA
CREATE TABLE uso_ia (
    id SERIAL PRIMARY KEY,
    estudante_id INT NOT NULL,
    tipo_recurso VARCHAR(50) NOT NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_uso_ia_estudante
        FOREIGN KEY (estudante_id)
        REFERENCES estudante (id)
);


-- Índices
CREATE INDEX idx_questao_tema
    ON questao (tema_id);

CREATE INDEX idx_interacao_estudante
    ON interacao (estudante_id);

CREATE INDEX idx_questao_prioridade
    ON questao (frequencia_historica DESC, nivel_dificuldade DESC);

CREATE INDEX idx_estudante_instituicao
    ON estudante (instituicao_id);

CREATE INDEX idx_uso_ia_estudante_periodo
    ON uso_ia (estudante_id, data_hora);

CREATE INDEX idx_solicitacao_estudante
    ON solicitacao_monitoria (estudante_id);

CREATE INDEX idx_solicitacao_monitor
    ON solicitacao_monitoria (monitor_id);