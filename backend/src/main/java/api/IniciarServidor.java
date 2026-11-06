package api;

public class IniciarServidor {

    public static void main(String[] args) {

        try {
            ServidorHTTP servidor = new ServidorHTTP();
            servidor.iniciar();

        } catch (Exception e) {
            System.err.println("Erro ao iniciar o servidor:");
            e.printStackTrace();
        }
    }
}