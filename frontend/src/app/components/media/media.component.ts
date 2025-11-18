import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { Media, MediaStatus, MediaType } from '../../models/media';
import { MediaService } from '../../services/media.service';

@Component({
  selector: 'app-media',
  templateUrl: './media.component.html',
  styleUrls: ['./media.component.scss'],
})
export class MediaComponent implements OnInit {

  private readonly listRouteMap: Record<MediaType, string> = {
    [MediaType.BOOK]:  'books',
    [MediaType.MOVIE]: 'movies',
    [MediaType.TV]:    'tv',
    [MediaType.GAME]:  'games',
  };

  mediaType!: MediaType;
  currentStatus!: MediaStatus;

  mediaList: Media[] = [];
  filteredList: Media[] = [];
  flippedCards: boolean[] = [];
  searchTerm = '';
  showSearch = false;
  showSortOptions = false;
  sortCriteria: 'name' | 'completedAt' = 'name';

  constructor(
    private mediaService: MediaService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const typeFromRoute   = this.route.snapshot.data['mediaType'] as MediaType | undefined;
    const statusFromRoute = this.route.snapshot.data['status'] as MediaStatus | undefined;

    if (!typeFromRoute || !statusFromRoute) {
      console.error('Missing mediaType or status in route data!');
      return;
    }

    this.mediaType = typeFromRoute;
    this.currentStatus = statusFromRoute;

    this.mediaService.getMediaByType(this.mediaType).subscribe({
      next: (data: Media[]) => {
        this.mediaList = data.filter(m => m.status === this.currentStatus);
        this.filteredList = [...this.mediaList];
        this.flippedCards = this.mediaList.map(() => false);
      },
      error: (err: unknown) => console.error('Error loading media:', err),
    });
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

  get pageTitle(): string {
    const base = this.mediaLabel;
    switch (this.currentStatus) {
      case MediaStatus.BACKLOG:
        return `Backlog ${base} List`;
      case MediaStatus.IN_PROGRESS:
        return `In-Progress ${base} List`;
      default:
        return `Completed ${base} List`;
    }
  }

  toggleSortOptions(): void {
    this.showSortOptions = !this.showSortOptions;
  }

  sortItems(): void {
    const sorted = [...this.filteredList];
    switch (this.sortCriteria) {
      case 'name':
        sorted.sort((a, b) => a.name.localeCompare(b.name));
        break;
      case 'completedAt':
        sorted.sort((a, b) => (a.completedAt || '').localeCompare(b.completedAt || ''));
        break;
    }
    this.filteredList = sorted;
  }

  toggleSearch(): void {
    this.showSearch = !this.showSearch;
    if (!this.showSearch) {
      this.resetFilter();
    }
  }

  filterItems(): void {
    if (this.searchTerm.trim() === '') {
      this.filteredList = [...this.mediaList];
    } else {
      const term = this.searchTerm.toLowerCase();
      this.filteredList = this.mediaList.filter(item =>
        item.name.toLowerCase().includes(term)
      );
    }
  }

  resetFilter(): void {
    this.searchTerm = '';
    this.filteredList = [...this.mediaList];
  }

  flipCard(index: number): void {
    this.flippedCards[index] = !this.flippedCards[index];
  }

  removeItem(index: number): void {
    const itemToRemove = this.filteredList[index];
    if (!itemToRemove?.id) return;

    if (confirm('Are you sure you want to remove this item?')) {
      this.flipCard(index);
      this.mediaService.deleteMedia(itemToRemove.id).subscribe({
        next: () => {
          this.mediaList = this.mediaList.filter(item => item.id !== itemToRemove.id);
          this.filteredList = this.filteredList.filter(item => item.id !== itemToRemove.id);
        },
        error: (err: unknown) => {
          console.error('Delete failed:', err);
          alert('Could not delete the item.');
        }
      });
    }
  }

  addItem(): void {
    this.router.navigate(['/add'], {
      state: {
        mediaType: this.mediaType,
        status: this.currentStatus,
      },
    });
  }

  editItem(index: number): void {
    const itemToEdit = this.filteredList[index];
    if (!itemToEdit?.id) return;
    this.router.navigate(['/edit-media', itemToEdit.id]);
  }

  viewItem(index: number): void {
    const itemToView = this.filteredList[index];
    if (!itemToView?.id) return;
    this.router.navigate(['/view-media', itemToView.id]);
  }

  goToCompleted(): void {
    const segment = this.listRouteMap[this.mediaType];
    this.router.navigate([`/${segment}`]);
  }

  goToBacklog(): void {
    const segment = this.listRouteMap[this.mediaType];
    this.router.navigate([`/${segment}/backlog`]);
  }

  goToInProgress(): void {
    const segment = this.listRouteMap[this.mediaType];
    this.router.navigate([`/${segment}/in-progress`]);
  }
}
