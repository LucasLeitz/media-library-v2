import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { MediaStatus, MediaType, CreateMediaRequest } from '../../models/media';
import { MediaService } from '../../services/media.service';

@Component({
  selector: 'app-upload-media',
  templateUrl: './upload-media.component.html',
  styleUrls: ['./upload-media.component.scss'],
})
export class UploadMediaComponent implements OnInit {

  // Used for redirect after submit/cancel
  private readonly mediaRouteMap: Record<MediaType, string> = {
    [MediaType.BOOK]: 'books',
    [MediaType.MOVIE]: 'movies',
    [MediaType.TV]:   'tv',
    [MediaType.GAME]: 'games',
  };

  mediaType!: MediaType;

  status: MediaStatus = MediaStatus.COMPLETED;

  readonly MediaStatusEnum = MediaStatus;

  item = {
    name: '',
    imageUrl: '',
    completedAt: '',
  };

  includeImage = false;

  constructor(
    private mediaService: MediaService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    const typeFromRoute = this.route.snapshot.data['mediaType'] as MediaType | undefined;

    if (!typeFromRoute) {
      throw new Error('Invalid or missing media type');
    }

    this.mediaType = typeFromRoute;

    this.status = MediaStatus.COMPLETED;
  }

  get mediaLabel(): string {
    switch (this.mediaType) {
      case MediaType.BOOK:  return 'Book';
      case MediaType.MOVIE: return 'Movie';
      case MediaType.TV:    return 'TV Show';
      case MediaType.GAME:  return 'Video Game';
      default:              return 'Media';
    }
  }

  onSubmit(): void {
    if (!this.item.name.trim()) {
      alert('Please enter a title/name.');
      return;
    }

    const completedAt =
      this.status === MediaStatus.COMPLETED && this.item.completedAt
        ? this.item.completedAt
        : null;

    const payload: CreateMediaRequest = {
      name: this.item.name.trim(),
      imageUrl: this.includeImage && this.item.imageUrl ? this.item.imageUrl.trim() : null,
      type: this.mediaType,
      status: this.status,
      startedAt: null,
      completedAt,
    };

    this.mediaService.createMedia(payload).subscribe({
      next: () => {
        const routeSegment = this.mediaRouteMap[this.mediaType];

        const redirectPath = `/${routeSegment}`;

        this.router.navigate([redirectPath]);
      },
      error: (err: unknown) => {
        console.error('Error adding item:', err);
        alert('Could not save media item.');
      }
    });
  }

  onCancel(): void {
    const routeSegment = this.mediaRouteMap[this.mediaType];
    const targetRoute = `/${routeSegment}`;
    this.router.navigate([targetRoute]);
  }
}
