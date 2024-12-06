import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  constructor(private http: HttpClient) {
  }

  getCountries() {
    const {apiUrl} = environment;
    return this.http.get<string[]>(`${apiUrl}/users/register`);
  }

}
