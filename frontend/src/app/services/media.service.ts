import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { CreateMediaRequest, Media, MediaType, MediaStatus} from '../models/media';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class MediaService {

  private readonly baseUrl = `${environment.apiBaseUrl}/api/media`;

  constructor(private http: HttpClient) {}

  createMedia(request: CreateMediaRequest): Observable<Media> {
    return this.http.post<Media>(this.baseUrl, request);
  }

  getAllMedia(): Observable<Media[]> {
    return this.http.get<Media[]>(this.baseUrl);
  }

  getMediaById(id: string): Observable<Media> {
    return this.http.get<Media>(`${this.baseUrl}/${id}`);
  }

  getMediaByType(type: MediaType): Observable<Media[]> {
    return this.http.get<Media[]>(this.baseUrl, {
      params: { type }
    });
  }

  getMediaByStatus(status: MediaStatus): Observable<Media[]> {
    return this.http.get<Media[]>(this.baseUrl, {
      params: { status }
    });
  }

  getCompletedCountForYear(type: MediaType, year: number): Observable<number> {
    return this.getMediaByType(type).pipe(
      map(items =>
        items.filter(m =>
          m.status === MediaStatus.COMPLETED &&
          m.completedAt &&
          new Date(m.completedAt).getFullYear() === year
        ).length
      )
    );
  }

  renameMedia(id: string, newName: string): Observable<Media> {
    return this.http.patch<Media>(
      `${this.baseUrl}/${id}/name`,
      newName,
      { headers: { 'Content-Type': 'text/plain' } }
    );
  }

  setMediaStatus(id: string, status: MediaStatus, completedAt: string | null): Observable<Media> {
    let params = new HttpParams().set('status', status);

    if (completedAt) {
      params = params.set('completedAt', completedAt);
    }

    return this.http.patch<Media>(
      `${this.baseUrl}/${id}/status`,
      null,
      { params }
    );
  }

  setMediaImageUrl(id: string, imageUrl: string | null): Observable<Media> {
    const body = imageUrl ?? '';
    return this.http.patch<Media>(
      `${this.baseUrl}/${id}/image-url`,
      body,
      { headers: new HttpHeaders({ 'Content-Type': 'text/plain' }) }
    );
  }
  updateMedia(id: string, request: Partial<CreateMediaRequest>): Observable<Media> {
    return this.http.put<Media>(`${this.baseUrl}/${id}`, request);
  }

  deleteMedia(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
