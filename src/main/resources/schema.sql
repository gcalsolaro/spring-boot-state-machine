/*DROP TABLE IF EXISTS user, instance;*/

CREATE TABLE user (
	id_user number NOT NULL,
	c_fiscal_code varchar(16) NOT NULL,
	name varchar(64) NULL,
	surname varchar(64) NULL,
	dt_born date NULL,
	state varchar(20) NOT NULL,
	email varchar(255) NULL,
	CONSTRAINT user_c_fiscal_code_key UNIQUE (c_fiscal_code),
	CONSTRAINT user_pkey PRIMARY KEY (id_user)
);

CREATE TABLE instance (
	id_instance number NOT NULL,
	c_instance varchar(45) NOT NULL,
	fk_user int4,
	info varchar(255) NULL,
	state varchar(20) NOT NULL,
	CONSTRAINT instance_c_instance_key UNIQUE (c_instance),
	CONSTRAINT instance_pkey PRIMARY KEY (id_instance)
);

ALTER TABLE instance ADD CONSTRAINT fk_instance_user FOREIGN KEY (fk_user) REFERENCES user(id_user);