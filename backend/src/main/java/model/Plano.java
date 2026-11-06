package model;

public class Plano {
	private int id;
	private String nome;
	private int limiteUsoIaMensal;
	
	//construtores
	public Plano() {
		
	}
	public Plano(int id, String nome, int limiteUsoDeIaMensal) {
		this.id = id;
		this.nome = nome;
		this.limiteUsoIaMensal = limiteUsoDeIaMensal;
	}
	//getters e setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public int getLimiteUsoIaMensal() {
		return limiteUsoIaMensal;
	}
	public void setLimiteUsoIaMensal(int limiteUsoDeIaMensal) {
		this.limiteUsoIaMensal = limiteUsoDeIaMensal;
	}
}
