import {Injectable, OnDestroy} from '@angular/core';
import {environment} from "../../../environments/environment";
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class WebsocketService implements OnDestroy {

  private readonly baseURL = `ws://${environment.monitorHost}${environment.monitorWsPath}`;

  private webSocket: WebSocket | undefined;
  private messageSubject = new Subject<string>();
  message$ = this.messageSubject.asObservable();

  constructor() {}

  init(userId: string) {
    this.webSocket = new WebSocket(this.baseURL + "?" + `userId=${userId}`);

    this.webSocket.onmessage = (event) => {
      this.receiveMessage(event);
    };

    this.webSocket.onerror = (error) => {
      console.error('WebSocket Error:', error);
    };
  }

  private receiveMessage(event: MessageEvent) {
    this.messageSubject.next(event.data);
  }

  ngOnDestroy() {
    if (this.webSocket) {
      this.webSocket.close();
    }
  }
}
