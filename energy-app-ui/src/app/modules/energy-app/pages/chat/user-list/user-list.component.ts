import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgClass, NgForOf, NgIf, NgStyle} from "@angular/common";
import {ChatUser} from "../../../../../domain/chat-types";

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    NgStyle,
    NgClass
  ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent {
  @Input() users: ChatUser[] = [];
  @Output() userSelected = new EventEmitter<ChatUser>();

  selectedUser: ChatUser | null = null;

  private colors: string[] = ['#ff6f61', '#6b5b95', '#88b04b', '#f7cac9', '#92a8d1', '#ffb347', '#d5a6bd', '#FF5733', '#33FF57', '#3357FF', '#F33FF5', '#F5F533'];

  getRandomColor(index: number): string {
    return this.colors[index % this.colors.length];
  }

  selectUser(user: ChatUser): void {
    this.selectedUser = user;
    this.userSelected.emit(user);
  }
}
