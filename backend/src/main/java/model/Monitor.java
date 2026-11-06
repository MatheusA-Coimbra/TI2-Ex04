package model;

import java.math.BigDecimal;

public class Monitor {
	private int id;
	private String nome;
	private String email;
	private String senhaHash;
	private String areaAtuacao;
	//BigDecimal para ter um numero que se encaixe ao que pede no SQL
	private BigDecimal valorHora;
	
	//construtores
	public Monitor() {
		
	}
	public Monitor(int id, String nome, String email, String senhaHash, String areaAtuacao, BigDecimal valorHora) {
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.senhaHash = senhaHash;
		this.areaAtuacao = areaAtuacao;
		this.valorHora = valorHora;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSenhaHash() {
		return senhaHash;
	}
	public void setSenhaHash(String senhaHash) {
		this.senhaHash = senhaHash;
	}
	public String getAreaAtuacao() {
		return areaAtuacao;
	}
	public void setAreaAtuacao(String areaAtuacao) {
		this.areaAtuacao = areaAtuacao;
	}
	public BigDecimal getValorHora() {
		return valorHora;
	}
	public void setValorHora(BigDecimal valorHora) {
		this.valorHora = valorHora;
	}	
}
