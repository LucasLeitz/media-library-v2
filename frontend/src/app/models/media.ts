// ---------- Enums ----------
export enum MediaStatus {
  BACKLOG = 'BACKLOG',
  COMPLETED = 'COMPLETED',
  IN_PROGRESS = 'IN_PROGRESS',
}

export enum MediaType {
  BOOK = 'BOOK',
  MOVIE = 'MOVIE',
  TV = 'TV',
  GAME = 'GAME',
}

export enum GamePlatform {
  GAMEBOY_ADVANCE = 'GAMEBOY_ADVANCE',
  GAMECUBE = 'GAMECUBE',
  NES = 'NES',
  NINTENDO_3DS = 'NINTENDO_3DS',
  NINTENDO_64 = 'NINTENDO_64',
  NINTENDO_DS = 'NINTENDO_DS',
  NINTENDO_SWITCH = 'NINTENDO_SWITCH',
  NINTENDO_SWITCH_2 = 'NINTENDO_SWITCH_2',
  PC = 'PC',
  PLAYSTATION_1 = 'PLAYSTATION_1',
  PLAYSTATION_2 = 'PLAYSTATION_2',
  PLAYSTATION_3 = 'PLAYSTATION_3',
  PLAYSTATION_4 = 'PLAYSTATION_4',
  PLAYSTATION_5 = 'PLAYSTATION_5',
  SNES = 'SNES',
  OTHER = 'OTHER',
}

// ---------- Detail types ----------

export interface BookDetails {
  mediaId: string;
  author?: string;
}

export interface GameDetails {
  mediaId: string;
  platform: GamePlatform;
}

// ---------- Core Media type ----------
export interface Media {
  id: string;
  name: string;
  imageUrl?: string | null;

  type: MediaType;
  status: MediaStatus;

  startedAt?: string | null;
  completedAt?: string | null;

  createdAt: string;
  updatedAt: string;

  bookDetails?: BookDetails | null;
  gameDetails?: GameDetails | null;
}

// ---------- Request Types ----------

export interface CreateMediaRequest {
  name: string;
  type: MediaType;
  status: MediaStatus;
  imageUrl?: string | null;
  startedAt?: string | null;
  completedAt?: string | null;
}

export interface UpdateMediaRequest {
  name?: string;
  type?: MediaType;
  status?: MediaStatus;
  imageUrl?: string | null;
  startedAt?: string | null;
  completedAt?: string | null;
}
