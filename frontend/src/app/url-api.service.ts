import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CreateShortUrlRequest, ShortUrl, ShortUrlStats } from './url.models';

@Injectable({ providedIn: 'root' })
export class UrlApiService {
  private readonly endpoint = '/api/v1/urls';

  constructor(private readonly http: HttpClient) {}

  create(request: CreateShortUrlRequest): Observable<ShortUrl> {
    return this.http.post<ShortUrl>(this.endpoint, request);
  }

  recent(): Observable<ShortUrl[]> {
    return this.http.get<ShortUrl[]>(`${this.endpoint}/recent`);
  }

  stats(shortCode: string): Observable<ShortUrlStats> {
    return this.http.get<ShortUrlStats>(`${this.endpoint}/${encodeURIComponent(shortCode)}/stats`);
  }
}
