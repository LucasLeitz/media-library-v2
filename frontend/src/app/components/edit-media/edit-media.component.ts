import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { MediaService } from '../../services/media.service';
import { Media, MediaType, MediaStatus } from '../../models/media';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-edit-media',
  templateUrl: './edit-media.component.html',
  styleUrls: ['./edit-media.component.scss'],
})
export class EditMediaComponent implements OnInit {

  item: Media | null = null;

  newImageUrl = '';
  isLoading = true;
  errorMessage: string | null = null;

  readonly MediaStatusEnum = MediaStatus;

  private readonly mediaRouteMap: Record<MediaType, string> = {
    [MediaType.BOOK]: 'books',
    [MediaType.MOVIE]: 'movies',
    [MediaType.TV]:   'tv',
    [MediaType.GAME]: 'games',
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private mediaService: MediaService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.errorMessage = 'No media ID provided.';
      this.isLoading = false;
      return;
    }

    this.mediaService.getMediaById(id).subscribe({
      next: (data: Media) => {
        this.item = data;
        this.newImageUrl = data.imageUrl ?? '';
        this.isLoading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = 'Failed to load media item.';
        console.error('Load error:', err.message);
        this.isLoading = false;
      }
    });
  }

  get mediaLabel(): string {
    if (!this.item) return 'Media';
    switch (this.item.type) {
      case MediaType.BOOK:  return 'Book';
      case MediaType.MOVIE: return 'Movie';
      case MediaType.TV:    return 'TV Show';
      case MediaType.GAME:  return 'Video Game';
      default:              return 'Media';
    }
  }

  onSave(): void {
    if (!this.item) return;

    const id = this.item.id;
    if (!id) {
      this.errorMessage = 'Item is missing an ID.';
      return;
    }

    const trimmedName = this.item.name.trim();
    if (!trimmedName) {
      this.errorMessage = 'Title cannot be empty.';
      return;
    }

    const statusToSend = this.item.status;

    // Normalize completedAt for COMPLETED vs others
    let completedAt: string | null = null;
    if (statusToSend === MediaStatus.COMPLETED && this.item.completedAt) {
      completedAt = this.item.completedAt; // already in YYYY-MM-DD from date input
    } else {
      this.item.completedAt = null;
    }

    // Determine if image actually changed
    const originalImage = this.item.imageUrl ?? '';
    const trimmedNewImage = this.newImageUrl.trim();
    const imageChanged = trimmedNewImage !== originalImage;

    this.isLoading = true;
    this.errorMessage = null;

    // 1) Rename
    this.mediaService.renameMedia(id, trimmedName).subscribe({
      next: () => {
        // 2) Status + completedAt
        this.mediaService.setMediaStatus(id, statusToSend, completedAt).subscribe({
          next: () => {
            // 3) Image (only if changed)
            if (imageChanged) {
              const imageToSend = trimmedNewImage.length > 0 ? trimmedNewImage : null;
              this.mediaService.setMediaImageUrl(id, imageToSend).subscribe({
                next: (updated: Media) => {
                  this.item = updated;
                  this.navigateBack();
                },
                error: (err: HttpErrorResponse) => {
                  this.errorMessage = 'Failed to update image URL.';
                  console.error('Image update error:', err.message);
                  this.isLoading = false;
                }
              });
            } else {
              this.navigateBack();
            }
          },
          error: (err: HttpErrorResponse) => {
            this.errorMessage = 'Failed to update media status.';
            console.error('Status update error:', err.message);
            this.isLoading = false;
          }
        });
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = 'Failed to update media title.';
        console.error('Rename error:', err.message);
        this.isLoading = false;
      }
    });
  }

  private navigateBack(): void {
    if (!this.item) {
      this.router.navigate(['/home']);
      return;
    }
    const redirectPath = `/${this.mediaRouteMap[this.item.type]}`;
    this.isLoading = false;
    this.router.navigate([redirectPath]);
  }

  onCancel(): void {
    if (!this.item) {
      this.router.navigate(['/home']);
      return;
    }
    const redirectPath = `/${this.mediaRouteMap[this.item.type]}`;
    this.router.navigate([redirectPath]);
  }

  updatePreview(): void {
    // Just here if you want to log / debug previews
    console.log('Preview updated with URL:', this.newImageUrl);
  }
}
