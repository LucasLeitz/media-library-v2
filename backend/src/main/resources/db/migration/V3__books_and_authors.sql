CREATE TABLE author (
  id   BINARY(16) NOT NULL,
  name VARCHAR(255) NOT NULL,
  CONSTRAINT pk_author PRIMARY KEY (id),
  CONSTRAINT uq_author_name UNIQUE (name)
);

CREATE TABLE book_details (
  media_id       BINARY(16) NOT NULL,
  author_display VARCHAR(512) NULL,
  CONSTRAINT pk_book_details PRIMARY KEY (media_id),
  CONSTRAINT fk_book_details_media FOREIGN KEY (media_id)
    REFERENCES media(id) ON DELETE CASCADE
);

CREATE TABLE book_author (
  media_id  BINARY(16) NOT NULL,
  author_id BINARY(16) NOT NULL,
  CONSTRAINT pk_book_author PRIMARY KEY (media_id, author_id),
  CONSTRAINT fk_book_author_media  FOREIGN KEY (media_id)  REFERENCES media(id)  ON DELETE CASCADE,
  CONSTRAINT fk_book_author_author FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE RESTRICT
);

CREATE INDEX ix_book_author_author ON book_author(author_id);
