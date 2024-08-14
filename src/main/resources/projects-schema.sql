DROP TABLE IF EXISTS project_category;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS material;
DROP TABLE IF EXISTS project;

CREATE TABLE project (
project_id INT AUTO_INCREMENT NOT NULL,
project_name VARCHAR(128) NOT NULL,
estimated_hours DECIMAL(7,2),
actual_hours DECIMAL(7,2),
difficulty INT,
notes TEXT,
PRIMARY KEY (project_id)
);

CREATE TABLE material (
material_id INT AUTO_INCREMENT NOT NULL,
project_id INT NOT NULL,
material_name VARCHAR(128) NOT NULL,
num_required INT,
cost DECIMAL(7,2),
PRIMARY KEY (material_id),
FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

CREATE TABLE step (
step_id INT AUTO_INCREMENT NOT NULL,
project_id INT NOT NULL,
step_text TEXT NOT NULL,
step_order INT NOT NULL,
PRIMARY KEY (step_id),
FOREIGN KEY (project_id) REFERENCES project (project_id)
);

CREATE TABLE category (
category_id INT AUTO_INCREMENT NOT NULL,
category_name VARCHAR(128) NOT NULL,
PRIMARY KEY (category_id)
);

CREATE TABLE project_category (
project_id INT NOT NULL,
category_id INT NOT NULL,
FOREIGN KEY (project_id) REFERENCES project (project_id),
FOREIGN KEY (category_id) REFERENCES category (category_id),
UNIQUE KEY (project_id, category_id)
);

-- add some data

INSERT INTO project (project_name, estimated_hours, actual_hours, difficulty, notes) VALUES (Entry Way Storage, 16, 18, 3, buy straight boards);
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (5, 'trim', 8, 5.60);
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (5, 'butcher block', 1, 350);
INSERT INTO step (project_id, step_text, step_order) VALUES (5, 'secure trim to wall', 1);
INSERT INTO step (project_id, step_text, step_order) VALUES (5, 'set butcher block on frame', 2);
INSERT INTO category (category_id, category_name) VALUES (2, 'DIY');
INSERT INTO category (category_id, category_name) VALUES (3, 'Custom Design');
INSERT INTO project_category (project_id, category_id) VALUES (5, 2);
INSERT INTO project_category (project_id, category_id) VALUES (5, 3);