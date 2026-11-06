package service;

import dao.QuestaoDAO;
import model.Questao;

import java.sql.SQLException;
import java.util.List;

public class QuestaoService {

    private final QuestaoDAO questaoDAO;

    public QuestaoService() {
        this.questaoDAO = new QuestaoDAO();
    }

    // Valida os dados da questão
    private void validarDadosQuestao(Questao questao) {

        if (questao == null) {
            throw new IllegalArgumentException("A questão não pode ser nula.");
        }

        if (questao.getEnunciado() == null || questao.getEnunciado().isBlank()) {
            throw new IllegalArgumentException("O enunciado da questão é obrigatório.");
        }

        if (questao.getAlternativas() == null || questao.getAlternativas().isBlank()) {
            throw new IllegalArgumentException("As alternativas são obrigatórias.");
        }

        if (questao.getGabarito() == null || questao.getGabarito().isBlank()) {
            throw new IllegalArgumentException("O gabarito é obrigatório.");
        }

        if (questao.getGabarito().length() > 5) {
            throw new IllegalArgumentException("O gabarito não pode ultrapassar 5 caracteres.");
        }

        if (questao.getNivelDificuldade() < 1 || questao.getNivelDificuldade() > 5) {
            throw new IllegalArgumentException("O nível de dificuldade deve estar entre 1 e 5.");
        }

        if (questao.getFrequenciaHistorica() == null) {
            throw new IllegalArgumentException("A frequência histórica é obrigatória.");
        }

        if (questao.getTemaId() <= 0) {
            throw new IllegalArgumentException("O ID do tema deve ser positivo.");
        }
    }

    // Insere uma questão
    public int inserir(Questao questao) throws SQLException {

        validarDadosQuestao(questao);

        return questaoDAO.inserir(questao);
    }

    // Lista todas as questões
    public List<Questao> listar() throws SQLException {
        return questaoDAO.listar();
    }

    // Busca uma questão pelo ID
    public Questao buscarPorId(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException("O ID deve ser positivo.");
        }

        return questaoDAO.buscarPorId(id);
    }

    // Atualiza uma questão
    public boolean atualizar(Questao questao) throws SQLException {

        validarDadosQuestao(questao);

        if (questao.getId() <= 0) {
            throw new IllegalArgumentException("O ID da questão deve ser positivo.");
        }

        return questaoDAO.atualizar(questao);
    }

    // Remove uma questão pelo ID
    public boolean remover(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException("O ID deve ser positivo.");
        }

        return questaoDAO.remover(id);
    }
}