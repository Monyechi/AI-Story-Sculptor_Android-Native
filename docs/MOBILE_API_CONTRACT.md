# Mobile API Contract (v1)

This contract is optimized for native clients and intentionally decoupled from web-template/session flows.

## Base

- Prefix: `/api/v1/mobile/`
- Auth: `Authorization: Bearer <access_token>`
- Content-Type: `application/json`

## Auth

### `POST /api/v1/mobile/auth/register/`
Request:
```json
{
  "username": "jane_doe",
  "email": "jane@example.com",
  "password": "super-secure-password",
  "display_name": "Jane",
  "agree_terms": true
}
```
Response:
```json
{
  "access_token": "...",
  "refresh_token": "...",
  "user": {
    "id": "42",
    "email": "jane@example.com",
    "display_name": "Jane"
  }
}
```

### `POST /api/v1/mobile/auth/login/`
Request:
```json
{
  "email": "jane@example.com",
  "password": "super-secure-password"
}
```
Response: same as register.

### `POST /api/v1/mobile/auth/refresh/`
Request:
```json
{
  "refresh_token": "..."
}
```
Response:
```json
{
  "access_token": "...",
  "refresh_token": "..."
}
```

## Library

### `GET /api/v1/mobile/books/`
Response:
```json
{
  "items": [
    {
      "id": "101",
      "title": "Moonlight Quest",
      "cover_thumbnail_url": "https://cdn.../thumb.jpg",
      "created_at": "2026-02-15T10:30:00Z",
      "status": "ready"
    }
  ]
}
```

## Create + Job Polling

### `POST /api/v1/mobile/books/`
Request:
```json
{
  "title": "Moonlight Quest",
  "author": "Jane Doe",
  "book_type": "fiction-novel",
  "genre": "Fantasy",
  "language": "English",
  "pov": "Third Person Limited",
  "writing_style": "descriptive",
  "summary": "A curious child discovers...",
  "character_name": "Luna",
  "character_description": "Brave and kind"
}
```
Response:
```json
{
  "job_id": "job_abc123",
  "state": "queued",
  "book_id": null,
  "message": "Book generation started"
}
```

### `GET /api/v1/mobile/jobs/{jobId}/`
Response:
```json
{
  "job_id": "job_abc123",
  "state": "processing",
  "book_id": null,
  "progress_percent": 40,
  "message": "Generating chapter 2"
}
```
Terminal response example:
```json
{
  "job_id": "job_abc123",
  "state": "completed",
  "book_id": "101",
  "progress_percent": 100,
  "message": "Done"
}
```

## Details

### `GET /api/v1/mobile/books/{bookId}/`
Response:
```json
{
  "id": "101",
  "title": "Moonlight Quest",
  "author": "Jane Doe",
  "genre": "Fantasy",
  "language": "English",
  "cover_image_url": "https://cdn.../cover.jpg",
  "created_at": "2026-02-15T10:30:00Z",
  "status": "ready",
  "download_url": "https://api.../download/pdf/101/",
  "share_url": "https://aistorysculptor.com/book/101/",
  "chapters": [
    {
      "index": 1,
      "title": "The Lantern",
      "content": "..."
    }
  ]
}
```

## Download Link

### `GET /api/v1/mobile/books/{bookId}/download/?format=pdf`
Response:
```json
{
  "url": "https://api.../download/pdf/101/",
  "format": "pdf",
  "expires_at": "2026-02-15T12:00:00Z"
}
```

## Error Shape (recommended)

```json
{
  "error": {
    "code": "validation_error",
    "message": "One or more fields are invalid",
    "details": {
      "email": ["This field is required"]
    }
  }
}
```
