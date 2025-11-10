CREATE TABLE game_details (
  media_id  BINARY(16) NOT NULL,
  platform  VARCHAR(32) NOT NULL,
  CONSTRAINT pk_game_details PRIMARY KEY (media_id),
  CONSTRAINT fk_game_details_media
    FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE,
  CONSTRAINT chk_game_details_platform CHECK (platform IN (
    'GAMEBOY_ADVANCE',
    'GAMECUBE',
    'NES',
    'NINTENDO_3DS',
    'NINTENDO_64',
    'NINTENDO_DS',
    'NINTENDO_SWITCH',
    'NINTENDO_SWITCH_2',
    'PC',
    'PLAYSTATION_1',
    'PLAYSTATION_2',
    'PLAYSTATION_3',
    'PLAYSTATION_4',
    'PLAYSTATION_5',
    'SNES',
    'OTHER'
  ))
);

CREATE INDEX ix_game_details_platform ON game_details(platform);