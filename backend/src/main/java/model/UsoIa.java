package model;

import java.time.LocalDateTime;

public class UsoIa {
	private int id;
	//Foreign Key
	private int estudanteId;
	private String tipoRecurso;
	private LocalDateTime dataHora;
		
	//construtores
	public UsoIa() {
		
	}
	public UsoIa(int id, int estudanteId, String tipoRecurso, LocalDateTime dataHora) {
		this.id = id;
		this.estudanteId = estudanteId;
		this.tipoRecurso = tipoRecurso;
		this.dataHora = dataHora;
	}
	
	//getters e setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTipoRecurso() {
		return tipoRecurso;
	}
	public void setTipoRecurso(String tipoRecurso) {
		this.tipoRecurso = tipoRecurso;
	}
	public LocalDateTime getDataHora() {
		return dataHora;
	}
	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
	}
	public int getEstudanteId() {
		return estudanteId;
	}
	public void setEstudanteId(int estudanteId) {
		this.estudanteId = estudanteId;
	}
	
}
