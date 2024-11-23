import { Injectable } from '@angular/core';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {KeycloakService} from "keycloak-angular";
import {Observable} from "rxjs";
import {ChatMessage, ChatUser} from "../../domain/chat-types";
@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private stompClient: any;
  user: string | null = null;

  chatURL = environment.chatURL;
  onlineUsersPath = "/users/online";
  messagesPath = "/messages";

  constructor(
    private httpClient: HttpClient,
    private keycloakService: KeycloakService
  ) {}

  connect() {
    const ws = new SockJS(this.chatURL + "/ws");
    this.stompClient = Stomp.over(ws);
    this.stompClient.connect({}, this.onConnected.bind(this), this.onError.bind(this));
  }

  onConnected() {
    console.log('WebSocket connected');
    this.keycloakService.loadUserProfile().then(profile => {
      const chatUser: ChatUser = {
        nickName: profile.username || '',
        fullName: profile.firstName + ' ' + profile.lastName,
        status: 'ONLINE'
      }

      this.stompClient.send("/app/user.addUser",
        {},
        JSON.stringify(chatUser)
      );
      this.stompClient.subscribe('/topic/public', this.onMessageReceived.bind(this));
    });
  }

  onError() {
    console.log('WebSocket error');
  }

  onMessageReceived(payload: any) {
    console.log('Message received:', payload);
  }

  getAllOnlineUsers(): Observable<ChatUser[]> {
    return this.httpClient.get<ChatUser[]>(`${this.chatURL}${this.onlineUsersPath}`);
  }

  getMessages(senderId: string, recipientId: string): Observable<ChatMessage[]> {
    return this.httpClient.get<ChatMessage[]>(`${this.chatURL}${this.messagesPath}/${senderId}/${recipientId}`);
  }

}
