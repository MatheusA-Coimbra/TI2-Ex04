package app;

import model.Questao;
import model.Tema;
import model.Interacao;
import model.Estudante;

import service.QuestaoService;
import service.TemaService;
import service.InteracaoService;
import service.EstudanteService;

import java.sql.SQLException;
import java.util.List;

public class Aplicacao {

    private final QuestaoService questaoService;
    private final TemaService temaService;
    private final InteracaoService interacaoService;
    private final EstudanteService estudanteService;

    public Aplicacao() {
        this.questaoService = new QuestaoService();
        this.temaService = new TemaService();
        this.interacaoService = new InteracaoService();
        this.estudanteService = new EstudanteService();
    }

    // =========================================
    // CRUD QUESTÃO
    // =========================================

    public int inserirQuestao(Questao questao) throws SQLException {
        return questaoService.inserir(questao);
    }

    public List<Questao> listarQuestoes() throws SQLException {
        return questaoService.listar();
    }

    public Questao buscarQuestaoPorId(int id) throws SQLException {
        return questaoService.buscarPorId(id);
    }

    public boolean atualizarQuestao(Questao questao) throws SQLException {
        return questaoService.atualizar(questao);
    }

    public boolean removerQuestao(int id) throws SQLException {
        return questaoService.remover(id);
    }

    // =========================================
    // CRUD TEMA
    // =========================================

    public int inserirTema(Tema tema) throws SQLException {
        return temaService.inserir(tema);
    }

    public List<Tema> listarTemas() throws SQLException {
        return temaService.listar();
    }

    public Tema buscarTemaPorId(int id) throws SQLException {
        return temaService.buscarPorId(id);
    }

    public boolean atualizarTema(Tema tema) throws SQLException {
        return temaService.atualizar(tema);
    }

    public boolean removerTema(int id) throws SQLException {
        return temaService.remover(id);
    }

    // =========================================
    // CRUD INTERAÇÃO
    // =========================================

    public int inserirInteracao(Interacao interacao) throws SQLException {
        return interacaoService.inserir(interacao);
    }

    public List<Interacao> listarInteracoes() throws SQLException {
        return interacaoService.listar();
    }

    public Interacao buscarInteracaoPorId(int id) throws SQLException {
        return interacaoService.buscarPorId(id);
    }

    public boolean atualizarInteracao(Interacao interacao) throws SQLException {
        return interacaoService.atualizar(interacao);
    }

    public boolean removerInteracao(int id) throws SQLException {
        return interacaoService.remover(id);
    }

    // =========================================
    // PERFIL DO ESTUDANTE
    // =========================================

    public Estudante buscarEstudantePorId(int id) throws SQLException {
        return estudanteService.buscarPorId(id);
    }

    public boolean atualizarPerfil(Estudante estudante) throws SQLException {
        return estudanteService.atualizarPerfil(estudante);
    }
}