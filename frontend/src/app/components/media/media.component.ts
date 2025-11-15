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

  private readonly mediaRouteMap: Record<MediaType, string> = {
    [MediaType.BOOK]: 'book',
    [MediaType.MOVIE]: 'movie',
    [MediaType.TV]: 'tv',
    [MediaType.GAME]: 'game',
  };

  mediaType!: MediaType;
  addRoute!: string;
  editRoutePrefix!: string;
  backlogRoute!: string;

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
    const typeFromRoute = this.route.snapshot.data['mediaType'] as MediaType | undefined;

    if (!typeFromRoute) {
      console.error('Invalid or missing mediaType!');
      return;
    }

    this.mediaType = typeFromRoute;

    const routeSegment = this.mediaRouteMap[this.mediaType];
    this.addRoute = `/add-${routeSegment}`;
    this.editRoutePrefix = `/edit-${routeSegment}`;
    this.backlogRoute = `/${routeSegment}s/backlog`;

    this.mediaService.getMediaByType(this.mediaType).subscribe({
      next: (data: Media[]) => {
        this.mediaList = data.filter(m => m.status === MediaStatus.COMPLETED);
        this.filteredList = [...this.mediaList];
        this.flippedCards = this.mediaList.map(() => false);
      },
      error: (err: unknown) => {
        console.error('Error loading media list:', err);
      }
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
    this.router.navigate([this.addRoute]);
  }

  editItem(index: number): void {
    const itemToEdit = this.filteredList[index];
    if (!itemToEdit?.id) return;
    this.router.navigate(['/edit-media', itemToEdit.id]);
  }

  goToBacklog(): void {
    this.router.navigate([this.backlogRoute]);
  }
}
