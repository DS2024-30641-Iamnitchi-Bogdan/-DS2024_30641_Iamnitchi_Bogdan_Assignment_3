import {Component, OnInit} from '@angular/core';
import {NgClass, NgForOf} from "@angular/common";
import {User} from "../../../../domain/user-types";
import {UserService} from "../../../../services/user/user.service";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    NgForOf,
    FormsModule,
    NgClass
  ],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss'
})
export class UsersComponent implements OnInit {

  users: User[] = [];
  roles = ['ADMIN', 'USER'];

  passwordVisible: boolean = false;

  private defaultUser: User = {
    id: '',
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    role: undefined
  };

  selectedUser: User = this.defaultUser;

  constructor(
    private userService: UserService
  ) {}

  ngOnInit() {
    this.loadUsers();
    this.selectedUser = this.defaultUser;
  }

  selectUser(user: any) {
    this.selectedUser = { ...user }; // Creates a copy of the user object
    this.passwordVisible = false;
  }

  togglePasswordVisibility() {
    this.passwordVisible = !this.passwordVisible;
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.selectedUser = this.defaultUser;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  refresh() {
    this.selectedUser = this.defaultUser;
    this.loadUsers();
  }

  addUser() {
    this.userService.addUser(this.selectedUser).subscribe({
      next: (user) => {
        this.users.push(user);
        this.selectedUser = this.defaultUser;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  updateUser() {
    this.userService.updateUser(this.selectedUser).subscribe({
      next: (user) => {
        this.users = this.users.map((u) => {
          if (u.id === user.id) {
            return user;
          }
          return u;
        });
        this.selectedUser = this.defaultUser;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  deleteUser() {
    this.userService.deleteUser(this.selectedUser.id || '').subscribe({
      next: () => {
        this.users = this.users.filter((u) => u.id !== this.selectedUser.id);
        this.selectedUser = this.defaultUser;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }
}
