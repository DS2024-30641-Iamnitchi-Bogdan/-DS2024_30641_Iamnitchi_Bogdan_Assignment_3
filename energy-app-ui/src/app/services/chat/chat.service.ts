import { Injectable } from '@angular/core';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import {environment} from "../../../environments/environment";
@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private socketClient: any;
  user: string | null = null;

  chatURL = environment.chatURL;

  connect() {
    const ws = new SockJS(this.chatURL + "/ws");
    this.socketClient = Stomp.over(ws);
    this.socketClient.connect({}, () => {
      console.log('Connected to chat server');
    });
  }


}
