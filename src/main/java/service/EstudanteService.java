package service;

import dao.EstudanteDAO;
import model.Estudante;

import java.security.SecureRandom;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class EstudanteService {

    private EstudanteDAO estudanteDAO;

    public EstudanteService() {
        estudanteDAO = new EstudanteDAO();
    }

    // 1. cadastrar estudante
    public void cadastrarEstudante(Estudante estudante) throws Exception {

        validarDados(estudante);

        if (estudanteDAO.existeEmail(estudante.getEmail())) {
            throw new Exception("E-mail ja cadastrado.");
        }

        String hash = gerarHash(estudante.getSenha());
        estudante.setSenha(hash);

        estudanteDAO.salvar(estudante);
    }

    // 2. buscar estudante pelo id
    public Estudante buscarEstudantePorId(int id) throws Exception {

        if (id <= 0) {
            throw new Exception("ID invalido.");
        }

        Estudante estudante = estudanteDAO.buscarPorId(id);

        if (estudante == null) {
            throw new Exception("Estudante nao encontrado.");
        }

        estudante.setSenha(null);
        return estudante;
    }

    // 3. atualizar estudante
    public void atualizarEstudante(Estudante estudante) throws Exception {

        if (estudante == null || estudante.getId() <= 0) {
            throw new Exception("Estudante invalido.");
        }

        if (estudante.getNome() == null ||
            estudante.getNome().isBlank()) {
            throw new Exception("Nome obrigatorio.");
        }

        if (estudante.getEmail() == null ||
            !estudante.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new Exception("E-mail invalido.");
        }

        Estudante existente = estudanteDAO.buscarPorId(estudante.getId());

        if (existente == null) {
            throw new Exception("Estudante nao encontrado.");
        }

        if (!existente.getEmail().equalsIgnoreCase(estudante.getEmail())
            && estudanteDAO.existeEmail(estudante.getEmail())) {
            throw new Exception("E-mail ja cadastrado.");
        }

        estudante.setSenha(existente.getSenha());
        estudanteDAO.atualizar(estudante);
    }

    // 4. autenticar estudante
    public Estudante autenticarEstudante(String email, String senha)
            throws Exception {

        if (email == null || senha == null) {
            throw new Exception("E-mail ou senha invalidos.");
        }

        Estudante estudante = estudanteDAO.buscarPorEmail(email);

        if (estudante == null ||
            !verificarSenha(senha, estudante.getSenha())) {
            throw new Exception("E-mail ou senha incorretos.");
        }

        estudante.setSenha(null);
        return estudante;
    }

    // 5. validar dados do cadastro
    private void validarDados(Estudante estudante) throws Exception {

        if (estudante == null) {
            throw new Exception("Estudante invalido.");
        }

        if (estudante.getNome() == null ||
            estudante.getNome().isBlank()) {
            throw new Exception("Nome obrigatorio.");
        }

        if (estudante.getEmail() == null ||
            !estudante.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new Exception("E-mail invalido.");
        }

        if (estudante.getSenha() == null ||
            estudante.getSenha().length() < 8) {
            throw new Exception("A senha deve ter pelo menos 8 caracteres.");
        }
    }

    // 6. gerar hash da senha
    private String gerarHash(String senha) throws Exception {

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        PBEKeySpec dados = new PBEKeySpec(
            senha.toCharArray(), salt, 600000, 256
        );

        try {
            SecretKeyFactory fabrica =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = fabrica.generateSecret(dados).getEncoded();

            return Base64.getEncoder().encodeToString(salt)
                + ":"
                + Base64.getEncoder().encodeToString(hash);

        } finally {
            dados.clearPassword();
        }
    }

    // 7. verificar senha
    private boolean verificarSenha(String senha, String senhaArmazenada)
            throws Exception {

        if (senhaArmazenada == null) {
            return false;
        }

        String[] partes = senhaArmazenada.split(":");

        if (partes.length != 2) {
            return false;
        }

        byte[] salt = Base64.getDecoder().decode(partes[0]);
        byte[] hashOriginal = Base64.getDecoder().decode(partes[1]);

        PBEKeySpec dados = new PBEKeySpec(
            senha.toCharArray(), salt, 600000, 256
        );

        try {
            SecretKeyFactory fabrica =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hashDigitado =
                fabrica.generateSecret(dados).getEncoded();

            return MessageDigest.isEqual(hashOriginal, hashDigitado);

        } finally {
            dados.clearPassword();
        }
    }
}
