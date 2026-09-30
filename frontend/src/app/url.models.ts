export interface CreateShortUrlRequest {
  originalUrl: string;
  customAlias?: string;
}

export interface ShortUrl {
  shortCode: string;
  originalUrl: string;
  customAlias: boolean;
  createdAt: string;
  expiresAt: string | null;
  totalClicks: number;
  shortUrl: string;
}

export interface ShortUrlStats {
  shortCode: string;
  totalClicks: number;
  clickTimestamps: string[];
}

export interface ProblemDetails {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
}
