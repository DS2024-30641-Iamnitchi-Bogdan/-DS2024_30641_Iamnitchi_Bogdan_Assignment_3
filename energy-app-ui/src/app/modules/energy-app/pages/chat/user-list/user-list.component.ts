import {Component, Input} from '@angular/core';
import {NgClass, NgForOf, NgIf, NgStyle} from "@angular/common";

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
  @Input() users: { name: string, hasUnread: boolean }[] = [];
  selectedUser: string | null = null;

  private colors: string[] = ['#ff6f61', '#6b5b95', '#88b04b', '#f7cac9', '#92a8d1', '#ffb347', '#d5a6bd'];

  getRandomColor(index: number): string {
    return this.colors[index % this.colors.length];
  }

  selectUser(userName: string): void {
    this.selectedUser = userName;

    const user = this.users.find(u => u.name === userName);
    if (user) {
      user.hasUnread = false;
    }
  }
}
