ALTER TABLE permissions ADD COLUMN resource VARCHAR(255) NOT NULL;
ALTER TABLE permissions ADD COLUMN action VARCHAR(255) NOT NULL;
ALTER TABLE permissions ADD COLUMN expression VARCHAR(255);
ALTER TABLE permissions ADD COLUMN name VARCHAR(255) NOT NULL;
ALTER TABLE permissions DROP COLUMN code;
ALTER TABLE permissions ADD CONSTRAINT unique_permission UNIQUE (resource, action);
