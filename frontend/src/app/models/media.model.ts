export interface Media {
  id: string;
  title: string;
  imageUrl: string | null;

  type: MediaType;
  status: MediaStatus;

  startedDate?: string | null;
  completedDate?: string | null;

  // authorName?: string | null;
  // platform?: string | null;
}
