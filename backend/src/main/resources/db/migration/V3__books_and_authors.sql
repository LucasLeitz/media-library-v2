CREATE TABLE book_details (
  media_id  BINARY(16)  NOT NULL,
  author    VARCHAR(512) NULL,
  CONSTRAINT pk_book_details PRIMARY KEY (media_id),
  CONSTRAINT fk_book_details_media FOREIGN KEY (media_id)
    REFERENCES media(id) ON DELETE CASCADE
);

CREATE INDEX idx_book_details_author ON book_details(author);