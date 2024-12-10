import { Injectable } from '@angular/core';
import {environment} from '../../environments/environment.development';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ContactService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {
  }

  sendContactMessage(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/contacts/send`, data);
  }
}
