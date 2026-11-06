package service;

import dao.InteracaoDAO;
import model.Interacao;
import dao.QuestaoDAO;
import model.Questao;

import java.time.LocalDateTime;
import java.sql.SQLException;
import java.util.List;


public class InteracaoService {

	private final InteracaoDAO interacaoDAO;
	private final QuestaoDAO questaoDAO;

	public InteracaoService() {
	    this.interacaoDAO = new InteracaoDAO();
	    this.questaoDAO = new QuestaoDAO();
	}

    // VALIDAÇÃO DOS DADOS
    private void validarDadosInteracao(Interacao interacao) {

        if (interacao == null) {
            throw new IllegalArgumentException(
                "A interação não pode ser nula."
            );
        }

        if (interacao.getEstudanteId() <= 0) {
            throw new IllegalArgumentException(
                "O ID do estudante deve ser positivo."
            );
        }

        if (interacao.getQuestaoId() <= 0) {
            throw new IllegalArgumentException(
                "O ID da questão deve ser positivo."
            );
        }

        if (interacao.getAlternativaEscolhida() == null ||
            interacao.getAlternativaEscolhida().isBlank()) {

            throw new IllegalArgumentException(
                "A alternativa escolhida é obrigatória."
            );
        }

        if (interacao.getAlternativaEscolhida().length() > 5) {
            throw new IllegalArgumentException(
                "A alternativa escolhida não pode ultrapassar 5 caracteres."
            );
        }
    }

    // INSERIR
    public int inserir(Interacao interacao) throws SQLException {

        validarDadosInteracao(interacao);

        return interacaoDAO.inserir(interacao);
    }

    // LISTAR
    public List<Interacao> listar() throws SQLException {

        return interacaoDAO.listar();
    }

    // BUSCAR POR ID
    public Interacao buscarPorId(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                "O ID deve ser positivo."
            );
        }

        return interacaoDAO.buscarPorId(id);
    }

    // ATUALIZAR
    public boolean atualizar(Interacao interacao) throws SQLException {

        validarDadosInteracao(interacao);

        if (interacao.getId() <= 0) {
            throw new IllegalArgumentException(
                "O ID da interação deve ser positivo."
            );
        }

        return interacaoDAO.atualizar(interacao);
    }

    // REMOVER
    public boolean remover(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                "O ID deve ser positivo."
            );
        }

        return interacaoDAO.remover(id);
    }
    
 // REGISTRAR RESPOSTA DO ESTUDANTE
    public boolean registrarResposta(int estudanteId, int questaoId, String alternativaEscolhida) throws SQLException {

        if (estudanteId <= 0 || questaoId <= 0) {
            throw new IllegalArgumentException(
                "ID do estudante ou da questão inválido."
            );
        }

        if (alternativaEscolhida == null || alternativaEscolhida.isBlank() || alternativaEscolhida.trim().length() > 5) {
            throw new IllegalArgumentException(
                "Alternativa escolhida inválida."
            );
        }

        // Buscar a questão no banco
        Questao questao = questaoDAO.buscarPorId(questaoId);
        if (questao == null) {
            throw new IllegalArgumentException(
                "Questão não encontrada."
            );
        }

        String gabarito = questao.getGabarito();
        if (gabarito == null || gabarito.isBlank()) {
            throw new IllegalStateException(
                "Questão sem gabarito cadastrado."
            );
        }

        // Comparar a resposta com o gabarito
        String resposta = alternativaEscolhida.trim();
        boolean correta = resposta.equalsIgnoreCase(
            gabarito.trim()
        );

        Interacao interacao = new Interacao();
        interacao.setEstudanteId(estudanteId);
        interacao.setQuestaoId(questaoId);
        interacao.setAlternativaEscolhida(resposta);
        interacao.setCorreta(correta);
        interacao.setDataHora(LocalDateTime.now());

        inserir(interacao);

        // Retornar se o estudante acertou
        return correta;
    }
}