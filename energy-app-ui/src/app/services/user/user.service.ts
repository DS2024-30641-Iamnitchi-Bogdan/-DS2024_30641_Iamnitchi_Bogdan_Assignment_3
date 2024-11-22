import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {User} from "../../domain/user-types";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private usersHost = environment.usersURL;
  private usersPath = environment.usersPath;

  constructor(private httpClient: HttpClient) { }

  getAllUsers(): Observable<User[]> {
    const url = `${this.usersHost}${this.usersPath}`;
    return this.httpClient.get<User[]>(url);
  }

  addUser(user: User): Observable<User> {
    const url = `${this.usersHost}${this.usersPath}`;
    return this.httpClient.post<User>(url, user);
  }

  updateUser(user: User): Observable<User> {
    const url = `${this.usersHost}${this.usersPath}/${user.id}`;
    return this.httpClient.put<User>(url, user);
  }

  deleteUser(id: string): Observable<void> {
    const url = `${this.usersHost}${this.usersPath}/${id}`;
    return this.httpClient.delete<void>(url);
  }
}
