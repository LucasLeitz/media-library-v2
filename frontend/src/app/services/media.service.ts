import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import {
  CreateMediaRequest,
  Media
} from '../models/media';

@Injectable({
  providedIn: 'root'
})
export class MediaService {

  private readonly baseUrl = `${environment.apiBaseUrl}/api/media`;

  constructor(private http: HttpClient) {}

  // POST /api/media
  createMedia(request: CreateMediaRequest): Observable<Media> {
    return this.http.post<Media>(this.baseUrl, request);
  }

  // GET /api/media
  getAllMedia(): Observable<Media[]> {
    return this.http.get<Media[]>(this.baseUrl);
  }

  // GET /api/media/{id}
  getMediaById(id: string): Observable<Media> {
    return this.http.get<Media>(`${this.baseUrl}/${id}`);
  }

  // PUT /api/media/{id}
  updateMedia(id: string, request: Partial<CreateMediaRequest>): Observable<Media> {
    return this.http.put<Media>(`${this.baseUrl}/${id}`, request);
  }

  // DELETE /api/media/{id}
  deleteMedia(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
