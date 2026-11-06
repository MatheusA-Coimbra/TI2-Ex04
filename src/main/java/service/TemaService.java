package service;

import dao.TemaDAO;
import model.Tema;
import java.util.List;

public class TemaService {

    private TemaDAO temaDAO;

    public TemaService() {
        temaDAO = new TemaDAO();
    }

    // listar todos os temas
    public List<Tema> listarTemas() throws Exception {
        return temaDAO.listarTodos();
    }

    // buscar tema pela sigla
    public Tema buscarTemaPorSigla(String sigla) throws Exception {

        if (sigla == null || sigla.isBlank()) {
            throw new Exception("Sigla invalida.");
        }

        return temaDAO.buscarPorSigla(sigla);
    }

    // listar temas de uma area
    public List<Tema> listarTemasPorArea(String area) throws Exception {

        if (area == null || area.isBlank()) {
            throw new Exception("Area invalida.");
        }

        return temaDAO.listarPorArea(area);
    }
}
