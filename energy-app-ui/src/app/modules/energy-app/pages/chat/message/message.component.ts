import {Component, Input} from '@angular/core';
import {NgClass, NgIf} from "@angular/common";

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
  @Input() message: string = '';
  @Input() isSeen: boolean = false;
  @Input() timestamp: string = '';
  @Input() isMine: boolean = false;
}
