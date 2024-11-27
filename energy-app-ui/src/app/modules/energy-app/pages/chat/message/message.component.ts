import {Component, Input} from '@angular/core';
import {NgClass, NgIf} from "@angular/common";
import {ChatMessage} from "../../../../../domain/chat-types";

@Component({
  selector: 'app-message',
  standalone: true,
  imports: [
    NgIf,
    NgClass
  ],
  templateUrl: './message.component.html',
  styleUrl: './message.component.scss'
})
export class MessageComponent {
  @Input() message!: ChatMessage;

  get formattedTimestamp(): string {
    const date = new Date(this.message.timestamp);
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }
}
