import {inject, Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {
    HttpErrorResponse,
    HttpEvent,
    HttpHandler,
    HttpHandlerFn,
    HttpInterceptorFn,
    HttpRequest
} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';

export const HttpErrorInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> => {
    const router = inject(Router);

    return next(req).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status === 404) {
                router.navigate(['/page-not-found']);
            } else if (error.status === 500) {
                router.navigate(['/server-error']);
            } else if (error.status === 503 || error.status === 0) {
                router.navigate(['/server-down']);
            }
            return throwError(() => error);
        })
    );
}
