package dao;

import model.Interacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class InteracaoDAO {

    // INSERIR
    public int inserir(Interacao interacao) throws SQLException {

        String sql = """
            INSERT INTO interacao
            (estudante_id, questao_id, alternativa_escolhida,
             correta, data_hora)
            VALUES (?, ?, ?, ?, COALESCE(?, CURRENT_TIMESTAMP))
            RETURNING id
            """;

        try (Connection conexao = ConexaoBD.abrirConexao();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setInt(1, interacao.getEstudanteId());
            pstmt.setInt(2, interacao.getQuestaoId());
            pstmt.setString(3, interacao.getAlternativaEscolhida());
            pstmt.setBoolean(4, interacao.isCorreta());

            if (interacao.getDataHora() != null) {
                pstmt.setTimestamp(
                    5, Timestamp.valueOf(interacao.getDataHora())
                );
            } else {
                pstmt.setTimestamp(5, null);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    interacao.setId(id);
                    return id;
                }
            }
        }

        throw new SQLException("Não foi possível inserir a interação.");
    }

    // LISTAR
    public List<Interacao> listar() throws SQLException {

        String sql = "SELECT * FROM interacao ORDER BY id";

        List<Interacao> interacoes = new ArrayList<>();

        try (Connection conexao = ConexaoBD.abrirConexao();
             PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Interacao interacao = new Interacao();

                interacao.setId(rs.getInt("id"));
                interacao.setEstudanteId(rs.getInt("estudante_id"));
                interacao.setQuestaoId(rs.getInt("questao_id"));
                interacao.setAlternativaEscolhida(
                    rs.getString("alternativa_escolhida")
                );
                interacao.setCorreta(rs.getBoolean("correta"));

                Timestamp dataHora = rs.getTimestamp("data_hora");

                if (dataHora != null) {
                    interacao.setDataHora(dataHora.toLocalDateTime());
                }

                interacoes.add(interacao);
            }
        }

        return interacoes;
    }

    // BUSCAR POR ID
    public Interacao buscarPorId(int id) throws SQLException {

        String sql = "SELECT * FROM interacao WHERE id = ?";

        try (Connection conexao = ConexaoBD.abrirConexao();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Interacao interacao = new Interacao();

                    interacao.setId(rs.getInt("id"));
                    interacao.setEstudanteId(rs.getInt("estudante_id"));
                    interacao.setQuestaoId(rs.getInt("questao_id"));
                    interacao.setAlternativaEscolhida(
                        rs.getString("alternativa_escolhida")
                    );
                    interacao.setCorreta(rs.getBoolean("correta"));

                    Timestamp dataHora = rs.getTimestamp("data_hora");

                    if (dataHora != null) {
                        interacao.setDataHora(dataHora.toLocalDateTime());
                    }

                    return interacao;
                }
            }
        }

        return null;
    }

    // ATUALIZAR
    public boolean atualizar(Interacao interacao) throws SQLException {

        String sql = """
            UPDATE interacao
            SET estudante_id = ?,
                questao_id = ?,
                alternativa_escolhida = ?,
                correta = ?,
                data_hora = COALESCE(?, data_hora)
            WHERE id = ?
            """;

        try (Connection conexao = ConexaoBD.abrirConexao();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setInt(1, interacao.getEstudanteId());
            pstmt.setInt(2, interacao.getQuestaoId());
            pstmt.setString(3, interacao.getAlternativaEscolhida());
            pstmt.setBoolean(4, interacao.isCorreta());

            if (interacao.getDataHora() != null) {
                pstmt.setTimestamp(
                    5, Timestamp.valueOf(interacao.getDataHora())
                );
            } else {
                pstmt.setTimestamp(5, null);
            }

            pstmt.setInt(6, interacao.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    // REMOVER
    public boolean remover(int id) throws SQLException {

        String sql = "DELETE FROM interacao WHERE id = ?";

        try (Connection conexao = ConexaoBD.abrirConexao();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        }
    }
}