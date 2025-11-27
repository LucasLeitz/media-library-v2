import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  provideHttpClientTesting,
  HttpTestingController,
} from '@angular/common/http/testing';
import { MediaService } from './media.service';
import {
  CreateMediaRequest,
  Media,
  MediaType,
  MediaStatus,
} from '../models/media';
import { environment } from '../../environments/environment';

describe('MediaService', () => {
  let service: MediaService;
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.apiBaseUrl}/api`;
  const baseMediaUrl = `${environment.apiBaseUrl}/api/media`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        MediaService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(MediaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('GET methods', () => {
    it('should get all media', () => {
      const mockMedia: Media[] = [
        {
          id: '1',
          name: 'Test Movie',
          type: MediaType.MOVIE,
          status: MediaStatus.BACKLOG,
          createdAt: '2024-01-01',
          updatedAt: '2024-01-01',
        } as Media,
        {
          id: '2',
          name: 'Test Book',
          type: MediaType.BOOK,
          status: MediaStatus.IN_PROGRESS,
          createdAt: '2024-01-02',
          updatedAt: '2024-01-02',
        } as Media,
      ];

      service.getAllMedia().subscribe((media) => {
        expect(media).toEqual(mockMedia);
        expect(media.length).toBe(2);
      });

      const req = httpMock.expectOne(baseMediaUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockMedia);
    });

    it('should get media by id', () => {
      const mockMedia: Media = {
        id: '123',
        name: 'Test Media',
        type: MediaType.MOVIE,
        status: MediaStatus.COMPLETED,
        createdAt: '2024-01-01',
        updatedAt: '2024-01-01',
      } as Media;

      service.getMediaById('123').subscribe((media) => {
        expect(media).toEqual(mockMedia);
        expect(media.id).toBe('123');
      });

      const req = httpMock.expectOne(`${baseMediaUrl}/123`);
      expect(req.request.method).toBe('GET');
      req.flush(mockMedia);
    });

    it('should get media by type', () => {
      const mockMedia: Media[] = [
        {
          id: '1',
          name: 'Movie 1',
          type: MediaType.MOVIE,
          status: MediaStatus.BACKLOG,
          createdAt: '2024-01-01',
          updatedAt: '2024-01-01',
        } as Media,
      ];

      service.getMediaByType(MediaType.MOVIE).subscribe((media) => {
        expect(media).toEqual(mockMedia);
      });

      const req = httpMock.expectOne(`${baseMediaUrl}?type=MOVIE`);
      expect(req.request.method).toBe('GET');
      expect(req.request.params.get('type')).toBe('MOVIE');
      req.flush(mockMedia);
    });

    it('should get media by status', () => {
      const mockMedia: Media[] = [
        {
          id: '1',
          name: 'Completed Movie',
          type: MediaType.MOVIE,
          status: MediaStatus.COMPLETED,
          createdAt: '2024-01-01',
          updatedAt: '2024-01-01',
        } as Media,
      ];

      service.getMediaByStatus(MediaStatus.COMPLETED).subscribe((media) => {
        expect(media).toEqual(mockMedia);
      });

      const req = httpMock.expectOne(`${baseMediaUrl}?status=COMPLETED`);
      expect(req.request.method).toBe('GET');
      expect(req.request.params.get('status')).toBe('COMPLETED');
      req.flush(mockMedia);
    });
  });

  describe('POST methods', () => {
    it('should create media', () => {
      const createRequest: CreateMediaRequest = {
        name: 'New Movie',
        type: MediaType.MOVIE,
        status: MediaStatus.BACKLOG,
      };

      const mockResponse: Media = {
        id: 'new-id',
        ...createRequest,
        createdAt: '2024-01-01',
        updatedAt: '2024-01-01',
      } as Media;

      service.createMedia(createRequest).subscribe((media) => {
        expect(media).toEqual(mockResponse);
        expect(media.id).toBe('new-id');
      });

      const req = httpMock.expectOne(baseMediaUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(createRequest);
      req.flush(mockResponse);
    });

    it('should create book details', () => {
      const mediaId = 'book-123';
      const author = 'J.K. Rowling';

      service.createBookDetails(mediaId, author).subscribe();

      const req = httpMock.expectOne(
        `${baseUrl}/bookdetails/${mediaId}?author=J.K.%20Rowling`,
      );
      expect(req.request.method).toBe('POST');
      expect(req.request.params.get('author')).toBe(author);
      expect(req.request.body).toBeNull();
      req.flush({});
    });

    it('should create game details', () => {
      const mediaId = 'game-456';
      const platform = 'PLAYSTATION_5';

      service.createGameDetails(mediaId, platform).subscribe();

      const req = httpMock.expectOne(
        `${baseUrl}/gamedetails/${mediaId}?platform=PLAYSTATION_5`,
      );
      expect(req.request.method).toBe('POST');
      expect(req.request.params.get('platform')).toBe(platform);
      expect(req.request.body).toBeNull();
      req.flush({});
    });
  });

  describe('PATCH methods', () => {
    it('should rename media', () => {
      const mediaId = '123';
      const newName = 'Updated Movie Name';
      const mockResponse: Media = {
        id: mediaId,
        name: newName,
        type: MediaType.MOVIE,
        status: MediaStatus.BACKLOG,
        createdAt: '2024-01-01',
        updatedAt: '2024-01-01',
      } as Media;

      service.renameMedia(mediaId, newName).subscribe((media) => {
        expect(media.name).toBe(newName);
      });

      const req = httpMock.expectOne(`${baseMediaUrl}/${mediaId}/name`);
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toBe(newName);
      expect(req.request.headers.get('Content-Type')).toBe('text/plain');
      req.flush(mockResponse);
    });

    it('should set media status without completedAt', () => {
      const mediaId = '123';
      const status = MediaStatus.IN_PROGRESS;
      const mockResponse: Media = {
        id: mediaId,
        name: 'Test',
        type: MediaType.MOVIE,
        status: status,
        createdAt: '2024-01-01',
        updatedAt: '2024-01-01',
      } as Media;

      service.setMediaStatus(mediaId, status, null).subscribe((media) => {
        expect(media.status).toBe(status);
      });

      const req = httpMock.expectOne(
        `${baseMediaUrl}/${mediaId}/status?status=IN_PROGRESS`,
      );
      expect(req.request.method).toBe('PATCH');
      expect(req.request.params.get('status')).toBe(status);
      expect(req.request.params.has('completedAt')).toBeFalse();
      expect(req.request.body).toBeNull();
      req.flush(mockResponse);
    });

    it('should set media status with completedAt', () => {
      const mediaId = '123';
      const status = MediaStatus.COMPLETED;
      const completedAt = '2024-01-15T10:30:00';
      const mockResponse: Media = {
        id: mediaId,
        name: 'Test',
        type: MediaType.MOVIE,
        status: status,
        completedAt: completedAt,
        createdAt: '2024-01-01',
        updatedAt: '2024-01-01',
      } as Media;

      service
        .setMediaStatus(mediaId, status, completedAt)
        .subscribe((media) => {
          expect(media.status).toBe(status);
          expect(media.completedAt).toBe(completedAt);
        });

      const req = httpMock.expectOne(
        `${baseMediaUrl}/${mediaId}/status?status=COMPLETED&completedAt=2024-01-15T10:30:00`,
      );
      expect(req.request.method).toBe('PATCH');
      expect(req.request.params.get('status')).toBe(status);
      expect(req.request.params.get('completedAt')).toBe(completedAt);
      req.flush(mockResponse);
    });

    it('should set media image url', () => {
      const mediaId = '123';
      const imageUrl = 'https://example.com/image.jpg';
      const mockResponse: Media = {
        id: mediaId,
        name: 'Test',
        type: MediaType.MOVIE,
        status: MediaStatus.BACKLOG,
        imageUrl: imageUrl,
        createdAt: '2024-01-01',
        updatedAt: '2024-01-01',
      } as Media;

      service.setMediaImageUrl(mediaId, imageUrl).subscribe((media) => {
        expect(media.imageUrl).toBe(imageUrl);
      });

      const req = httpMock.expectOne(`${baseMediaUrl}/${mediaId}/image-url`);
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toBe(imageUrl);
      expect(req.request.headers.get('Content-Type')).toBe('text/plain');
      req.flush(mockResponse);
    });

    it('should set media image url to null', () => {
      const mediaId = '123';
      const mockResponse: Media = {
        id: mediaId,
        name: 'Test',
        type: MediaType.MOVIE,
        status: MediaStatus.BACKLOG,
        imageUrl: null,
        createdAt: '2024-01-01',
        updatedAt: '2024-01-01',
      } as Media;

      service.setMediaImageUrl(mediaId, null).subscribe((media) => {
        expect(media.imageUrl).toBeNull();
      });

      const req = httpMock.expectOne(`${baseMediaUrl}/${mediaId}/image-url`);
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toBe('');
      req.flush(mockResponse);
    });

    it('should update book author', () => {
      const mediaId = '123';
      const author = 'George Orwell';

      service.updateBookAuthor(mediaId, author).subscribe();

      const req = httpMock.expectOne(
        `${baseUrl}/bookdetails/${mediaId}/author`,
      );
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toBe(author);
      expect(req.request.headers.get('Content-Type')).toBe('text/plain');
      req.flush({});
    });

    it('should update game platform', () => {
      const mediaId = '123';
      const platform = 'NINTENDO_SWITCH';

      service.updateGamePlatform(mediaId, platform).subscribe();

      const req = httpMock.expectOne(
        `${baseUrl}/gamedetails/${mediaId}/platform?platform=NINTENDO_SWITCH`,
      );
      expect(req.request.method).toBe('PATCH');
      expect(req.request.params.get('platform')).toBe(platform);
      expect(req.request.body).toBeNull();
      req.flush({});
    });
  });

  describe('PUT and DELETE methods', () => {
    it('should update media', () => {
      const mediaId = '123';
      const updateRequest: Partial<CreateMediaRequest> = {
        name: 'Updated Name',
        status: MediaStatus.COMPLETED,
      };
      const mockResponse: Media = {
        id: mediaId,
        name: 'Updated Name',
        type: MediaType.MOVIE,
        status: MediaStatus.COMPLETED,
        createdAt: '2024-01-01',
        updatedAt: '2024-01-15',
      } as Media;

      service.updateMedia(mediaId, updateRequest).subscribe((media) => {
        expect(media.name).toBe('Updated Name');
        expect(media.status).toBe(MediaStatus.COMPLETED);
      });

      const req = httpMock.expectOne(`${baseMediaUrl}/${mediaId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateRequest);
      req.flush(mockResponse);
    });

    it('should delete media', () => {
      const mediaId = '123';

      service.deleteMedia(mediaId).subscribe();

      const req = httpMock.expectOne(`${baseMediaUrl}/${mediaId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });
});
