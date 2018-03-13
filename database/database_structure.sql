CREATE TABLE IF NOT EXISTS bicycle (
  id CHAR(10) NOT NULL,
  current_locker INT,

  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS locker_set (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  location_longitude INT NOT NULL,
  location_latitude INT NOT NULL,
  capacity INT NOT NULL,

  PRIMARY KEY (id),
  CONSTRAINT UC_name UNIQUE(name),
  CONSTRAINT UC_location UNIQUE(location_longitude,location_latitude),
  CHECK (capacity >= 1)
);

CREATE TABLE IF NOT EXISTS transaction (
  bicycle_id CHAR(10) NOT NULL,
  user_id INT NOT NULL,
  taken_locker INT NOT NULL,
  taken_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  returned_locker INT,
  returned_timestamp DATETIME,

  PRIMARY KEY (bicycle_id,taken_timestamp),
  CHECK (returned_timestamp IS NULL OR returned_timestamp > taken_timestamp),
  CHECK (returned_locker IS NULL = returned_timestamp IS NULL)
);

ALTER TABLE bicycle
ADD FOREIGN KEY (current_locker) REFERENCES locker_set(id);

ALTER TABLE transaction
ADD FOREIGN KEY(bicycle_id) REFERENCES bicycle(id);

ALTER TABLE transaction
ADD FOREIGN KEY(taken_locker) REFERENCES locker_set(id);

ALTER TABLE transaction
ADD FOREIGN KEY(returned_locker) REFERENCES locker_set(id);

