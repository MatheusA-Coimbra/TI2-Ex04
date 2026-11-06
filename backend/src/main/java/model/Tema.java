package model;

public class Tema {

    private int id;
    private String nomeTema;
    private String areaConhecimento;

    public Tema() {
    }

    public Tema(int id, String nomeTema, String areaConhecimento) {
        this.id = id;
        this.nomeTema = nomeTema;
        this.areaConhecimento = areaConhecimento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeTema() {
        return nomeTema;
    }

    public void setNomeTema(String nomeTema) {
        this.nomeTema = nomeTema;
    }

    public String getAreaConhecimento() {
        return areaConhecimento;
    }

    public void setAreaConhecimento(String areaConhecimento) {
        this.areaConhecimento = areaConhecimento;
    }
    
}
