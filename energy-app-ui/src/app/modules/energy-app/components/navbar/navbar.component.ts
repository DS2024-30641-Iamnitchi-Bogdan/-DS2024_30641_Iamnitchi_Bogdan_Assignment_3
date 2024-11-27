import {Component, OnInit} from '@angular/core';
import {RouterLink, RouterLinkActive} from "@angular/router";
import {NgIf} from "@angular/common";
import {KeycloakService} from "keycloak-angular";
import {KeycloakProfile} from "keycloak-js";
import {User} from "../../../../domain/user-types";
import {Role} from "../../../../domain/emus";
import {ChatService} from "../../../../services/chat/chat.service";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive,
    NgIf
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit {

  profile: User | undefined = undefined;
  isAuthenticated: boolean = false;

  constructor(
    private keycloakService: KeycloakService,
    private chatService: ChatService
  ) {}

  async ngOnInit() {
    this.isAuthenticated = this.keycloakService.isLoggedIn();
    if (this.isAuthenticated) {
      const keycloakProfile = await this.keycloakService.getKeycloakInstance().loadUserProfile();
      this.profile = {
        id: keycloakProfile.id,
        email: keycloakProfile.email,
        firstName: keycloakProfile.firstName,
        lastName: keycloakProfile.lastName,
        role: this.getSingleRole(this.keycloakService.getKeycloakInstance().realmAccess?.roles),
      };
    }
  }

  private getSingleRole(roles?: string[]): Role | undefined {
    if (!roles) return undefined; // Return undefined if no roles provided
    const validRoles = roles.filter(role => role === Role.ADMIN || role === Role.USER);
    return validRoles.length > 0 ? validRoles[0] as Role : undefined; // Return the first valid role
  }

  async login(event: Event) {
    event.preventDefault();
    await this.keycloakService.login();
    this.isAuthenticated = true;
  }

  async logout(event: Event) {
    event.preventDefault();
    await this.keycloakService.logout();
    this.chatService.sendUser(this.profile!);
    this.isAuthenticated = false;
    this.profile = undefined;
  }

  getName(): string {
    return this.profile?.firstName || this.profile?.lastName || this.profile?.email || '';
  }

  isAdmin() {
    return this.profile?.role === Role.ADMIN;
  }

  isUser() {
    return this.profile?.role === Role.USER;
  }
}
