/*
CREATE TABLE istanza (
	id_istanza number NOT NULL,
	c_istanza varchar(45) NOT NULL,
	dt_domanda date NULL,
	dt_ora_ins timestamp NOT NULL,
	userid_ins varchar(64) NOT NULL,
	dt_ora_mod timestamp NULL,
	userid_mod varchar(64) NULL,
	CONSTRAINT istanza_c_istanza_key UNIQUE (c_istanza),
	CONSTRAINT istanza_pkey PRIMARY KEY (id_istanza)
);
*/

/*DROP TABLE IF EXISTS utente, istanza;*/

CREATE TABLE utente (
	id_utente number NOT NULL,
	c_fiscale varchar(16) NOT NULL,
	nome varchar(64) NULL,
	cognome varchar(64) NULL,
	dt_nascita date NULL,
	stato varchar(20) NOT NULL,
	email varchar(255) NULL,
	CONSTRAINT utente_c_fiscale_key UNIQUE (c_fiscale),
	CONSTRAINT utente_pkey PRIMARY KEY (id_utente)
);

CREATE TABLE istanza (
	id_istanza number NOT NULL,
	c_istanza varchar(45) NOT NULL,
	fk_utente int4,
	info varchar(255) NULL,
	stato varchar(20) NOT NULL,
	CONSTRAINT istanza_c_istanza_key UNIQUE (c_istanza),
	CONSTRAINT istanza_pkey PRIMARY KEY (id_istanza)
);

ALTER TABLE istanza ADD CONSTRAINT fk_istanza_utente FOREIGN KEY (fk_utente) REFERENCES utente(id_utente);