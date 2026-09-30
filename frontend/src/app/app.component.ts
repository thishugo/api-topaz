import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { DatePipe } from '@angular/common';
import { finalize } from 'rxjs';
import { ProblemDetails, ShortUrl, ShortUrlStats } from './url.models';
import { UrlApiService } from './url-api.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [DatePipe, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  originalUrl = '';
  customAlias = '';
  isSubmitting = false;
  isLoadingRecent = false;
  loadingStatsCode: string | null = null;
  expandedCode: string | null = null;
  generatedUrl: ShortUrl | null = null;
  urls: ShortUrl[] = [];
  stats: ShortUrlStats | null = null;
  errorMessage = '';
  copied = false;

  constructor(private readonly api: UrlApiService) {}

  ngOnInit(): void {
    this.loadRecent();
  }

  submitUrl(): void {
    this.errorMessage = '';
    this.copied = false;
    this.isSubmitting = true;
    const request = {
      originalUrl: this.originalUrl.trim(),
      customAlias: this.customAlias.trim() || undefined
    };

    this.api.create(request).pipe(finalize(() => this.isSubmitting = false)).subscribe({
      next: (url) => {
        this.generatedUrl = url;
        this.originalUrl = '';
        this.customAlias = '';
        this.loadRecent();
      },
      error: (error: unknown) => this.errorMessage = this.messageFrom(error)
    });
  }

  loadRecent(): void {
    this.isLoadingRecent = true;
    this.api.recent().pipe(finalize(() => this.isLoadingRecent = false)).subscribe({
      next: (urls) => this.urls = urls,
      error: (error: unknown) => this.errorMessage = this.messageFrom(error)
    });
  }

  toggleStats(url: ShortUrl): void {
    if (this.expandedCode === url.shortCode) {
      this.expandedCode = null;
      this.stats = null;
      return;
    }

    this.expandedCode = url.shortCode;
    this.stats = null;
    this.loadingStatsCode = url.shortCode;
    this.errorMessage = '';
    this.api.stats(url.shortCode).pipe(finalize(() => this.loadingStatsCode = null)).subscribe({
      next: (stats) => this.stats = stats,
      error: (error: unknown) => this.errorMessage = this.messageFrom(error)
    });
  }

  copyGeneratedUrl(): void {
    if (!this.generatedUrl) {
      return;
    }

    navigator.clipboard.writeText(this.generatedUrl.shortUrl).then(() => {
      this.copied = true;
      window.setTimeout(() => this.copied = false, 1800);
    }).catch(() => this.errorMessage = 'Não foi possível copiar o link neste navegador.');
  }

  private messageFrom(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      const problem = error.error as ProblemDetails | null;
      return problem?.detail || problem?.title || 'Não foi possível concluir a solicitação.';
    }
    return 'Não foi possível concluir a solicitação.';
  }
}
