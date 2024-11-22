import { KeycloakService } from "keycloak-angular";
import {environment} from "../../../environments/environment";

export function initializeKeycloak(
  keycloak: KeycloakService
) {
  return () =>
    keycloak.init({
      config: {
        url: environment.keycloakURL,
        realm: environment.keycloakRealm,
        clientId: environment.keycloakClientId,

      },
      initOptions: {
        onLoad: 'check-sso',
        checkLoginIframe: false,
      },
      enableBearerInterceptor: true,
      bearerPrefix: 'Bearer',
      loadUserProfileAtStartUp: true,
    });
}
