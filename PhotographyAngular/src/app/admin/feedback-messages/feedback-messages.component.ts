import {Component, OnInit} from '@angular/core';
import {ContactService} from '../../services/contact-service/contact.service';
import {FormsModule} from '@angular/forms';
import {DatePipe} from '@angular/common';

@Component({
    selector: 'app-feedback-messages',
    standalone: true,
    imports: [
        FormsModule,
        DatePipe
    ],
    templateUrl: './feedback-messages.component.html',
    styleUrl: './feedback-messages.component.css'
})
export class FeedbackMessagesComponent implements OnInit {
    messages: any[] = [];
    filteredMessages: any[] = [];
    filter: 'all' | 'answered' | 'unanswered' | 'deleted' = 'unanswered';
    errorMessage: string | null = null;

    isSortDescending: boolean = true;

    constructor(private contactService: ContactService) {
    }

    ngOnInit(): void {
        this.loadMessages();
    }

    loadMessages(): void {
        this.contactService.getAllContactMessage().subscribe({
            next: (response) => {
                if (Array.isArray(response)) {
                    this.messages = response;
                } else {
                    this.errorMessage = 'Failed to load messages. Invalid data format.';
                }
                this.applyFilter();
                this.errorMessage = null;
            },
            error: (error) => {
                this.errorMessage = 'Failed to load messages. Please try again later.';
            },
        });
    }

    markAsDeleted(message: any): void {
        this.contactService.deleteMessage(message.id).subscribe({
            next: () => {
                alert('Message marked as deleted successfully!');
                message.deleted = true;
                this.applyFilter();
            },
            error: (error) => {
                alert('Failed to delete the message. Please try again.');
            },
        });
    }

    toggleSortOrder(): void {
        this.isSortDescending = !this.isSortDescending;
        this.applyFilter();
    }

    applyFilter(): void {
        let filtered = [];
        if (this.filter === 'unanswered') {
            filtered = this.messages.filter((message: any) => !message.answered && !message.deleted);
        } else if (this.filter === 'answered') {
            filtered = this.messages.filter((message: any) => message.answered && !message.deleted);
        } else if (this.filter === 'deleted') {
            filtered = this.messages.filter((message: any) => message.deleted);
        } else {
            filtered = this.messages.filter((message: any) => message);
        }

        this.filteredMessages = filtered.sort((a: any, b: any) => {
            const dateA = new Date(a.sentAt).getTime();
            const dateB = new Date(b.sentAt).getTime();
            return this.isSortDescending ? dateB - dateA : dateA - dateB;
        });
    }

    setFilter(newFilter: 'all' | 'answered' | 'unanswered' | 'deleted'): void {
        this.filter = newFilter;
        this.applyFilter();
    }

    sendReply(message: any): void {
        if (!message.answer || message.answer.trim().length === 0) {
            alert('Please write a reply before sending.');
            return;
        }

        this.contactService.sendReply({id: message.id, answer: message.answer}).subscribe({
            next: () => {
                alert('Reply sent successfully!');
                message.answered = true;
                this.applyFilter();
            },
            error: (error) => {
                alert('Failed to send reply. Please try again.');
            },
        });
    }
}
