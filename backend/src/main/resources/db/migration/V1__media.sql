CREATE TABLE media (
  id            BINARY(16)     NOT NULL,
  name          VARCHAR(255)   NOT NULL,
  image_url     VARCHAR(2048)  NULL,
  type          VARCHAR(16)    NOT NULL,   -- BOOK | GAME | TV | MOVIE
  status        VARCHAR(16)    NOT NULL,   -- BACKLOG | IN_PROGRESS | COMPLETED
  started_at    DATE           NULL,
  completed_at  DATE           NULL,
  created_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT pk_media PRIMARY KEY (id)
);

CREATE INDEX ix_media_type_status ON media(type, status);
CREATE INDEX ix_media_completed   ON media(completed_at);