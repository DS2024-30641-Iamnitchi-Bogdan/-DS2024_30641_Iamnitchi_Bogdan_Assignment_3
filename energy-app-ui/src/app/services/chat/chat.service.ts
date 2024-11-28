import { Injectable } from '@angular/core';
import SockJS from 'sockjs-client';
import {Stomp} from '@stomp/stompjs';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ChatMessageRequest, ChatMessage, ChatUser} from "../../domain/chat-types";
import {User} from "../../domain/user-types";
import {KeycloakService} from "keycloak-angular";
@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private stompClient: any;

  chatURL = environment.chatURL;
  onlineUsersPath = "/users/online";
  messagesPath = "/messages";

  constructor(
    private httpClient: HttpClient,
    private keycloakService: KeycloakService
  ) {}


  connect(user: User, onMessageReceived: (message: any) => void, onUserChange: (user: any) => void, onError: (error: any) => void) {
    const ws = new SockJS(this.chatURL + "/ws");
    this.stompClient = Stomp.over(ws);
    this.stompClient.connect({'Authorization': 'Bearer ' + this.keycloakService.getToken()}, () => this.onConnected(user, onMessageReceived, onUserChange), onError);
  }

  onConnected(user: User, onMessageReceived: (message: any) => void, onUserChange: (user: any) => void,) {
    console.log(user.role);
    const chatUser: ChatUser = {
      nickName: user.email || '',
      fullName: user.firstName + ' ' + user.lastName,
      status: 'ONLINE',
      role: user.role || ''
    }

    this.stompClient.subscribe(`/user/${chatUser.nickName}/queue/messages`, (message: any) => onMessageReceived(message));
    this.stompClient.subscribe('/topic/users', (user: any) => onUserChange(user));
    this.stompClient.send("/app/user.addUser",
      {'Authorization': 'Bearer ' + this.keycloakService.getToken()},
      JSON.stringify(chatUser)
    );
  }

  getAllOnlineUsers(): Observable<ChatUser[]> {
    return this.httpClient.get<ChatUser[]>(`${this.chatURL}${this.onlineUsersPath}`);
  }

  getMessages(senderId: string, recipientId: string): Observable<ChatMessage[]> {
    return this.httpClient.get<ChatMessage[]>(`${this.chatURL}${this.messagesPath}/${senderId}/${recipientId}`);
  }

  deleteAllMessages(): Observable<void> {
    return this.httpClient.delete<void>(`${this.chatURL}${this.messagesPath}`);
  }

  sendMessage(message: ChatMessageRequest): void {
    this.stompClient.send("/app/chat",
      {'Authorization': 'Bearer ' + this.keycloakService.getToken()},
      JSON.stringify(message)
    );
  }

  notifyReadMessage(message: ChatMessage): void {
    this.stompClient.send("/app/read",
      {'Authorization': 'Bearer ' + this.keycloakService.getToken()},
      JSON.stringify(message)
    );
  }

  notifyTyping(chatUser: ChatUser): void {

    this.stompClient.send("/app/user.typing",
      {'Authorization': 'Bearer ' + this.keycloakService.getToken()},
      JSON.stringify(chatUser)
    );
  }

  sendUser(user: User): void {
    const chatUser: ChatUser = {
      nickName: user.email || '',
      fullName: user.firstName + ' ' + user.lastName,
      status: 'OFFLINE',
      role: user.role || ''
    }

    this.stompClient.send("/app/user.disconnectUser",
      {'Authorization': 'Bearer ' + this.keycloakService.getToken()},
      JSON.stringify(chatUser)
    );
  }

}
