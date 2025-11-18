import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { MediaService } from '../../services/media.service';
import { Media, MediaType } from '../../models/media';
import { HttpErrorResponse } from "@angular/common/http";

@Component({
  selector: 'app-view-media',
  templateUrl: './view-media.component.html',
  styleUrls: ['./view-media.component.scss']
})


export class ViewMediaComponent implements OnInit {

  item: Media | null = null;
  errorMessage: string | null = null;
  isLoading = true;
  protected readonly MediaType = MediaType;

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

  ngOnInit() {
    const id = this.route.snapshot.params['id'];
    if (!id) {
      this.errorMessage = 'No media ID provided.';
      this.isLoading = false;
      return;
    }

    this.mediaService.getMediaById(id).subscribe({
      next: (data: Media) => {
        this.item = data;
        this.isLoading = false;
      },

      error: (err: HttpErrorResponse) => {
        this.errorMessage = 'Failed to load media item.';
        console.error('Load error:', err.message);
        this.isLoading = false;
      }
    });
  }

  editMedia(): void {
    if (this.item) {
      this.router.navigate(['/edit-media/', this.item.id], {
        queryParams: { returnUrl: `/view-media/${this.item.id}` }
      });
    }
  }

  goBack(): void {
    if (!this.item) {
      this.router.navigate(['/home']);
      return;
    }
    const redirectPath = `/${this.mediaRouteMap[this.item.type]}`;
    this.isLoading = false;
    this.router.navigate([redirectPath]);
  }

}
