import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { CreateMediaRequest, Media, MediaType, MediaStatus} from '../models/media';

@Injectable({
  providedIn: 'root'
})
export class MediaService {

  private readonly baseUrl = `${environment.apiBaseUrl}/api`;
  private readonly baseMediaUrl = `${environment.apiBaseUrl}/api/media`;

  constructor(private http: HttpClient) {}

  createMedia(request: CreateMediaRequest): Observable<Media> {
    return this.http.post<Media>(this.baseMediaUrl, request);
  }

  getAllMedia(): Observable<Media[]> {
    return this.http.get<Media[]>(this.baseMediaUrl);
  }

  getMediaById(id: string): Observable<Media> {
    return this.http.get<Media>(`${this.baseMediaUrl}/${id}`);
  }

  getMediaByType(type: MediaType): Observable<Media[]> {
    return this.http.get<Media[]>(this.baseMediaUrl, {
      params: { type }
    });
  }

  getMediaByStatus(status: MediaStatus): Observable<Media[]> {
    return this.http.get<Media[]>(this.baseMediaUrl, {
      params: { status }
    });
  }

  renameMedia(id: string, newName: string): Observable<Media> {
    return this.http.patch<Media>(
      `${this.baseMediaUrl}/${id}/name`,
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
      `${this.baseMediaUrl}/${id}/status`,
      null,
      { params }
    );
  }

  setMediaImageUrl(id: string, imageUrl: string | null): Observable<Media> {
    const body = imageUrl ?? '';
    return this.http.patch<Media>(
      `${this.baseMediaUrl}/${id}/image-url`,
      body,
      { headers: new HttpHeaders({ 'Content-Type': 'text/plain' }) }
    );
  }
  updateMedia(id: string, request: Partial<CreateMediaRequest>): Observable<Media> {
    return this.http.put<Media>(`${this.baseMediaUrl}/${id}`, request);
  }

  deleteMedia(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseMediaUrl}/${id}`);
  }

  updateBookAuthor(mediaId: string, author: string): Observable<any> {
    return this.http.patch(`${this.baseUrl}/bookdetails/${mediaId}/author`, author, {
      headers: { 'Content-Type': 'text/plain' }
    });
  }

  createBookDetails(mediaId: string, author: string): Observable<any> {
    const params = new HttpParams().set('author', author);
    return this.http.post(`${this.baseUrl}/bookdetails/${mediaId}`, null, { params });
  }

  createGameDetails(mediaId: string, platform: string): Observable<any> {
    const params = new HttpParams().set('platform', platform);
    return this.http.post(`${this.baseUrl}/gamedetails/${mediaId}`, null, { params });
  }

  updateGamePlatform(mediaId: string, platform: string): Observable<any> {
    const params = new HttpParams().set('platform', platform);
    return this.http.patch(`${this.baseUrl}/gamedetails/${mediaId}/platform`, null, { params });
  }





}
