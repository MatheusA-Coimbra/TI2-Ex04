package dao;

import model.Questao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QuestaoDAO {

    // INSERIR
    public int inserir(Questao questao) throws SQLException {
        String sql = """
            INSERT INTO questao
            (enunciado, alternativas, gabarito, ano_edicao,
             nivel_dificuldade, frequencia_historica, tema_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;

        try (Connection conexao = ConexaoBD.abrirConexao();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setString(1, questao.getEnunciado());
            pstmt.setString(2, questao.getAlternativas());
            pstmt.setString(3, questao.getGabarito());
            pstmt.setInt(4, questao.getAnoEdicao());
            pstmt.setInt(5, questao.getNivelDificuldade());
            pstmt.setBigDecimal(6, questao.getFrequenciaHistorica());
            pstmt.setInt(7, questao.getTemaId());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    questao.setId(id);
                    return id;
                }
            }
        }

        throw new SQLException("Não foi possível inserir a questão.");
    }

    // LISTAR
    public ArrayList<Questao> listar() throws SQLException {
        String sql = "SELECT * FROM questao ORDER BY id";

        ArrayList<Questao> questoes = new ArrayList<>();

        try (Connection conexao = ConexaoBD.abrirConexao();
             PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Questao questao = new Questao();

                questao.setId(rs.getInt("id"));
                questao.setEnunciado(rs.getString("enunciado"));
                questao.setAlternativas(rs.getString("alternativas"));
                questao.setGabarito(rs.getString("gabarito"));
                questao.setAnoEdicao(rs.getInt("ano_edicao"));
                questao.setNivelDificuldade(rs.getInt("nivel_dificuldade"));
                questao.setFrequenciaHistorica(
                    rs.getBigDecimal("frequencia_historica")
                );
                questao.setTemaId(rs.getInt("tema_id"));

                questoes.add(questao);
            }
        }

        return questoes;
    }

    // BUSCAR POR ID
    public Questao buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM questao WHERE id = ?";

        try (Connection conexao = ConexaoBD.abrirConexao();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Questao questao = new Questao();

                    questao.setId(rs.getInt("id"));
                    questao.setEnunciado(rs.getString("enunciado"));
                    questao.setAlternativas(rs.getString("alternativas"));
                    questao.setGabarito(rs.getString("gabarito"));
                    questao.setAnoEdicao(rs.getInt("ano_edicao"));
                    questao.setNivelDificuldade(
                        rs.getInt("nivel_dificuldade")
                    );
                    questao.setFrequenciaHistorica(
                        rs.getBigDecimal("frequencia_historica")
                    );
                    questao.setTemaId(rs.getInt("tema_id"));

                    return questao;
                }
            }
        }

        return null;
    }

    // ATUALIZAR
    public boolean atualizar(Questao questao) throws SQLException {
        String sql = """
            UPDATE questao
            SET enunciado = ?,
                alternativas = ?,
                gabarito = ?,
                ano_edicao = ?,
                nivel_dificuldade = ?,
                frequencia_historica = ?,
                tema_id = ?
            WHERE id = ?
            """;

        try (Connection conexao = ConexaoBD.abrirConexao();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setString(1, questao.getEnunciado());
            pstmt.setString(2, questao.getAlternativas());
            pstmt.setString(3, questao.getGabarito());
            pstmt.setInt(4, questao.getAnoEdicao());
            pstmt.setInt(5, questao.getNivelDificuldade());
            pstmt.setBigDecimal(6, questao.getFrequenciaHistorica());
            pstmt.setInt(7, questao.getTemaId());
            pstmt.setInt(8, questao.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    // REMOVER
    public boolean remover(int id) throws SQLException {
        String sql = "DELETE FROM questao WHERE id = ?";

        try (Connection conexao = ConexaoBD.abrirConexao();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        }
    }
}