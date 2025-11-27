import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { HomeComponent } from './home.component';
import { MediaService } from '../../services/media.service';
import { Media, MediaStatus, MediaType } from '../../models/media';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let mediaService: jasmine.SpyObj<MediaService>;

  const currentYear = new Date().getFullYear();

  const mockBooks: Media[] = [
    {
      id: '1',
      name: 'Book 1',
      type: MediaType.BOOK,
      status: MediaStatus.COMPLETED,
      completedAt: `${currentYear}-01-15`,
      createdAt: '2024-01-01',
      updatedAt: '2024-01-01',
    } as Media,
    {
      id: '2',
      name: 'Book 2',
      type: MediaType.BOOK,
      status: MediaStatus.COMPLETED,
      completedAt: `${currentYear - 1}-06-20`,
      createdAt: '2024-01-01',
      updatedAt: '2024-01-01',
    } as Media,
    {
      id: '3',
      name: 'Book 3',
      type: MediaType.BOOK,
      status: MediaStatus.BACKLOG,
      createdAt: '2024-01-01',
      updatedAt: '2024-01-01',
    } as Media,
  ];

  const mockMovies: Media[] = [
    {
      id: '4',
      name: 'Movie 1',
      type: MediaType.MOVIE,
      status: MediaStatus.COMPLETED,
      completedAt: `${currentYear}-03-10`,
      createdAt: '2024-01-01',
      updatedAt: '2024-01-01',
    } as Media,
    {
      id: '5',
      name: 'Movie 2',
      type: MediaType.MOVIE,
      status: MediaStatus.COMPLETED,
      completedAt: `${currentYear}-05-22`,
      createdAt: '2024-01-01',
      updatedAt: '2024-01-01',
    } as Media,
  ];

  const mockTvShows: Media[] = [
    {
      id: '6',
      name: 'TV Show 1',
      type: MediaType.TV,
      status: MediaStatus.COMPLETED,
      completedAt: `${currentYear}-07-30`,
      createdAt: '2024-01-01',
      updatedAt: '2024-01-01',
    } as Media,
  ];

  const mockGames: Media[] = [
    {
      id: '7',
      name: 'Game 1',
      type: MediaType.GAME,
      status: MediaStatus.IN_PROGRESS,
      createdAt: '2024-01-01',
      updatedAt: '2024-01-01',
    } as Media,
  ];

  beforeEach(async () => {
    const mediaServiceSpy = jasmine.createSpyObj('MediaService', [
      'getMediaByType',
    ]);

    await TestBed.configureTestingModule({
      declarations: [HomeComponent],
      providers: [{ provide: MediaService, useValue: mediaServiceSpy }],
    }).compileComponents();

    mediaService = TestBed.inject(MediaService) as jasmine.SpyObj<MediaService>;
    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set currentYear to the current year', () => {
    expect(component.currentYear).toBe(currentYear);
  });

  describe('ngOnInit', () => {
    it('should call loadTotalCounts and loadCompletedCounts', () => {
      spyOn(component, 'loadTotalCounts');
      spyOn(component, 'loadCompletedCounts');

      component.ngOnInit();

      expect(component.loadTotalCounts).toHaveBeenCalled();
      expect(component.loadCompletedCounts).toHaveBeenCalled();
    });
  });

  describe('loadTotalCounts', () => {
    beforeEach(() => {
      mediaService.getMediaByType.and.returnValue(of([]));
    });

    it('should call getMediaByType for all media types', () => {
      component.loadTotalCounts();

      expect(mediaService.getMediaByType).toHaveBeenCalledWith(MediaType.BOOK);
      expect(mediaService.getMediaByType).toHaveBeenCalledWith(MediaType.MOVIE);
      expect(mediaService.getMediaByType).toHaveBeenCalledWith(MediaType.TV);
      expect(mediaService.getMediaByType).toHaveBeenCalledWith(MediaType.GAME);
      expect(mediaService.getMediaByType).toHaveBeenCalledTimes(4);
    });

    it('should count only completed books', () => {
      mediaService.getMediaByType.and.callFake((type: MediaType) => {
        if (type === MediaType.BOOK) return of(mockBooks);
        return of([]);
      });

      component.loadTotalCounts();

      expect(component.totalBooks).toBe(2); // Only completed books
    });

    it('should count only completed movies', () => {
      mediaService.getMediaByType.and.callFake((type: MediaType) => {
        if (type === MediaType.MOVIE) return of(mockMovies);
        return of([]);
      });

      component.loadTotalCounts();

      expect(component.totalMovies).toBe(2); // All movies are completed
    });

    it('should count only completed TV shows', () => {
      mediaService.getMediaByType.and.callFake((type: MediaType) => {
        if (type === MediaType.TV) return of(mockTvShows);
        return of([]);
      });

      component.loadTotalCounts();

      expect(component.totalTvShows).toBe(1); // One completed TV show
    });

    it('should count only completed video games', () => {
      mediaService.getMediaByType.and.callFake((type: MediaType) => {
        if (type === MediaType.GAME) return of(mockGames);
        return of([]);
      });

      component.loadTotalCounts();

      expect(component.totalVideoGames).toBe(0); // No completed games
    });

    it('should handle errors when fetching books', () => {
      spyOn(console, 'error');
      const error = new Error('Failed to fetch books');
      mediaService.getMediaByType.and.callFake((type: MediaType) => {
        if (type === MediaType.BOOK) return throwError(() => error);
        return of([]);
      });

      component.loadTotalCounts();

      expect(console.error).toHaveBeenCalledWith(
        'Error fetching books:',
        error,
      );
    });

    it('should handle errors when fetching movies', () => {
      spyOn(console, 'error');
      const error = new Error('Failed to fetch movies');
      mediaService.getMediaByType.and.callFake((type: MediaType) => {
        if (type === MediaType.MOVIE) return throwError(() => error);
        return of([]);
      });

      component.loadTotalCounts();

      expect(console.error).toHaveBeenCalledWith(
        'Error fetching movies:',
        error,
      );
    });

    it('should handle errors when fetching TV shows', () => {
      spyOn(console, 'error');
      const error = new Error('Failed to fetch TV shows');
      mediaService.getMediaByType.and.callFake((type: MediaType) => {
        if (type === MediaType.TV) return throwError(() => error);
        return of([]);
      });

      component.loadTotalCounts();

      expect(console.error).toHaveBeenCalledWith(
        'Error fetching TV shows:',
        error,
      );
    });

    it('should handle errors when fetching video games', () => {
      spyOn(console, 'error');
      const error = new Error('Failed to fetch games');
      mediaService.getMediaByType.and.callFake((type: MediaType) => {
        if (type === MediaType.GAME) return throwError(() => error);
        return of([]);
      });

      component.loadTotalCounts();

      expect(console.error).toHaveBeenCalledWith(
        'Error fetching video games:',
        error,
      );
    });
  });

  describe('loadCompletedCounts', () => {
    beforeEach(() => {
      mediaService.getMediaByType.and.returnValue(of([]));
    });

    it('should call getMediaByType for all media types', () => {
      component.loadCompletedCounts();

      expect(mediaService.getMediaByType).toHaveBeenCalledWith(MediaType.BOOK);
      expect(mediaService.getMediaByType).toHaveBeenCalledWith(MediaType.MOVIE);
      expect(mediaService.getMediaByType).toHaveBeenCalledWith(MediaType.TV);
      expect(mediaService.getMediaByType).toHaveBeenCalledWith(MediaType.GAME);
      expect(mediaService.getMediaByType).toHaveBeenCalledTimes(4);
    });

    it('should count books completed this year', () => {
      mediaService.getMediaByType.and.callFake((type: MediaType) => {
        if (type === MediaType.BOOK) return of(mockBooks);
        return of([]);
      });

      component.loadCompletedCounts();

      expect(component.completedBooksCount).toBe(1); // Only Book 1 was completed this year
    });

    it('should count movies completed this year', () => {
      mediaService.getMediaByType.and.callFake((type: MediaType) => {
        if (type === MediaType.MOVIE) return of(mockMovies);
        return of([]);
      });

      component.loadCompletedCounts();

      expect(component.completedMoviesCount).toBe(2); // Both movies completed this year
    });

    it('should count TV shows completed this year', () => {
      mediaService.getMediaByType.and.callFake((type: MediaType) => {
        if (type === MediaType.TV) return of(mockTvShows);
        return of([]);
      });

      component.loadCompletedCounts();

      expect(component.completedTvShowsCount).toBe(1); // One TV show completed this year
    });

    it('should count video games completed this year', () => {
      mediaService.getMediaByType.and.callFake((type: MediaType) => {
        if (type === MediaType.GAME) return of(mockGames);
        return of([]);
      });

      component.loadCompletedCounts();

      expect(component.completedVideoGamesCount).toBe(0); // No completed games
    });

    it('should handle errors when fetching completed books', () => {
      spyOn(console, 'error');
      const error = new Error('Failed to fetch books');
      mediaService.getMediaByType.and.callFake((type: MediaType) => {
        if (type === MediaType.BOOK) return throwError(() => error);
        return of([]);
      });

      component.loadCompletedCounts();

      expect(console.error).toHaveBeenCalledWith(
        'Error fetching completed books:',
        error,
      );
    });

    it('should handle errors when fetching completed movies', () => {
      spyOn(console, 'error');
      const error = new Error('Failed to fetch movies');
      mediaService.getMediaByType.and.callFake((type: MediaType) => {
        if (type === MediaType.MOVIE) return throwError(() => error);
        return of([]);
      });

      component.loadCompletedCounts();

      expect(console.error).toHaveBeenCalledWith(
        'Error fetching completed movies:',
        error,
      );
    });

    it('should handle errors when fetching completed TV shows', () => {
      spyOn(console, 'error');
      const error = new Error('Failed to fetch TV shows');
      mediaService.getMediaByType.and.callFake((type: MediaType) => {
        if (type === MediaType.TV) return throwError(() => error);
        return of([]);
      });

      component.loadCompletedCounts();

      expect(console.error).toHaveBeenCalledWith(
        'Error fetching completed TV shows:',
        error,
      );
    });

    it('should handle errors when fetching completed video games', () => {
      spyOn(console, 'error');
      const error = new Error('Failed to fetch games');
      mediaService.getMediaByType.and.callFake((type: MediaType) => {
        if (type === MediaType.GAME) return throwError(() => error);
        return of([]);
      });

      component.loadCompletedCounts();

      expect(console.error).toHaveBeenCalledWith(
        'Error fetching completed video games:',
        error,
      );
    });
  });

  describe('countCompletedThisYear', () => {
    it('should return 0 for empty array', () => {
      const result = component['countCompletedThisYear']([]);
      expect(result).toBe(0);
    });

    it('should count only completed media from current year', () => {
      const testMedia: Media[] = [
        {
          id: '1',
          name: 'Item 1',
          type: MediaType.BOOK,
          status: MediaStatus.COMPLETED,
          completedAt: `${currentYear}-06-15`,
          createdAt: '2024-01-01',
          updatedAt: '2024-01-01',
        } as Media,
        {
          id: '2',
          name: 'Item 2',
          type: MediaType.BOOK,
          status: MediaStatus.COMPLETED,
          completedAt: `${currentYear}-12-31`,
          createdAt: '2024-01-01',
          updatedAt: '2024-01-01',
        } as Media,
        {
          id: '3',
          name: 'Item 3',
          type: MediaType.BOOK,
          status: MediaStatus.COMPLETED,
          completedAt: `${currentYear - 1}-06-15`,
          createdAt: '2024-01-01',
          updatedAt: '2024-01-01',
        } as Media,
      ];

      const result = component['countCompletedThisYear'](testMedia);
      expect(result).toBe(2); // Only items 1 and 2 completed this year
    });

    it('should exclude items without COMPLETED status', () => {
      const testMedia: Media[] = [
        {
          id: '1',
          name: 'Item 1',
          type: MediaType.BOOK,
          status: MediaStatus.IN_PROGRESS,
          completedAt: `${currentYear}-06-15`,
          createdAt: '2024-01-01',
          updatedAt: '2024-01-01',
        } as Media,
        {
          id: '2',
          name: 'Item 2',
          type: MediaType.BOOK,
          status: MediaStatus.BACKLOG,
          completedAt: `${currentYear}-06-15`,
          createdAt: '2024-01-01',
          updatedAt: '2024-01-01',
        } as Media,
      ];

      const result = component['countCompletedThisYear'](testMedia);
      expect(result).toBe(0);
    });

    it('should exclude completed items without completedAt date', () => {
      const testMedia: Media[] = [
        {
          id: '1',
          name: 'Item 1',
          type: MediaType.BOOK,
          status: MediaStatus.COMPLETED,
          createdAt: '2024-01-01',
          updatedAt: '2024-01-01',
        } as Media,
      ];

      const result = component['countCompletedThisYear'](testMedia);
      expect(result).toBe(0);
    });

    it('should exclude items completed in previous years', () => {
      const testMedia: Media[] = [
        {
          id: '1',
          name: 'Item 1',
          type: MediaType.BOOK,
          status: MediaStatus.COMPLETED,
          completedAt: `${currentYear - 1}-12-31`,
          createdAt: '2024-01-01',
          updatedAt: '2024-01-01',
        } as Media,
        {
          id: '2',
          name: 'Item 2',
          type: MediaType.BOOK,
          status: MediaStatus.COMPLETED,
          completedAt: `${currentYear - 2}-06-15`,
          createdAt: '2024-01-01',
          updatedAt: '2024-01-01',
        } as Media,
      ];

      const result = component['countCompletedThisYear'](testMedia);
      expect(result).toBe(0);
    });

    it('should handle completedAt at beginning of year', () => {
      const testMedia: Media[] = [
        {
          id: '1',
          name: 'Item 1',
          type: MediaType.BOOK,
          status: MediaStatus.COMPLETED,
          completedAt: `${currentYear}-01-01`,
          createdAt: '2024-01-01',
          updatedAt: '2024-01-01',
        } as Media,
      ];

      const result = component['countCompletedThisYear'](testMedia);
      expect(result).toBe(1);
    });

    it('should handle completedAt at end of year', () => {
      const testMedia: Media[] = [
        {
          id: '1',
          name: 'Item 1',
          type: MediaType.BOOK,
          status: MediaStatus.COMPLETED,
          completedAt: `${currentYear}-12-31`,
          createdAt: '2024-01-01',
          updatedAt: '2024-01-01',
        } as Media,
      ];

      const result = component['countCompletedThisYear'](testMedia);
      expect(result).toBe(1);
    });

    it('should handle mixed scenarios correctly', () => {
      const testMedia: Media[] = [
        {
          id: '1',
          name: 'Item 1',
          type: MediaType.BOOK,
          status: MediaStatus.COMPLETED,
          completedAt: `${currentYear}-03-15`,
          createdAt: '2024-01-01',
          updatedAt: '2024-01-01',
        } as Media,
        {
          id: '2',
          name: 'Item 2',
          type: MediaType.BOOK,
          status: MediaStatus.COMPLETED,
          completedAt: `${currentYear - 1}-03-15`,
          createdAt: '2024-01-01',
          updatedAt: '2024-01-01',
        } as Media,
        {
          id: '3',
          name: 'Item 3',
          type: MediaType.BOOK,
          status: MediaStatus.IN_PROGRESS,
          createdAt: '2024-01-01',
          updatedAt: '2024-01-01',
        } as Media,
        {
          id: '4',
          name: 'Item 4',
          type: MediaType.BOOK,
          status: MediaStatus.COMPLETED,
          createdAt: '2024-01-01',
          updatedAt: '2024-01-01',
        } as Media,
        {
          id: '5',
          name: 'Item 5',
          type: MediaType.BOOK,
          status: MediaStatus.COMPLETED,
          completedAt: `${currentYear}-09-20`,
          createdAt: '2024-01-01',
          updatedAt: '2024-01-01',
        } as Media,
      ];

      const result = component['countCompletedThisYear'](testMedia);
      expect(result).toBe(2); // Only items 1 and 5
    });
  });
});
