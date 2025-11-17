import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { MediaStatus, MediaType, CreateMediaRequest, Media } from '../../models/media';
import { MediaService } from '../../services/media.service';

@Component({
  selector: 'app-upload-media',
  templateUrl: './upload-media.component.html',
  styleUrls: ['./upload-media.component.scss'],
})
export class UploadMediaComponent implements OnInit {

  private readonly listRouteMap: Record<MediaType, string> = {
    [MediaType.BOOK]:  'books',
    [MediaType.MOVIE]: 'movies',
    [MediaType.TV]:    'tv',
    [MediaType.GAME]:  'games',
  };

  mediaType!: MediaType;
  initialStatus!: MediaStatus;

  readonly MediaStatusEnum = MediaStatus;
  readonly MediaTypeEnum = MediaType;

  readonly mediaTypeOptions = [
    { value: MediaType.BOOK, label: 'Book' },
    { value: MediaType.MOVIE, label: 'Movie' },
    { value: MediaType.TV, label: 'TV Show' },
    { value: MediaType.GAME, label: 'Video Game' },
  ];

  item: Partial<Media> = {
    name: '',
    imageUrl: '',
    startedAt: null,
    completedAt: null,
    status: MediaStatus.COMPLETED,
    type: undefined,
  };

  includeImage = false;

  constructor(
    private mediaService: MediaService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    // First try to get state from router navigation
    let mediaTypeFromState = this.router.getCurrentNavigation()?.extras.state?.['mediaType'] as MediaType | undefined;
    let statusFromState = this.router.getCurrentNavigation()?.extras.state?.['status'] as MediaStatus | undefined;

    // If getCurrentNavigation() returns null, fall back to history.state
    if (!mediaTypeFromState || !statusFromState) {
      mediaTypeFromState = history.state?.['mediaType'] as MediaType | undefined;
      statusFromState = history.state?.['status'] as MediaStatus | undefined;
    }

    this.mediaType    = mediaTypeFromState ?? MediaType.BOOK;
    this.initialStatus = statusFromState ?? MediaStatus.COMPLETED;

    this.item.type   = this.mediaType;
    this.item.status = this.initialStatus;
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

  onMediaTypeChange(newType: MediaType): void {
    this.mediaType = newType;
    this.item.type = newType;
  }

  onSubmit(): void {
    const name = this.item.name?.trim() ?? '';
    if (!name) {
      alert('Please enter a title/name.');
      return;
    }

    const status = this.item.status ?? MediaStatus.COMPLETED;

    const completedAt =
      status === MediaStatus.COMPLETED && this.item.completedAt
        ? this.item.completedAt
        : null;

    const payload: CreateMediaRequest = {
      name,
      imageUrl: this.includeImage && this.item.imageUrl
        ? this.item.imageUrl.trim()
        : null,
      type: this.mediaType,
      status,
      startedAt: null,
      completedAt,
    };

    this.mediaService.createMedia(payload).subscribe({
      next: () => {
        const segment = this.listRouteMap[this.mediaType];

        let redirectPath: string;
        switch (status) {
          case MediaStatus.BACKLOG:
            redirectPath = `/${segment}/backlog`;
            break;
          case MediaStatus.IN_PROGRESS:
            redirectPath = `/${segment}/in-progress`;
            break;
          default:
            redirectPath = `/${segment}`;
        }

        this.router.navigate([redirectPath]);
      },
      error: (err: unknown) => {
        console.error('Error adding item:', err);
        alert('Could not save media item.');
      }
    });
  }

  onCancel(): void {
    const segment = this.listRouteMap[this.mediaType];

    let targetPath: string;
    switch (this.initialStatus) {
      case MediaStatus.BACKLOG:
        targetPath = `/${segment}/backlog`;
        break;
      case MediaStatus.IN_PROGRESS:
        targetPath = `/${segment}/in-progress`;
        break;
      default:
        targetPath = `/${segment}`;
    }

    this.router.navigate([targetPath]);
  }
}
