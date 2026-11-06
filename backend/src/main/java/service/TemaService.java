package service;

import dao.TemaDAO;
import model.Tema;

import java.sql.SQLException;
import java.util.List;

public class TemaService {

    private final TemaDAO temaDAO;

    public TemaService() {
        this.temaDAO = new TemaDAO();
    }

    public int inserir(Tema tema) throws SQLException {

        if (tema == null) {
            throw new IllegalArgumentException("O tema não pode ser nulo.");
        }

        if (tema.getNomeTema() == null || tema.getNomeTema().isBlank()) {
            throw new IllegalArgumentException("O nome do tema é obrigatório.");
        }

        if (tema.getAreaConhecimento() == null || tema.getAreaConhecimento().isBlank()) {
            throw new IllegalArgumentException("A área de conhecimento é obrigatória.");
        }

        return temaDAO.inserir(tema);
    }

    public List<Tema> listar() throws SQLException {
        return temaDAO.listar();
    }

    public Tema buscarPorId(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException("O ID deve ser positivo.");
        }

        return temaDAO.buscarPorId(id);
    }

    public boolean atualizar(Tema tema) throws SQLException {

        if (tema == null || tema.getId() <= 0) {
            throw new IllegalArgumentException("Tema ou ID inválido.");
        }

        if (tema.getNomeTema() == null || tema.getNomeTema().isBlank()) {
            throw new IllegalArgumentException("O nome do tema é obrigatório.");
        }

        if (tema.getAreaConhecimento() == null || tema.getAreaConhecimento().isBlank()) {
            throw new IllegalArgumentException("A área de conhecimento é obrigatória.");
        }

        return temaDAO.atualizar(tema);
    }

    public boolean remover(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException("O ID deve ser positivo.");
        }

        return temaDAO.remover(id);
    }
}