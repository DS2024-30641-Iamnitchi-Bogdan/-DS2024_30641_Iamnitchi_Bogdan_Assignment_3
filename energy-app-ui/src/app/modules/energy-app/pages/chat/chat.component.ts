import {Component, OnInit} from '@angular/core';
import {ChatService} from "../../../../services/chat/chat.service";
import {MessageComponent} from "./message/message.component";
import {NgForOf, NgIf} from "@angular/common";
import {UserListComponent} from "./user-list/user-list.component";
import {User} from "../../../../domain/user-types";
import {KeycloakService} from "keycloak-angular";
import {Role} from "../../../../domain/emus";
import {ChatMessage, ChatUser} from "../../../../domain/chat-types";

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    MessageComponent,
    NgForOf,
    UserListComponent,
    NgIf
  ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit {

  profile: User | undefined = undefined;
  users: ChatUser[] = [];
  selectedUser: ChatUser | null = null;

  messages: ChatMessage[] = [];

  constructor(
    private keycloakService: KeycloakService,
    private chatService: ChatService
  ) {}

  ngOnInit() {
    this.loadProfile();
    this.chatService.connect();
    this.loadOnlineUsers();
  }

  loadProfile() {
    this.keycloakService.loadUserProfile().then(profile => {
      this.profile = {
        id: profile.id,
        email: profile.email,
        firstName: profile.firstName,
        lastName: profile.lastName,
        role: this.getSingleRole(this.keycloakService.getKeycloakInstance().realmAccess?.roles),
      }
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

}
