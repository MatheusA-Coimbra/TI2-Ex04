package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Tema;

public class TemaDAO {

    public int inserir(Tema tema) throws SQLException {

        String sql = """
            INSERT INTO tema (nome_tema, area_conhecimento)
            VALUES (?, ?)
            RETURNING id
            """;

        try (Connection conexao = ConexaoBD.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, tema.getNomeTema());
            comando.setString(2, tema.getAreaConhecimento());

            try (ResultSet resultado = comando.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getInt("id");
                }
            }
        }

        throw new SQLException("Não foi possível inserir o tema.");
    }

    public List<Tema> listar() throws SQLException {

        String sql = "SELECT * FROM tema";

        List<Tema> temas = new ArrayList<>();

        try (Connection conexao = ConexaoBD.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {

                Tema tema = new Tema();

                tema.setId(resultado.getInt("id"));
                tema.setNomeTema(resultado.getString("nome_tema"));
                tema.setAreaConhecimento(resultado.getString("area_conhecimento"));

                temas.add(tema);
            }
        }

        return temas;
    }

    public Tema buscarPorId(int id) throws SQLException {

    String sql = "SELECT * FROM tema WHERE id = ?";

    try (Connection conexao = ConexaoBD.abrirConexao();
         PreparedStatement comando = conexao.prepareStatement(sql)) {

        comando.setInt(1, id);

        try (ResultSet resultado = comando.executeQuery()) {

            if (resultado.next()) {

                Tema tema = new Tema();

                tema.setId(resultado.getInt("id"));
                tema.setNomeTema(resultado.getString("nome_tema"));
                tema.setAreaConhecimento(resultado.getString("area_conhecimento"));

                return tema;
            }
        }
    }

    return null;
    }

    public boolean atualizar(Tema tema) throws SQLException {

    String sql = """
        UPDATE tema
        SET nome_tema = ?, area_conhecimento = ?
        WHERE id = ?
        """;

    try (Connection conexao = ConexaoBD.abrirConexao();
         PreparedStatement comando = conexao.prepareStatement(sql)) {

        comando.setString(1, tema.getNomeTema());
        comando.setString(2, tema.getAreaConhecimento());
        comando.setInt(3, tema.getId());

        int linhasAfetadas = comando.executeUpdate();

        return linhasAfetadas > 0;
    }
  }

  public boolean remover(int id) throws SQLException {
      String sql = "DELETE FROM tema WHERE id = ?";

      try (Connection conexao = ConexaoBD.abrirConexao();
            PreparedStatement comando = conexao.prepareStatement(sql)) {

                comando.setInt(1, id);

                int linhasAfetadas = comando.executeUpdate();

                return linhasAfetadas > 0;
            }
        }
} 