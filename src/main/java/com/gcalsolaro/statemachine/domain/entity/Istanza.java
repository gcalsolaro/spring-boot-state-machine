package com.gcalsolaro.statemachine.domain.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.gcalsolaro.statemachine.domain.enums.StatoIstanza;

/**
 * The persistent class for the istanza database table.
 * 
 */
@Entity
public class Istanza implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_istanza")
	private Integer idIstanza;

	@Column(name = "c_istanza")
	private String cIstanza;

	private String info;

	@Column
	@Enumerated(EnumType.STRING)
	private StatoIstanza stato;

	// bi-directional many-to-one association to Utente
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_utente")
	private Utente utente;

	public Istanza() {
	}

	public Integer getIdIstanza() {
		return idIstanza;
	}

	public void setIdIstanza(Integer idIstanza) {
		this.idIstanza = idIstanza;
	}

	public String getcIstanza() {
		return cIstanza;
	}

	public void setcIstanza(String cIstanza) {
		this.cIstanza = cIstanza;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public StatoIstanza getStato() {
		return stato;
	}

	public void setStato(StatoIstanza stato) {
		this.stato = stato;
	}

	public Utente getUtente() {
		return utente;
	}

	public void setUtente(Utente utente) {
		this.utente = utente;
	}

}