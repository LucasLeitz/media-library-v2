import { Component } from '@angular/core';
import { Media, MediaStatus, MediaType } from '../../models/media';
import { MediaService } from '../../services/media.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  clicks = 0;
  media?: Media;
  loading = false;
  error?: string;

  constructor(private mediaService: MediaService) {}

  onButtonClick(): void {
    this.clicks++;
  }

  generateMedia(): void {
    this.loading = true;
    this.error = undefined;
    this.media = undefined;

    const request = {
      name: 'Test Media ' + new Date().toISOString(),
      type: MediaType.BOOK,
      status: MediaStatus.BACKLOG,
      imageUrl: null,
      startedAt: null,
      completedAt: null
    };

    this.mediaService.createMedia(request).subscribe({
      next: (created) => {
        this.media = created;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to create media', err);
        this.error = err?.error?.message || 'Failed to create media';
        this.loading = false;
      }
    });
  }
}
