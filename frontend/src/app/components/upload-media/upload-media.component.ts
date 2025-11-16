import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import {MediaStatus, MediaType, CreateMediaRequest, Media} from '../../models/media';
import { MediaService } from '../../services/media.service';

@Component({
  selector: 'app-upload-media',
  templateUrl: './upload-media.component.html',
  styleUrls: ['./upload-media.component.scss'],
})
export class UploadMediaComponent implements OnInit {

  private readonly mediaRouteMap: Record<MediaType, string> = {
    [MediaType.BOOK]: 'books',
    [MediaType.MOVIE]: 'movies',
    [MediaType.TV]:   'tv',
    [MediaType.GAME]: 'games',
  };

  mediaType!: MediaType;

  status: MediaStatus = MediaStatus.COMPLETED;

  readonly MediaStatusEnum = MediaStatus;

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
    const typeFromRoute = this.route.snapshot.data['mediaType'] as MediaType | undefined;
    if (!typeFromRoute) {
      throw new Error('Invalid or missing media type');
    }

    this.mediaType = typeFromRoute;

    const nav = this.router.getCurrentNavigation();
    const statusFromState = nav?.extras.state?.['status'] as MediaStatus | undefined;

    const statusFromQueryStr = this.route.snapshot.queryParamMap.get('status');
    let statusFromQuery: MediaStatus | undefined;

    if (statusFromQueryStr === 'COMPLETED') {
      statusFromQuery = MediaStatus.COMPLETED;
    } else if (statusFromQueryStr === 'IN_PROGRESS') {
      statusFromQuery = MediaStatus.IN_PROGRESS;
    } else if (statusFromQueryStr === 'BACKLOG') {
      statusFromQuery = MediaStatus.BACKLOG;
    }

    const initialStatus =
      statusFromState ??
      statusFromQuery ??
      MediaStatus.COMPLETED;

    this.item.status = initialStatus;
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
