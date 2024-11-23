import {Component, OnInit} from '@angular/core';
import {ChatService} from "../../../../services/chat/chat.service";
import {MessageComponent} from "./message/message.component";
import {NgForOf, NgIf} from "@angular/common";
import {UserListComponent} from "./user-list/user-list.component";
import {User} from "../../../../domain/user-types";
import {KeycloakService} from "keycloak-angular";
import {Role} from "../../../../domain/emus";
import {ChatMessageRequest, ChatMessageResponse, ChatUser} from "../../../../domain/chat-types";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    MessageComponent,
    NgForOf,
    UserListComponent,
    NgIf,
    FormsModule
  ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit {

  profile: User | undefined = undefined;
  users: ChatUser[] = [];
  selectedUser: ChatUser | null = null;

  messages: ChatMessageResponse[] = [];
  content: string = '';

  constructor(
    private keycloakService: KeycloakService,
    private chatService: ChatService
  ) {}


  ngOnInit() {
    this.keycloakService.loadUserProfile().then(profile => {
      this.profile = {
        id: profile.id,
        email: profile.email,
        firstName: profile.firstName,
        lastName: profile.lastName,
        role: this.getSingleRole(this.keycloakService.getKeycloakInstance().realmAccess?.roles),
      }
      this.chatService.connect(profile, this.onMessageReceived, this.onErrorMessage);
      this.loadOnlineUsers();
    })
  }

  private getSingleRole(roles?: string[]): Role | undefined {
    if (!roles) return undefined; // Return undefined if no roles provided
    const validRoles = roles.filter(role => role === Role.ADMIN || role === Role.USER);
    return validRoles.length > 0 ? validRoles[0] as Role : undefined; // Return the first valid role
  }

  loadOnlineUsers(): void {
    this.chatService.getAllOnlineUsers().subscribe({
      next: (users) => {
        this.users = users.filter(user => user.nickName !== this.profile?.email)
      },
      error: (error) => {
        console.error(error);
      }
    })
  }

  loadMessages(senderId: string, recipientId: string): void {
    this.chatService.getMessages(senderId, recipientId).subscribe({
      next: (messages) => {
        this.messages = messages.map(msg => ({
          ...msg,
          isMine: msg.senderId === this.profile?.email
        }));
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  handleUserSelected(user: ChatUser): void {
    this.selectedUser = user;
    const senderId = this.profile?.email || '';
    const recipientId = this.selectedUser?.nickName || '';
    this.loadMessages(senderId, recipientId);
  }

  sendMessage(): void {
    console.log('Sending message:', this.content);
    const message = {
      senderId: this.profile?.email || '',
      recipientId: this.selectedUser?.nickName || '',
      content: this.content,
      timestamp: new Date().toISOString(),
    };
    this.chatService.sendMessage(message);
    this.loadMessages(message.senderId, message.recipientId);
  }

  onMessageReceived(message: any) {
    console.log('Message received:', message);
    const msg: ChatMessageResponse = JSON.parse(message.body);
    //selectedUser is null for some reason
    //here we need to update the users list and from whom we have the message
    // if (msg.senderId === this.selectedUser?.nickName || msg.recipientId === this.profile?.email) {
    //   this.messages.push({
    //     ...msg,
    //     isMine: msg.senderId === this.profile?.email
    //   });
    // }
  }

  onErrorMessage(error: any) {
    console.error('Error:', error);
  }

}
