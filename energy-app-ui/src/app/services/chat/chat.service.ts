import { Injectable } from '@angular/core';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ChatMessageRequest, ChatMessageResponse, ChatUser} from "../../domain/chat-types";
import {User} from "../../domain/user-types";
@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private stompClient: any;

  chatURL = environment.chatURL;
  onlineUsersPath = "/users/online";
  messagesPath = "/messages";

  constructor(
    private httpClient: HttpClient
  ) {}


  connect(user: User, onMessageReceived: (message: any) => void, onError: (error: any) => void) {
    const ws = new SockJS(this.chatURL + "/ws");
    this.stompClient = Stomp.over(ws);
    this.stompClient.connect({}, () => this.onConnected(user, onMessageReceived), onError);
  }

  onConnected(user: User, onMessageReceived: (message: any) => void) {
    const chatUser: ChatUser = {
      nickName: user.email || '',
      fullName: user.firstName + ' ' + user.lastName,
      status: 'ONLINE'
    }

    this.stompClient.subscribe(`/user/${chatUser.nickName}/queue/messages`, (message: any) => onMessageReceived(message));
    this.stompClient.subscribe('/topic/users', (message: any) => onMessageReceived(message));
    this.stompClient.send("/app/user.addUser",
      {},
      JSON.stringify(chatUser)
    );
  }

  getAllOnlineUsers(): Observable<ChatUser[]> {
    return this.httpClient.get<ChatUser[]>(`${this.chatURL}${this.onlineUsersPath}`);
  }

  getMessages(senderId: string, recipientId: string): Observable<ChatMessageResponse[]> {
    return this.httpClient.get<ChatMessageResponse[]>(`${this.chatURL}${this.messagesPath}/${senderId}/${recipientId}`);
  }

  sendMessage(message: ChatMessageRequest): void {
    this.stompClient.send("/app/chat",
      {},
      JSON.stringify(message)
    );
  }

}
