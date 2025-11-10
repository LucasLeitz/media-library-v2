CREATE TABLE platform (
  id   BINARY(16) NOT NULL,
  name VARCHAR(64) NOT NULL,
  CONSTRAINT pk_platform PRIMARY KEY (id),
  CONSTRAINT uq_platform_name UNIQUE (name)
);

CREATE TABLE game_details (
  media_id    BINARY(16) NOT NULL,
  platform_id BINARY(16) NOT NULL,
  CONSTRAINT pk_game_details PRIMARY KEY (media_id),
  CONSTRAINT fk_game_details_media    FOREIGN KEY (media_id)    REFERENCES media(id)    ON DELETE CASCADE,
  CONSTRAINT fk_game_details_platform FOREIGN KEY (platform_id) REFERENCES platform(id) ON DELETE RESTRICT
);

CREATE INDEX ix_game_details_platform ON game_details(platform_id);