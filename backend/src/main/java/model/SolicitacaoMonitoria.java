package model;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class SolicitacaoMonitoria {
	private int id;
	private int estudanteId;
	private int monitorId;
	private String status;
	private LocalDateTime dataHora;
	private BigDecimal valorCobrado;
	private BigDecimal comissaoPlataforma;
	
	//construtores
	public SolicitacaoMonitoria() {
		
	}
	public SolicitacaoMonitoria(int id, int estudanteId, int monitorId, String status, LocalDateTime dataHora,
			BigDecimal valorCobrado, BigDecimal comissaoPlataforma) {
		this.id = id;
		this.estudanteId = estudanteId;
		this.monitorId = monitorId;
		this.status = status;
		this.dataHora = dataHora;
		this.valorCobrado = valorCobrado;
		this.comissaoPlataforma = comissaoPlataforma;
	}
	
	//getters e setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getEstudanteId() {
		return estudanteId;
	}
	public void setEstudanteId(int estudanteId) {
		this.estudanteId = estudanteId;
	}
	public int getMonitorId() {
		return monitorId;
	}
	public void setMonitorId(int monitorId) {
		this.monitorId = monitorId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public LocalDateTime getDataHora() {
		return dataHora;
	}
	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
	}
	public BigDecimal getValorCobrado() {
		return valorCobrado;
	}
	public void setValorCobrado(BigDecimal valorCobrado) {
		this.valorCobrado = valorCobrado;
	}
	public BigDecimal getComissaoPlataforma() {
		return comissaoPlataforma;
	}
	public void setComissaoPlataforma(BigDecimal comissaoPlataforma) {
		this.comissaoPlataforma = comissaoPlataforma;
	}
	
}
