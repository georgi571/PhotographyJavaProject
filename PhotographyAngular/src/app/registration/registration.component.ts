import {Component, OnInit} from '@angular/core';
import {HeaderComponent} from '../core/header/header.component';
import {ApiService} from '../services/api.service';

@Component({
  selector: 'app-registration',
  standalone: true,
  imports: [
    HeaderComponent,
  ],
  templateUrl: './registration.component.html',
  styleUrl: './registration.component.css'
})
export class RegistrationComponent implements OnInit {
  countries: string[] = [];

  constructor(private apiService: ApiService) {
  }

  // Get all country names from the BE

  ngOnInit() {
    this.apiService.getCountries().subscribe((response: any) => {
      if (response && response.countries) {
        this.countries = response.countries;
      }
    });
  }
}
