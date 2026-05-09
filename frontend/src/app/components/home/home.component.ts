import { Component, OnInit } from '@angular/core';
import { MediaService } from '../../services/media.service';
import { Media, MediaStatus, MediaType } from '../../models/media';

type MediaCountMap = Record<MediaType, number | null>;

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  private createEmptyCounts(initialValue: number | null): MediaCountMap {
    return {
      [MediaType.BOOK]: initialValue,
      [MediaType.MOVIE]: initialValue,
      [MediaType.TV]: initialValue,
      [MediaType.GAME]: initialValue,
    };
  }

  totalCounts: MediaCountMap =
    this.createEmptyCounts(0);

  requestedYearCounts: MediaCountMap =
    this.createEmptyCounts(null);

  MediaType = MediaType;

  requestedYear = new Date().getFullYear();

  availableYears = [2026, 2025, 2024, 2023, 2022];

  constructor(private mediaService: MediaService) {}

  ngOnInit(): void {
    this.loadTotalCounts();
    this.loadCountsForYear(this.requestedYear, this.requestedYearCounts);
  }

  loadTotalCounts(): void {
    Object.values(MediaType).forEach((type) => {
      this.mediaService.getMediaByType(type).subscribe({
        next: (media: Media[]) => {
          this.totalCounts[type] = media.filter(
            (m) => m.status === MediaStatus.COMPLETED,
          ).length;
        },
        error: (err) => console.error(`Error fetching ${type}:`, err),
      });
    });
  }

  loadCountsForYear(
    year: number,
    targetCounts: Record<MediaType, number | null>,
  ): void {
    Object.values(MediaType).forEach((type) => {
      targetCounts[type] = null;

      this.mediaService.getMediaByTypeAndYear(type, year).subscribe({
        next: (media: Media[]) => {
          targetCounts[type] = media.length;
        },
        error: (err) => console.error(`Error fetching ${type} for ${year}:`, err),
      });
    });
  }

  onRequestedYearChange(): void {
    this.loadCountsForYear(this.requestedYear, this.requestedYearCounts);
  }
}
