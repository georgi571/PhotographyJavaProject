import {Component, OnInit} from '@angular/core';
import {ContactService} from '../../services/contact-service/contact.service';
import {FormsModule} from '@angular/forms';

@Component({
    selector: 'app-feedback-messages',
    imports: [
        FormsModule
    ],
    templateUrl: './feedback-messages.component.html',
    styleUrl: './feedback-messages.component.css'
})
export class FeedbackMessagesComponent implements OnInit {
    messages: any = [];
    errorMessage: string | null = null;

    constructor(private contactService: ContactService) {
    }

    ngOnInit(): void {
        this.loadMessages();
    }

    loadMessages(): void {
        this.contactService.getAllContactMessage()
            .subscribe({
                next: (response) => {
                    this.messages = response;
                    this.errorMessage = null;
                },
                error: (error) => {
                    console.error('Error fetching messages:', error);
                    this.errorMessage = 'Failed to load messages. Please try again later.';
                }
            });
    }

    sendReply(message: any): void {
        if (!message.answer || message.answer.trim().length === 0) {
            alert('Please write a reply before sending.');
            return;
        }

        this.contactService.sendReply({id: message.id, answer: message.answer})
            .subscribe({
                next: () => {
                    alert('Reply sent successfully!');
                    this.messages = this.messages.filter((m: any) => m.id !== message.id);
                },
                error: (error) => {
                    console.error('Error sending reply:', error);
                    alert('Failed to send reply. Please try again.');
                    console.log({id: message.id, answer: message.answer})
                }
            });
    }
}
