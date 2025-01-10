import { Component } from '@angular/core';
import {HeaderComponent} from "../core/header/header.component";
import {FooterComponent} from "../core/footer/footer.component";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ContactService} from '../services/contact-service/contact.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-contact',
  imports: [
    HeaderComponent,
    FooterComponent,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.css'
})
export class ContactComponent {

  contactForm = new FormGroup({
    name: new FormControl('', [Validators.required, Validators.minLength(1), Validators.maxLength(20)]),
    email: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(20), Validators.email]),
    message: new FormControl('', [Validators.required, Validators.minLength(1), Validators.maxLength(2000)])
  },)

  maxCharacters = 2000;
  message: string = '';
  remainingCharacters: number = this.maxCharacters;

  updateCounter(): void {
    const messageControl = this.contactForm.get('message');
    if (messageControl) {
      this.remainingCharacters = this.maxCharacters - (messageControl.value?.length || 0);
    }
  }

  constructor(private contactService: ContactService, private router: Router,) {
  }

  //send contact message to BE

  onSubmit() {
    if (this.contactForm.valid) {
      this.contactService.sendContactMessage(this.contactForm.value).subscribe({
        next: (response) => {
          console.log('Send successful:', response);
          this.router.navigate(['contacts']);
        },
        error: (error) => {
          if (error.status === 404 && error.error) {
            const backendErrors = error.error;

            Object.keys(backendErrors).forEach((field) => {
              if (this.contactForm.get(field)) {
                const currentErrors = this.contactForm.get(field)?.errors || {};
                this.contactForm.get(field)?.setErrors({
                  ...currentErrors,
                  backendError: backendErrors[field],
                });
              }
            });
          } else {
            console.error('Unexpected error:', error);
          }
        },
      });
    } else {
      console.log('Form is invalid');
      this.contactForm.markAllAsTouched();
    }
  }

}
