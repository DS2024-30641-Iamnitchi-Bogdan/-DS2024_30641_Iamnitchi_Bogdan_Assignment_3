import {AfterViewChecked, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ChatService} from "../../../../services/chat/chat.service";
import {MessageComponent} from "./message/message.component";
import {NgForOf, NgIf} from "@angular/common";
import {UserListComponent} from "./user-list/user-list.component";
import {User} from "../../../../domain/user-types";
import {KeycloakService} from "keycloak-angular";
import {Role} from "../../../../domain/emus";
import {ChatMessageRequest, ChatMessage, ChatUser} from "../../../../domain/chat-types";
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
export class ChatComponent implements OnInit, AfterViewChecked {

  @ViewChild('chatContainer') chatContainer!: ElementRef;

  profile: User | undefined = undefined;
  users: ChatUser[] = [];
  selectedUser: ChatUser | null = null;

  messages: ChatMessage[] = [];
  content: string = '';

  constructor(
    private keycloakService: KeycloakService,
    private chatService: ChatService
  ) {}

  ngAfterViewChecked() {
    if (this.chatContainer) {
      const chatElement = this.chatContainer.nativeElement;
      chatElement.scrollTop = chatElement.scrollHeight;
    }
  }

  async ngOnInit() {
    const keycloakProfile = await this.keycloakService.getKeycloakInstance().loadUserProfile();
    this.profile = {
      id: keycloakProfile.id,
      email: keycloakProfile.email,
      firstName: keycloakProfile.firstName,
      lastName: keycloakProfile.lastName,
      role: this.getSingleRole(this.keycloakService.getKeycloakInstance().realmAccess?.roles),
    };
    this.chatService.connect(
      this.profile,
      this.onMessageReceived.bind(this), // Explicitly bind the context
      this.onUserChange.bind(this), // Explicitly bind the context
      this.onErrorMessage.bind(this) // Explicitly bind the context
    );
    this.loadOnlineUsers();
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

  get filteredMessages(): ChatMessage[] {
    if (!this.selectedUser) {
      return [];
    }

    const selectedUserId = this.selectedUser.nickName;
    return this.messages.filter(
      msg =>
        msg.senderId === selectedUserId ||
        msg.recipientId === selectedUserId
    );
  }

  loadMessages(senderId: string, recipientId: string): void {
    this.chatService.getMessages(senderId, recipientId).subscribe({
      next: (messages) => {
        this.messages = messages.map(msg => ({
          ...msg,
          isMine: msg.senderId === this.profile?.email
        }));
        this.messages.forEach(msg => {
          if(this.selectedUser?.nickName === msg.senderId && !msg.read) {
            this.chatService.notifyReadMessage(msg);
          }
        });
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  deleteAllMessages(): void {
    this.chatService.deleteAllMessages().subscribe({
      next: () => {
        this.messages = [];
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  handleUserSelectedEvent(user: ChatUser): void {
    this.selectedUser = user;
    const senderId = this.profile?.email || '';
    const recipientId = this.selectedUser?.nickName || '';
    this.loadMessages(senderId, recipientId);
    this.selectedUser.hasUnreadMessages = false;
  }

  onInputFocus(): void {
    const chatUser: ChatUser = {
      nickName: this.profile?.email || '',
      fullName: this.profile?.firstName + ' ' + this.profile?.lastName,
      status: 'TYPING',
      role: this.profile?.role || ''
    }
    this.chatService.notifyTyping(chatUser);
  }

  onInputBlur(): void {
    const chatUser: ChatUser = {
      nickName: this.profile?.email || '',
      fullName: this.profile?.firstName + ' ' + this.profile?.lastName,
      status: 'ONLINE',
      role: this.profile?.role || ''
    }
    this.chatService.notifyTyping(chatUser);
  }

  sendMessage(): void {
    const message = {
      senderId: this.profile?.email || '',
      recipientId: this.selectedUser?.nickName || '',
      content: this.content,
      timestamp: new Date(),
    };
    this.chatService.sendMessage(message);
    this.content = '';
    this.loadMessages(message.senderId, message.recipientId);
  }

  onMessageReceived(message: any) {
    const msg: ChatMessage = JSON.parse(message.body);
    this.messages.forEach(m => {
      console.log(m.id + "-" + msg.id);
    });
    const existingMessageIndex = this.messages.findIndex(m => m.id === msg.id);
    if (existingMessageIndex !== -1) {
      this.messages[existingMessageIndex] = {
        ...msg,
        isMine: msg.senderId === this.profile?.email
      };
    } else {
      this.messages.push({
        ...msg,
        isMine: msg.senderId === this.profile?.email
      });
      if(this.selectedUser?.nickName === msg.senderId) {
        this.chatService.notifyReadMessage(msg);
      }
    }

    this.users.forEach(user => {
      if (user.nickName === msg.senderId) {
        user.hasUnreadMessages = true;
      }
    });
  }

  onUserChange(user: any) {
    const parsedUser = JSON.parse(user.body);
    if(parsedUser.nickName === this.profile?.email) {
      return;
    }

    const existingUserIndex = this.users.findIndex(user => user.nickName === parsedUser.nickName);
    if (parsedUser.status === 'OFFLINE') {
      if(existingUserIndex !== -1) {
        this.users.splice(existingUserIndex, 1);
      }
    } else {
      if(existingUserIndex !== -1) {
        this.users[existingUserIndex] = parsedUser;
      } else {
        this.users.push(parsedUser);
      }
    }
  }

  onErrorMessage(error: any) {
    console.error('Error:', error);
  }

}
