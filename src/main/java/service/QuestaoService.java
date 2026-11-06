package service;

import dao.QuestaoDAO;
import model.Questao;
import java.util.List;

public class QuestaoService {

    private QuestaoDAO questaoDAO;

    public QuestaoService() {
        questaoDAO = new QuestaoDAO();
    }

    // listar todas as questoes
    public List<Questao> listarQuestoes() throws Exception {
        return questaoDAO.listarTodas();
    }

    // buscar questao pelo codigo
    public Questao buscarQuestaoPorCodigo(int codigo) throws Exception {

        if (codigo <= 0) {
            throw new Exception("Codigo invalido.");
        }

        return questaoDAO.buscarPorCodigo(codigo);
    }

    // listar questoes de um tema
    public List<Questao> listarQuestoesPorTema(String sigla) throws Exception {

        if (sigla == null || sigla.isBlank()) {
            throw new Exception("Sigla invalida.");
        }

        return questaoDAO.listarPorTema(sigla);
    }
}
