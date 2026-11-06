package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centraliza a conexão com o banco de dados PostgreSQL.
 */
public class ConexaoBD {

    private static final String URL = "jdbc:postgresql://aluminasistemas2.postgres.database.azure.com:5432/postgres?sslmode=require";


    /**
     * Abre uma conexão com o banco de dados.
     *
     * @return conexão ativa com o PostgreSQL
     * @throws SQLException caso ocorra algum erro na conexão
     */
    public static Connection abrirConexao() throws SQLException {

        String usuario = System.getenv("DB_USER");
        String senha = System.getenv("DB_PASSWORD");

        if (usuario == null || senha == null) {
            throw new SQLException("Variáveis de ambiente DB_USER e DB_PASSWORD não configuradas.");
        }

        return DriverManager.getConnection(URL, usuario, senha);
    }

    /**
     * Fecha uma conexão com o banco de dados.
     *
     * @param conexao conexão que será encerrada
     */
    public static void fecharConexao(Connection conexao) {

        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}