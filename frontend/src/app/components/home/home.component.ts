import { Component, OnInit } from '@angular/core';
import { MediaService } from '../../services/media.service';
import { Media, MediaStatus, MediaType } from '../../models/media';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  totalBooks = 0;
  totalMovies = 0;
  totalTvShows = 0;
  totalVideoGames = 0;

  completedBooksCount: number | null = null;
  completedMoviesCount: number | null = null;
  completedTvShowsCount: number | null = null;
  completedVideoGamesCount: number | null = null;

  currentYear = new Date().getFullYear();

  constructor(private mediaService: MediaService) {}

  ngOnInit(): void {
    this.loadTotalCounts();
    this.loadCompletedCounts();
  }

  loadTotalCounts(): void {
    this.mediaService.getMediaByType(MediaType.BOOK).subscribe({
      next: (books: Media[]) =>
        (this.totalBooks = books.filter(
          (b) => b.status === MediaStatus.COMPLETED,
        ).length),
      error: (err: unknown) => console.error('Error fetching books:', err),
    });

    this.mediaService.getMediaByType(MediaType.MOVIE).subscribe({
      next: (movies: Media[]) =>
        (this.totalMovies = movies.filter(
          (m) => m.status === MediaStatus.COMPLETED,
        ).length),
      error: (err: unknown) => console.error('Error fetching movies:', err),
    });

    this.mediaService.getMediaByType(MediaType.TV).subscribe({
      next: (shows: Media[]) =>
        (this.totalTvShows = shows.filter(
          (s) => s.status === MediaStatus.COMPLETED,
        ).length),
      error: (err: unknown) => console.error('Error fetching TV shows:', err),
    });

    this.mediaService.getMediaByType(MediaType.GAME).subscribe({
      next: (games: Media[]) =>
        (this.totalVideoGames = games.filter(
          (g) => g.status === MediaStatus.COMPLETED,
        ).length),
      error: (err: unknown) =>
        console.error('Error fetching video games:', err),
    });
  }

  loadCompletedCounts(): void {
    this.mediaService.getMediaByType(MediaType.BOOK).subscribe({
      next: (books: Media[]) =>
        (this.completedBooksCount = this.countCompletedByYear(books, this.currentYear)),
      error: (err: unknown) =>
        console.error('Error fetching completed books:', err),
    });

    this.mediaService.getMediaByType(MediaType.MOVIE).subscribe({
      next: (movies: Media[]) =>
        (this.completedMoviesCount = this.countCompletedByYear(movies, this.currentYear)),
      error: (err: unknown) =>
        console.error('Error fetching completed movies:', err),
    });

    this.mediaService.getMediaByType(MediaType.TV).subscribe({
      next: (shows: Media[]) =>
        (this.completedTvShowsCount = this.countCompletedByYear(shows, this.currentYear)),
      error: (err: unknown) =>
        console.error('Error fetching completed TV shows:', err),
    });

    this.mediaService.getMediaByType(MediaType.GAME).subscribe({
      next: (games: Media[]) =>
        (this.completedVideoGamesCount = this.countCompletedByYear(games, this.currentYear)),
      error: (err: unknown) =>
        console.error('Error fetching completed video games:', err),
    });
  }

  private countCompletedByYear(list: Media[], year: number): number {
    return list.filter((m) => {
      if (m.status !== MediaStatus.COMPLETED || !m.completedAt) {
        return false;
      }

      const completedYear = Number(String(m.completedAt).substring(0, 4));
      return completedYear === year;
    }).length;
  }
}
