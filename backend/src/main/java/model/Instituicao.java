package model;

import java.time.LocalDateTime;

public class Instituicao {
	private int id;
	private String nomeInstituicao;
	private String logoUrl;
	private String corPrimaria;
	//localDateTime é usado para saber o dia e as horas da contratação
	private LocalDateTime dataContratacao;
	
	//construtores
	public Instituicao() {
		
	}
	public Instituicao(int id, String nomeInstituicao, String logoUrl, String corPrimaria,
			LocalDateTime dataContratacao) {
		this.id = id;
		this.nomeInstituicao = nomeInstituicao;
		this.logoUrl = logoUrl;
		this.corPrimaria = corPrimaria;
		this.dataContratacao = dataContratacao;
	}
	
	//getters e setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNomeInstituicao() {
		return nomeInstituicao;
	}
	public void setNomeInstituicao(String nomeInstituicao) {
		this.nomeInstituicao = nomeInstituicao;
	}
	public String getLogoUrl() {
		return logoUrl;
	}
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}
	public String getCorPrimaria() {
		return corPrimaria;
	}
	public void setCorPrimaria(String corPrimaria) {
		this.corPrimaria = corPrimaria;
	}
	public LocalDateTime getDataContratacao() {
		return dataContratacao;
	}
	public void setDataContratacao(LocalDateTime dataContratacao) {
		this.dataContratacao = dataContratacao;
	}
}
