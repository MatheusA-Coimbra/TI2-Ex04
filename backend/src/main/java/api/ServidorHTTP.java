package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.Aplicacao;
import model.Estudante;
import model.Tema;
import model.Questao;
import model.Interacao;
import model.Plano;
import service.EstudanteService;
import service.PlanoService;
import service.InteracaoService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorHTTP {

    private static final int PORTA = 8080;
    private static final int TAMANHO_MAXIMO_JSON = 10000;

    private final EstudanteService estudanteService;
    private final PlanoService planoService;
    private final InteracaoService interacaoService;
    private final Aplicacao aplicacao;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Integer> sessoes = new ConcurrentHashMap<>();

    public ServidorHTTP() {
        this.estudanteService = new EstudanteService();
        this.planoService = new PlanoService();
        this.interacaoService = new InteracaoService();
        this.aplicacao = new Aplicacao();
    }

    // =========================================
    // INICIALIZAÇÃO DO SERVIDOR
    // =========================================

    public void iniciar() throws IOException {

        HttpServer servidor = HttpServer.create(
            new InetSocketAddress(PORTA), 0
        );

        servidor.createContext(
            "/api/estudantes/cadastro",
            this::cadastrar
        );

        servidor.createContext(
            "/api/estudantes/login",
            this::login
        );

        servidor.createContext(
            "/api/estudantes/perfil",
            this::perfil
        );

        servidor.createContext(
            "/api/temas",
            this::temas
        );

        servidor.createContext(
            "/api/questoes",
            this::questoes
        );

        servidor.createContext(
            "/api/interacoes",
            this::interacoes
        );
        
        servidor.createContext(
        	    "/api/interacoes/respostas",
        	    this::registrarResposta
        	);

        servidor.createContext(
            "/api/planos",
            this::planos
        );

        servidor.setExecutor(null);
        servidor.start();

        System.out.println("Servidor iniciado!");
        System.out.println("Endereço: http://localhost:" + PORTA);
    }

    // =========================================
    // CADASTRO DE ESTUDANTE
    // =========================================

    private void cadastrar(HttpExchange requisicao)
            throws IOException {

        if (requisicao.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            responder(requisicao, 204, "");
            return;
        }

        if (!requisicao.getRequestMethod().equalsIgnoreCase("POST")) {
            responder(requisicao, 405,
                "{\"erro\":\"Método não permitido.\"}");
            return;
        }

        try {

            JsonNode dados = lerJSON(requisicao);

            String nome = obterTexto(dados, "nome");
            String email = obterTexto(dados, "email");
            String senha = obterTexto(dados, "senha");

            Estudante estudante = new Estudante();

            estudante.setNome(nome);
            estudante.setEmail(email);
            estudante.setSenhaHash(senha);

            int id = estudanteService.cadastrar(estudante);

            String resposta = objectMapper.writeValueAsString(
                Map.of(
                    "mensagem", "Cadastro realizado com sucesso.",
                    "id", id
                )
            );

            responder(requisicao, 201, resposta);

        } catch (IllegalArgumentException e) {

            responder(requisicao, 400, jsonErro(e.getMessage()));

        } catch (SQLException e) {

            if ("23505".equals(e.getSQLState())) {

                responder(requisicao, 409,
                    "{\"erro\":\"Este e-mail já está cadastrado.\"}");

            } else {

                e.printStackTrace();

                responder(requisicao, 500,
                    "{\"erro\":\"Erro ao cadastrar estudante.\"}");
            }

        } catch (Exception e) {

            e.printStackTrace();

            responder(requisicao, 400,
                "{\"erro\":\"Requisição inválida.\"}");
        }
    }

    // =========================================
    // LOGIN DE ESTUDANTE
    // =========================================

    private void login(HttpExchange requisicao)
            throws IOException {

        if (requisicao.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            responder(requisicao, 204, "");
            return;
        }

        if (!requisicao.getRequestMethod().equalsIgnoreCase("POST")) {
            responder(requisicao, 405,
                "{\"erro\":\"Método não permitido.\"}");
            return;
        }

        try {

            JsonNode dados = lerJSON(requisicao);

            String email = obterTexto(dados, "email");
            String senha = obterTexto(dados, "senha");

            Estudante estudante =
                estudanteService.autenticar(email, senha);

            if (estudante == null) {

                responder(requisicao, 401,
                    "{\"erro\":\"E-mail ou senha incorretos.\"}");

                return;
            }

            String token = UUID.randomUUID().toString();
            sessoes.put(token, estudante.getId());

            Map<String, Object> dadosEstudante = new LinkedHashMap<>();
            dadosEstudante.put("id", estudante.getId());
            dadosEstudante.put("nome", estudante.getNome());
            dadosEstudante.put("email", estudante.getEmail());

            Map<String, Object> dadosResposta = new LinkedHashMap<>();
            dadosResposta.put("mensagem", "Login realizado com sucesso.");
            dadosResposta.put("token", token);
            dadosResposta.put("estudante", dadosEstudante);

            String resposta = objectMapper.writeValueAsString(dadosResposta);

            responder(requisicao, 200, resposta);

        } catch (IllegalArgumentException e) {

            responder(requisicao, 400, jsonErro(e.getMessage()));

        } catch (SQLException e) {

            e.printStackTrace();

            responder(requisicao, 500,
                "{\"erro\":\"Erro ao realizar login.\"}");

        } catch (Exception e) {

            e.printStackTrace();

            responder(requisicao, 400,
                "{\"erro\":\"Requisição inválida.\"}");
        }
    }

    // =========================================
    // CONSULTA E ATUALIZAÇÃO DO PERFIL
    // =========================================

    private void perfil(HttpExchange requisicao)
            throws IOException {

        String metodo = requisicao.getRequestMethod();

        if (metodo.equalsIgnoreCase("OPTIONS")) {
            responder(requisicao, 204, "");
            return;
        }

        try {

            // Verifica o token de autenticação
            String cabecalho = requisicao.getRequestHeaders()
                .getFirst("Authorization");

            if (cabecalho == null || !cabecalho.startsWith("Bearer ")) {
                responder(requisicao, 401,
                    "{\"erro\":\"Autenticação necessária.\"}");
                return;
            }

            String token = cabecalho.substring(7);
            Integer estudanteId = sessoes.get(token);

            if (estudanteId == null) {
                responder(requisicao, 401,
                    "{\"erro\":\"Sessão inválida. Faça login novamente.\"}");
                return;
            }

            // GET /api/estudantes/perfil
            if (metodo.equalsIgnoreCase("GET")) {

                Estudante estudante =
                    aplicacao.buscarEstudantePorId(estudanteId);

                if (estudante == null) {
                    responder(requisicao, 404,
                        "{\"erro\":\"Estudante não encontrado.\"}");
                    return;
                }

                Map<String, Object> dados = new LinkedHashMap<>();
                dados.put("id", estudante.getId());
                dados.put("nome", estudante.getNome());
                dados.put("email", estudante.getEmail());
                dados.put("instituicaoId", estudante.getInstituicaoId());
                dados.put("planoId", estudante.getPlanoId());

                responder(requisicao, 200,
                    objectMapper.writeValueAsString(dados));
                return;
            }

            // PUT /api/estudantes/perfil
            if (metodo.equalsIgnoreCase("PUT")) {

                JsonNode dados = lerJSON(requisicao);
                String nome = obterTexto(dados, "nome");

                Integer instituicaoId = null;
                JsonNode instituicao = dados.get("instituicaoId");

                if (instituicao != null && !instituicao.isNull()) {

                    if (!instituicao.isIntegralNumber()
                            || !instituicao.canConvertToInt()) {

                        throw new IllegalArgumentException(
                            "O ID da instituição deve ser um número inteiro válido."
                        );
                    }

                    instituicaoId = instituicao.intValue();
                }

                Estudante estudante = new Estudante();
                estudante.setId(estudanteId);
                estudante.setNome(nome);
                estudante.setInstituicaoId(instituicaoId);

                boolean atualizado =
                    aplicacao.atualizarPerfil(estudante);

                if (!atualizado) {
                    responder(requisicao, 404,
                        "{\"erro\":\"Não foi possível atualizar o perfil.\"}");
                    return;
                }

                responder(requisicao, 200,
                    "{\"mensagem\":\"Perfil atualizado com sucesso.\"}");
                return;
            }

            responder(requisicao, 405,
                "{\"erro\":\"Método não permitido.\"}");

        } catch (IllegalArgumentException e) {

            responder(requisicao, 400, jsonErro(e.getMessage()));

        } catch (SQLException e) {

            e.printStackTrace();

            responder(requisicao, 500,
                "{\"erro\":\"Erro ao acessar o perfil.\"}");

        } catch (Exception e) {

            e.printStackTrace();

            responder(requisicao, 400,
                "{\"erro\":\"Requisição inválida.\"}");
        }
    }

    // =========================================
    // CONSULTA DE PLANOS
    // =========================================

    private void planos(HttpExchange requisicao)
            throws IOException {

        String metodo = requisicao.getRequestMethod();

        if (metodo.equalsIgnoreCase("OPTIONS")) {
            responder(requisicao, 204, "");
            return;
        }

        if (!metodo.equalsIgnoreCase("GET")) {
            responder(requisicao, 405,
            "{\"erro\":\"Método não permitido.\"}");
            return;
        }
        try {
            List<Plano> planos = planoService.listar();

            responder(requisicao, 200,
                objectMapper.writeValueAsString(planos));

        } catch (SQLException e) {
            e.printStackTrace();

            responder(requisicao, 500,
                "{\"erro\":\"Erro ao acessar os planos.\"}");
        }
    }

    // =========================================
    // CRUD DE TEMAS
    // =========================================

    private void temas(HttpExchange requisicao)
            throws IOException {

        String metodo = requisicao.getRequestMethod();

        if (metodo.equalsIgnoreCase("OPTIONS")) {
            responder(requisicao, 204, "");
            return;
        }

        try {

            String caminho = requisicao.getRequestURI().getPath();
            String base = "/api/temas";

            String identificador = caminho.substring(base.length());

            if (identificador.isEmpty() &&
                metodo.equalsIgnoreCase("GET")) {

                List<Tema> temas = aplicacao.listarTemas();

                responder(requisicao, 200,
                    objectMapper.writeValueAsString(temas));
                return;
            }

            if (identificador.isEmpty() &&
                metodo.equalsIgnoreCase("POST")) {

                JsonNode dados = lerJSON(requisicao);

                Tema tema = new Tema();
                tema.setNomeTema(obterTexto(dados, "nomeTema"));
                tema.setAreaConhecimento(
                    obterTexto(dados, "areaConhecimento")
                );

                int id = aplicacao.inserirTema(tema);

                responder(requisicao, 201,
                    objectMapper.writeValueAsString(
                        Map.of(
                            "mensagem", "Tema cadastrado com sucesso.",
                            "id", id
                        )
                    ));
                return;
            }

            if (!identificador.matches("/[0-9]+")) {
                responder(requisicao, 404,
                    "{\"erro\":\"Rota não encontrada.\"}");
                return;
            }

            int id = Integer.parseInt(identificador.substring(1));

            if (metodo.equalsIgnoreCase("GET")) {

                Tema tema = aplicacao.buscarTemaPorId(id);

                if (tema == null) {
                    responder(requisicao, 404,
                        "{\"erro\":\"Tema não encontrado.\"}");
                    return;
                }

                responder(requisicao, 200,
                    objectMapper.writeValueAsString(tema));
                return;
            }

            if (metodo.equalsIgnoreCase("PUT")) {

                JsonNode dados = lerJSON(requisicao);

                Tema tema = new Tema();
                tema.setId(id);
                tema.setNomeTema(obterTexto(dados, "nomeTema"));
                tema.setAreaConhecimento(
                    obterTexto(dados, "areaConhecimento")
                );

                boolean atualizado = aplicacao.atualizarTema(tema);

                if (!atualizado) {
                    responder(requisicao, 404,
                        "{\"erro\":\"Tema não encontrado.\"}");
                    return;
                }

                responder(requisicao, 200,
                    "{\"mensagem\":\"Tema atualizado com sucesso.\"}");
                return;
            }

            if (metodo.equalsIgnoreCase("DELETE")) {

                boolean removido = aplicacao.removerTema(id);

                if (!removido) {
                    responder(requisicao, 404,
                        "{\"erro\":\"Tema não encontrado.\"}");
                    return;
                }

                responder(requisicao, 204, "");
                return;
            }

            responder(requisicao, 405,
                "{\"erro\":\"Método não permitido.\"}");

        } catch (NumberFormatException e) {

            responder(requisicao, 400,
                "{\"erro\":\"ID inválido.\"}");

        } catch (IllegalArgumentException e) {

            responder(requisicao, 400, jsonErro(e.getMessage()));

        } catch (SQLException e) {

            e.printStackTrace();

            if ("23503".equals(e.getSQLState())) {
                responder(requisicao, 409,
                    "{\"erro\":\"Não foi possível realizar a operação porque existem registros relacionados.\"}");
            } else {
                responder(requisicao, 500,
                    "{\"erro\":\"Erro ao acessar os temas.\"}");
            }

        } catch (Exception e) {

            e.printStackTrace();

            responder(requisicao, 400,
                "{\"erro\":\"Requisição inválida.\"}");
        }
    }

    // =========================================
    // CRUD DE QUESTÕES
    // =========================================

    private void questoes(HttpExchange requisicao)
            throws IOException {

        String metodo = requisicao.getRequestMethod();

        if (metodo.equalsIgnoreCase("OPTIONS")) {
            responder(requisicao, 204, "");
            return;
        }

        try {

            String caminho = requisicao.getRequestURI().getPath();
            String base = "/api/questoes";

            String identificador = caminho.substring(base.length());

            // GET /api/questoes
            if (identificador.isEmpty() &&
                metodo.equalsIgnoreCase("GET")) {

                List<Questao> questoes = aplicacao.listarQuestoes();

                responder(requisicao, 200,
                    objectMapper.writeValueAsString(questoes));
                return;
            }

            // POST /api/questoes
            if (identificador.isEmpty() &&
                metodo.equalsIgnoreCase("POST")) {

                JsonNode dados = lerJSON(requisicao);
                Questao questao = criarQuestao(dados);

                int id = aplicacao.inserirQuestao(questao);

                responder(requisicao, 201,
                    objectMapper.writeValueAsString(
                        Map.of(
                            "mensagem", "Questão cadastrada com sucesso.",
                            "id", id
                        )
                    ));
                return;
            }

            // Operações que exigem ID
            if (!identificador.matches("/[0-9]+")) {
                responder(requisicao, 404,
                    "{\"erro\":\"Rota não encontrada.\"}");
                return;
            }

            int id = Integer.parseInt(identificador.substring(1));

            // GET /api/questoes/{id}
            if (metodo.equalsIgnoreCase("GET")) {

                Questao questao = aplicacao.buscarQuestaoPorId(id);

                if (questao == null) {
                    responder(requisicao, 404,
                        "{\"erro\":\"Questão não encontrada.\"}");
                    return;
                }

                responder(requisicao, 200,
                    objectMapper.writeValueAsString(questao));
                return;
            }

            // PUT /api/questoes/{id}
            if (metodo.equalsIgnoreCase("PUT")) {

                JsonNode dados = lerJSON(requisicao);
                Questao questao = criarQuestao(dados);
                questao.setId(id);

                boolean atualizado =
                    aplicacao.atualizarQuestao(questao);

                if (!atualizado) {
                    responder(requisicao, 404,
                        "{\"erro\":\"Questão não encontrada.\"}");
                    return;
                }

                responder(requisicao, 200,
                    "{\"mensagem\":\"Questão atualizada com sucesso.\"}");
                return;
            }

            // DELETE /api/questoes/{id}
            if (metodo.equalsIgnoreCase("DELETE")) {

                boolean removido = aplicacao.removerQuestao(id);

                if (!removido) {
                    responder(requisicao, 404,
                        "{\"erro\":\"Questão não encontrada.\"}");
                    return;
                }

                responder(requisicao, 204, "");
                return;
            }

            responder(requisicao, 405,
                "{\"erro\":\"Método não permitido.\"}");

        } catch (NumberFormatException e) {

            responder(requisicao, 400,
                "{\"erro\":\"ID ou campo numérico inválido.\"}");

        } catch (IllegalArgumentException e) {

            responder(requisicao, 400, jsonErro(e.getMessage()));

        } catch (SQLException e) {

            e.printStackTrace();

            if ("23503".equals(e.getSQLState())) {
                responder(requisicao, 409,
                    "{\"erro\":\"O tema informado não existe ou a questão possui registros relacionados.\"}");
            } else {
                responder(requisicao, 500,
                    "{\"erro\":\"Erro ao acessar as questões.\"}");
            }

        } catch (Exception e) {

            e.printStackTrace();

            responder(requisicao, 400,
                "{\"erro\":\"Requisição inválida.\"}");
        }
    }

    // =========================================
    // CRUD DE INTERAÇÕES
    // =========================================

    private void interacoes(HttpExchange requisicao)
            throws IOException {

        String metodo = requisicao.getRequestMethod();

        if (metodo.equalsIgnoreCase("OPTIONS")) {
            responder(requisicao, 204, "");
            return;
        }

        try {

            String caminho = requisicao.getRequestURI().getPath();
            String base = "/api/interacoes";

            String identificador = caminho.substring(base.length());

            // GET /api/interacoes
            if (identificador.isEmpty() &&
                metodo.equalsIgnoreCase("GET")) {

            List<Map<String, Object>> interacoes = aplicacao.listarInteracoes()
            .stream()
            .map(this::interacaoParaJSON)
            .toList();

                responder(requisicao, 200,
                    objectMapper.writeValueAsString(interacoes));
                return;
            }

            // POST /api/interacoes
            if (identificador.isEmpty() &&
                metodo.equalsIgnoreCase("POST")) {

                JsonNode dados = lerJSON(requisicao);

                Interacao interacao = criarInteracao(dados);

                int id = aplicacao.inserirInteracao(interacao);

                responder(requisicao, 201,
                    objectMapper.writeValueAsString(
                        Map.of(
                            "mensagem", "Interação cadastrada com sucesso.",
                            "id", id
                        )
                    ));
                return;
            }

            // Operações que exigem ID
            if (!identificador.matches("/[0-9]+")) {
                responder(requisicao, 404,
                    "{\"erro\":\"Rota não encontrada.\"}");
                return;
            }

            int id = Integer.parseInt(identificador.substring(1));

            // GET /api/interacoes/{id}
            if (metodo.equalsIgnoreCase("GET")) {

                Interacao interacao =
                    aplicacao.buscarInteracaoPorId(id);

                if (interacao == null) {
                    responder(requisicao, 404,
                        "{\"erro\":\"Interação não encontrada.\"}");
                    return;
                }

                responder(requisicao, 200,
                    objectMapper.writeValueAsString(
                        interacaoParaJSON(interacao)
                    ));
                return;
            }

            // PUT /api/interacoes/{id}
            if (metodo.equalsIgnoreCase("PUT")) {

                JsonNode dados = lerJSON(requisicao);

                Interacao interacao = criarInteracao(dados);
                interacao.setId(id);

                boolean atualizado =
                    aplicacao.atualizarInteracao(interacao);

                if (!atualizado) {
                    responder(requisicao, 404,
                        "{\"erro\":\"Interação não encontrada.\"}");
                    return;
                }

                responder(requisicao, 200,
                    "{\"mensagem\":\"Interação atualizada com sucesso.\"}");
                return;
            }

            // DELETE /api/interacoes/{id}
            if (metodo.equalsIgnoreCase("DELETE")) {

                boolean removido =
                    aplicacao.removerInteracao(id);

                if (!removido) {
                    responder(requisicao, 404,
                        "{\"erro\":\"Interação não encontrada.\"}");
                    return;
                }

                responder(requisicao, 204, "");
                return;
            }

            responder(requisicao, 405,
                "{\"erro\":\"Método não permitido.\"}");

        } catch (NumberFormatException e) {

            responder(requisicao, 400,
                "{\"erro\":\"ID inválido.\"}");

        } catch (IllegalArgumentException e) {

            responder(requisicao, 400, jsonErro(e.getMessage()));

        } catch (SQLException e) {

            e.printStackTrace();

            if ("23503".equals(e.getSQLState())) {
                responder(requisicao, 409,
                    "{\"erro\":\"O estudante ou a questão informada não existe.\"}");
            } else {
                responder(requisicao, 500,
                    "{\"erro\":\"Erro ao acessar as interações.\"}");
            }

        } catch (Exception e) {

            e.printStackTrace();

            responder(requisicao, 400,
                "{\"erro\":\"Requisição inválida.\"}");
        }
    }     

        // REGISTRAR RESPOSTA E VERIFICAR GABARITO
        private void registrarResposta(HttpExchange requisicao)
                throws IOException {

            String metodo = requisicao.getRequestMethod();

            if (metodo.equalsIgnoreCase("OPTIONS")) {
                responder(requisicao, 204, "");
                return;
            }

            if (!requisicao.getRequestURI().getPath()
                    .equals("/api/interacoes/respostas")) {
                responder(requisicao, 404,
                    "{\"erro\":\"Rota não encontrada.\"}");
                return;
            }

            if (!metodo.equalsIgnoreCase("POST")) {
                responder(requisicao, 405,
                    "{\"erro\":\"Método não permitido.\"}");
                return;
            }

            // Identificar o estudante autenticado
            String autorizacao = requisicao.getRequestHeaders()
                    .getFirst("Authorization");

            if (autorizacao == null
                    || !autorizacao.startsWith("Bearer ")) {
                responder(requisicao, 401,
                    "{\"erro\":\"Autenticação necessária.\"}");
                return;
            }

            String token = autorizacao.substring(7).trim();
            Integer estudanteId = sessoes.get(token);

            if (estudanteId == null) {
                responder(requisicao, 401,
                    "{\"erro\":\"Sessão inválida.\"}");
                return;
            }

            try {
                JsonNode dados = lerJSON(requisicao);

                int questaoId = obterInteiro(dados, "questaoId");

                String alternativa = obterTexto(
                    dados, "alternativaEscolhida"
                );

                boolean correta = interacaoService.registrarResposta(
                    estudanteId, questaoId, alternativa
                );

                responder(requisicao, 201,
                    objectMapper.writeValueAsString(
                        Map.of(
                            "mensagem", "Resposta registrada com sucesso.",
                            "correta", correta
                        )
                    )
                );

            } catch (IllegalArgumentException | IllegalStateException e) {
                responder(requisicao, 400, jsonErro(e.getMessage()));

            } catch (SQLException e) {
                e.printStackTrace();

                if ("23503".equals(e.getSQLState())) {
                    responder(requisicao, 409,
                        "{\"erro\":\"Estudante ou questão inexistente.\"}");
                } else {
                    responder(requisicao, 500,
                        "{\"erro\":\"Erro ao registrar resposta.\"}");
                }
            }
        }

    // =========================================
    // CRIAÇÃO DE OBJETO QUESTÃO A PARTIR DO JSON
    // =========================================

    private Questao criarQuestao(JsonNode dados) {

        Questao questao = new Questao();

        questao.setEnunciado(obterTexto(dados, "enunciado"));
        questao.setAlternativas(obterTexto(dados, "alternativas"));
        questao.setGabarito(obterTexto(dados, "gabarito"));
        questao.setAnoEdicao(obterInteiro(dados, "anoEdicao"));
        questao.setNivelDificuldade(
            obterInteiro(dados, "nivelDificuldade")
        );
        questao.setFrequenciaHistorica(
            obterDecimal(dados, "frequenciaHistorica")
        );
        questao.setTemaId(obterInteiro(dados, "temaId"));

        return questao;
    }

    // =========================================
    // CRIAÇÃO DE OBJETO INTERAÇÃO A PARTIR DO JSON
    // =========================================

    private Interacao criarInteracao(JsonNode dados) {

        Interacao interacao = new Interacao();

        interacao.setEstudanteId(
            obterInteiro(dados, "estudanteId")
        );

        interacao.setQuestaoId(
            obterInteiro(dados, "questaoId")
        );

        interacao.setAlternativaEscolhida(
            obterTexto(dados, "alternativaEscolhida")
        );

        JsonNode correta = dados.get("correta");

        if (correta == null || !correta.isBoolean()) {
            throw new IllegalArgumentException(
                "O campo correta deve ser verdadeiro ou falso."
            );
        }

        interacao.setCorreta(correta.booleanValue());

        JsonNode dataHora = dados.get("dataHora");

        if (dataHora != null && !dataHora.isNull()) {

            if (!dataHora.isTextual()) {
                throw new IllegalArgumentException(
                    "O campo dataHora deve ser um texto válido."
                );
            }

            interacao.setDataHora(
                LocalDateTime.parse(dataHora.asText())
            );
        }

        return interacao;
    }

    // =========================================
    // CONVERSÃO DE INTERAÇÃO PARA JSON
    // =========================================

    private Map<String, Object> interacaoParaJSON(
            Interacao interacao) {

        Map<String, Object> dados = new LinkedHashMap<>();

        dados.put("id", interacao.getId());
        dados.put("estudanteId", interacao.getEstudanteId());
        dados.put("questaoId", interacao.getQuestaoId());
        dados.put(
            "alternativaEscolhida",
            interacao.getAlternativaEscolhida()
        );
        dados.put("correta", interacao.isCorreta());

        dados.put(
            "dataHora",
            interacao.getDataHora() == null
                ? null
                : interacao.getDataHora().toString()
        );

        return dados;
    }

    // =========================================
    // LEITURA DO JSON
    // =========================================

    private JsonNode lerJSON(HttpExchange requisicao)
            throws IOException {

        try (InputStream entrada = requisicao.getRequestBody()) {

            byte[] corpo = entrada.readNBytes(
                TAMANHO_MAXIMO_JSON + 1
            );

            if (corpo.length == 0 ||
                corpo.length > TAMANHO_MAXIMO_JSON) {

                throw new IllegalArgumentException(
                    "Corpo da requisição inválido."
                );
            }

            JsonNode dados = objectMapper.readTree(corpo);

            if (dados == null || !dados.isObject()) {

                throw new IllegalArgumentException(
                    "O JSON enviado deve ser um objeto válido."
                );
            }

            return dados;

        } catch (JsonProcessingException e) {

            throw new IllegalArgumentException(
                "Formato JSON inválido."
            );
        }
    }

    // =========================================
    // OBTENÇÃO DOS CAMPOS DO JSON
    // =========================================

    private String obterTexto(JsonNode dados, String campo) {

        JsonNode valor = dados.get(campo);

        if (valor == null || !valor.isTextual()) {
            return null;
        }

        return valor.asText();
    }

    private int obterInteiro(JsonNode dados, String campo) {

        JsonNode valor = dados.get(campo);

        if (valor == null || !valor.isIntegralNumber() ||
            !valor.canConvertToInt()) {

            throw new IllegalArgumentException(
                "O campo " + campo + " deve ser um número inteiro válido."
            );
        }

        return valor.intValue();
    }

    private BigDecimal obterDecimal(JsonNode dados, String campo) {

        JsonNode valor = dados.get(campo);

        if (valor == null || !valor.isNumber()) {

            throw new IllegalArgumentException(
                "O campo " + campo + " deve ser numérico."
            );
        }

        return valor.decimalValue();
    }

    // =========================================
    // RESPOSTAS HTTP
    // =========================================

    private void responder(
            HttpExchange requisicao,
            int status,
            String resposta) throws IOException {

        requisicao.getResponseHeaders().set(
            "Content-Type",
            "application/json; charset=UTF-8"
        );

        requisicao.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "http://localhost:8000"
        );

        requisicao.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "GET, POST, PUT, DELETE, OPTIONS"
        );

        requisicao.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type, Authorization"
        );

        if (status == 204) {
            requisicao.sendResponseHeaders(204, -1);
            requisicao.close();
            return;
        }

        byte[] bytes = resposta.getBytes(StandardCharsets.UTF_8);

        requisicao.sendResponseHeaders(status, bytes.length);

        try (OutputStream saida = requisicao.getResponseBody()) {
            saida.write(bytes);
        }
    }

    // =========================================
    // TRATAMENTO DE ERROS JSON
    // =========================================

    private String jsonErro(String mensagem) throws IOException {

        return objectMapper.writeValueAsString(
            Map.of("erro", mensagem)
        );
    }
}