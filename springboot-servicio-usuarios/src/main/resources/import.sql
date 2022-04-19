INSERT INTO usuarios (username, password, enabled, nombre, apellido, email) VALUES ('fabian', '$2a$10$q5735hfNHQ8hWg8aJ8r.qO92jbkVLSxqlsiBIRrbVXQhyCYSABamK', true, 'Fabian', 'Bauza', 'fabian.bauza@errepar.com');
INSERT INTO usuarios (username, password, enabled, nombre, apellido, email) VALUES ('admin', '$2a$10$NgCkAkBKzYWSCv32tmsHL.CPh1EzIYFNtp02oehUO4oLRIzhT3po2', true, 'Admin', 'Admin', 'administrador@errepar.com');

INSERT INTO roles (nombre) VALUES ('ROLE_USER');
INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN');

INSERT INTO usuarios_roles (usuario_id, role_id) VALUES (1,1);
INSERT INTO usuarios_roles (usuario_id, role_id) VALUES (2,2);
INSERT INTO usuarios_roles (usuario_id, role_id) VALUES (2,1);
