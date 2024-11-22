import {Component, OnInit} from '@angular/core';
import {ChatService} from "../../../../services/chat/chat.service";
import {MessageComponent} from "./message/message.component";
import {NgForOf} from "@angular/common";
import {UserListComponent} from "./user-list/user-list.component";

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    MessageComponent,
    NgForOf,
    UserListComponent
  ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit {

  users = [
    { name: 'John', hasUnread: true },
    { name: 'Jane', hasUnread: false },
    { name: 'Bob', hasUnread: true }
  ];

  messages = [
    { text: 'Hello, John!', isSeen: true, isMine: false, timestamp: '10:00 AM' },
    { text: 'Hi there!', isSeen: false, isMine: true, timestamp: '10:05 AM' },
    { text: 'How are you?', isSeen: false, isMine: false, timestamp: '10:10 AM' }
  ];

  constructor(
    private chatService: ChatService
  ) {}

  ngOnInit() {
    this.chatService.connect();
  }

}
