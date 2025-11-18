import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { MediaService } from '../../services/media.service';
import {Media, MediaType, MediaStatus, GamePlatform} from '../../models/media';
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
  selectedPlatform: string = '';
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

        this.selectedPlatform = data.gameDetails?.platform || '';

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

    let completedAt: string | null = null;
    if (statusToSend === MediaStatus.COMPLETED && this.item.completedAt) {
      completedAt = this.item.completedAt;
    } else {
      this.item.completedAt = null;
    }

    const originalImage = this.item.imageUrl ?? '';
    const trimmedNewImage = this.newImageUrl.trim();
    const imageChanged = trimmedNewImage !== originalImage;

    this.isLoading = true;
    this.errorMessage = null;

    this.mediaService.renameMedia(id, trimmedName).subscribe({
      next: () => {
        this.mediaService.setMediaStatus(id, statusToSend, completedAt).subscribe({
          next: () => {
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
    const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');

    if (returnUrl) {
      this.isLoading = false;
      this.router.navigateByUrl(returnUrl);
      return;
    }

    if (!this.item) {
      this.router.navigate(['/home']);
      return;
    }
    const redirectPath = `/${this.mediaRouteMap[this.item.type]}`;
    this.isLoading = false;
    this.router.navigate([redirectPath]);
  }

  onCancel(): void {
    const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');

    if (returnUrl) {
      this.router.navigateByUrl(returnUrl);
      return;
    }

    if (!this.item) {
      this.router.navigate(['/home']);
      return;
    }
    const redirectPath = `/${this.mediaRouteMap[this.item.type]}`;
    this.router.navigate([redirectPath]);
  }

  updatePreview(): void {
    console.log('Preview updated with URL:', this.newImageUrl);
  }

  updateAuthor(author: string) {
    if (!this.item || !author.trim()) return;

    if (!this.item.bookDetails) {
      this.mediaService.createBookDetails(this.item.id, author).subscribe({
        next: () => {
          console.log('Book details created successfully');
          if (this.item) {
            this.item.bookDetails = { mediaId: this.item.id, author };
          }
        },
        error: (error) => {
          console.error('Error creating book details', error);
        }
      });
    } else {
      this.mediaService.updateBookAuthor(this.item.id, author).subscribe({
        next: () => {
          console.log('Author updated successfully');
          if (this.item) {
            this.item.bookDetails = { mediaId: this.item.id, author };
          }
        },
        error: (error) => {
          console.error('Error updating author', error);
        }
      });
    }
  }


  updatePlatform(platform: string) {
    if (!this.item || !platform) return;

    if (!this.item.gameDetails) {
      this.mediaService.createGameDetails(this.item.id, platform).subscribe({
        next: () => {
          console.log('Game details created successfully');
          if (this.item) {
            this.item.gameDetails = { mediaId: this.item.id, platform: platform as GamePlatform };
            this.selectedPlatform = platform;
          }
        },
        error: (error) => {
          console.error('Error creating game details', error);
        }
      });
    } else {
      this.mediaService.updateGamePlatform(this.item.id, platform).subscribe({
        next: () => {
          console.log('Platform updated successfully');
          if (this.item) {
            this.item.gameDetails = { mediaId: this.item.id, platform: platform as GamePlatform };
            this.selectedPlatform = platform;
          }
        },
        error: (error) => {
          console.error('Error updating platform', error);
        }
      });
    }
  }

}
